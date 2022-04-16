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
	private final HashMap<Advancement, String> advancementTitleMappings;
	private final HashMap<Advancement, String> advancementDescriptionMappings;
	
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
		HashMap<Advancement, String> advancementTitleMappings = new HashMap<>();
		HashMap<Advancement, String> advancementDescriptionMappings = new HashMap<>();
		for(String string : stringList) {
			String[] splittedString = string.split(" : ");
			if(splittedString.length != 3) {
				GeneralSettings.plugin.getLogger().warning("The Advancement is not well formatted: " + string);
			}
			Advancement advancement = allAdvancementsAndKeys.get(splittedString[0]);
			advancements.add(advancement);
			advancementTitleMappings.put(advancement, splittedString[1]);
			advancementDescriptionMappings.put(advancement, splittedString[2]);
		}
		advancementsList = advancements;
		this.advancementDescriptionMappings = advancementDescriptionMappings;
		this.advancementTitleMappings = advancementTitleMappings;
		
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

	public HashMap<Advancement, String> getAdvancementTitleMappings() {
		return advancementTitleMappings;
	}

	public HashMap<Advancement, String> getAdvancementDescriptionMappings() {
		return advancementDescriptionMappings;
	}
}
