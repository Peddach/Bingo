package com.github.peddach.bingoHost.quest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.peddach.bingoHost.GeneralSettings;

public class AdvancementList {
	
	private static final AdvancementList advancementList = createInstance();
	private final List<Advancement> advancementsList;
	
	public AdvancementList() {
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(new File(GeneralSettings.plugin.getDataFolder(), "advancements.yml"));
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		List<?> objList = config.getList("Advancements");
		List<String> stringList = new ArrayList<>();
		for(Object object : objList) {
			stringList.add((String) object);
		}
		List<Advancement> allAdvancements = new ArrayList<>();
		Bukkit.advancementIterator().forEachRemaining(allAdvancements::add);
		HashMap<String, Advancement> allAdvancementsAndKeys = new HashMap<>();
		for(Advancement advancement : allAdvancements) {
			allAdvancementsAndKeys.put(advancement.getKey().getKey(), advancement);
		}
		List<Advancement> advancements = new ArrayList<>();
		for(String string : stringList) {
			advancements.add(allAdvancementsAndKeys.get(string));
		}
		advancementsList = advancements;
		
	}
	
	private static AdvancementList createInstance() {
		return new AdvancementList();
	}
	
	public static AdvancementList getInstance() {
		return advancementList;
	}

	public List<Advancement> getAdvancements() {
		return advancementsList;
	}
}
