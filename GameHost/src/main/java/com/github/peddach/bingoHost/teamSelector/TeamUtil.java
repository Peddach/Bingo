package com.github.peddach.bingoHost.teamSelector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class TeamUtil implements Listener {

	public static HashMap<Integer, String> teamMappingsName = loadTeamNameMappings();
	public static HashMap<Integer, Material> teamMappingsBeds = loadTeamBedMappings();

	private static HashMap<Integer, String> loadTeamNameMappings() {
		HashMap<Integer, String> mappings = new HashMap<>();
		mappings.put(0, "Orange");
		mappings.put(1, "Lila");
		mappings.put(2, "Blau");
		mappings.put(3, "Rot");
		mappings.put(4, "Grün");
		mappings.put(5, "Hellblau");
		mappings.put(6, "Gelb");
		mappings.put(7, "Magenta");
		mappings.put(8, "Hellgrün");
		mappings.put(9, "Pink");
		return mappings;
	}

	private static HashMap<Integer, Material> loadTeamBedMappings() {
		HashMap<Integer, Material> mappings = new HashMap<>();
		mappings.put(0, Material.ORANGE_BED);
		mappings.put(1, Material.PURPLE_BED);
		mappings.put(2, Material.BLUE_BED);
		mappings.put(3, Material.RED_BED);
		mappings.put(4, Material.GREEN_BED);
		mappings.put(5, Material.LIGHT_BLUE_BED);
		mappings.put(6, Material.YELLOW_BED);
		mappings.put(7, Material.MAGENTA_BED);
		mappings.put(8, Material.LIME_BED);
		mappings.put(9, Material.PINK_BED);
		return mappings;
	}

	public static ItemStack getTeamItem() {
		ItemStack item = new ItemStack(Material.GREEN_BED, 1);
		final ItemMeta meta = item.getItemMeta();
		List<Component> lore = new ArrayList<>();
		lore.add(Component.text(" "));
		lore.add(Component.text("Wähle dein Team", NamedTextColor.GRAY));
		lore.add(Component.text(" "));
		meta.lore(lore);
		meta.displayName(Component.text("Wähle dein Team", NamedTextColor.GOLD));
		item.setItemMeta(meta);
		return item;
	}
}
