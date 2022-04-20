package com.github.peddach.bingoHost.quest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import com.github.peddach.bingoHost.GeneralSettings;

public class BlockList {
	
	private final static BlockList BLOCKLIST = new BlockList();
	
	private List<Material> allBlocks;
	private List<Material> easyBlocks;
	private List<Material> normalBlocks;
	private List<Material> hardBlocks;
	
	private BlockList() {
		File file = new File(GeneralSettings.plugin.getDataFolder(), "blocks.yml");
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		easyBlocks = loadList(config, "Leicht");
		hardBlocks = loadList(config, "Schwer");
		hardBlocks = loadList(config, "Mittel");
		allBlocks = new ArrayList<>();
		allBlocks.addAll(easyBlocks);
		allBlocks.addAll(hardBlocks);
		allBlocks.addAll(normalBlocks);
	}
	
	private List<Material> loadList(YamlConfiguration config, String listName) {
		@Nullable List<?> objList = config.getList(listName);
		List<String> stringList = new ArrayList<>();
		objList.forEach(s -> stringList.add((String) s));
		List<Material> materialList = new ArrayList<>();
		stringList.forEach(s -> materialList.add(Material.valueOf(s)));
		return materialList;
	}
	

	public List<Material> getAllBlocks() {
		return allBlocks;
	}

	public List<Material> getEasyBlocks() {
		return easyBlocks;
	}

	public List<Material> getHardBlocks() {
		return hardBlocks;
	}
	
	public List<Material> getNormalBlocks() {
		return normalBlocks;
	}

	public static BlockList getInstance() {
		return BLOCKLIST;
	}
}
