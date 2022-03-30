package com.github.peddach.bingoHost.quest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.peddach.bingoHost.GeneralSettings;

public class BlockList {

	private static final ArrayList<Material> blockList = loadBlockList();

	private static File blocksConfigFile;
	private static FileConfiguration blocksConfig;

	public static ArrayList<Material> loadBlockList() {
		blocksConfigFile = new File(GeneralSettings.plugin.getDataFolder(), "blocks.yml");
		
		if (!blocksConfigFile.exists()) {
			GeneralSettings.plugin.getLogger().warning("No blocks.yml found!");
			Bukkit.getServer().getPluginManager().disablePlugin(GeneralSettings.plugin);
		}

		blocksConfig = new YamlConfiguration();
		try {
			blocksConfig.load(blocksConfigFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		List<?> objlist = blocksConfig.getList("Blocks");
		ArrayList<String> stringList = new ArrayList<>();
		objlist.forEach(object -> stringList.add((String)object));
		ArrayList<Material> matList = new ArrayList<>();
		stringList.forEach(string -> matList.add(Material.valueOf(string)));
		
		return matList;
		
	}

	public static ArrayList<Material> getBlockList() {
		return blockList;
	}
}
