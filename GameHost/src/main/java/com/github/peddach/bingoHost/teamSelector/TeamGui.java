package com.github.peddach.bingoHost.teamSelector;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.peddach.bingoHost.arena.Arena;
import com.github.peddach.bingoHost.arena.ArenaMode;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class TeamGui {
	
	private Inventory inv = Bukkit.createInventory(null, 9, Component.text("Wähle dein Team", NamedTextColor.GOLD));
	private Arena arena;
	public TeamGui(Arena arena) {
		this.arena = arena;
		updateInv();
	}
	
	public void updateInv() {
		List<ItemStack> beds = getBeds();
		for(int i = 0; i < 9; i++) {
			inv.setItem(i, beds.get(i));
		}
	}
	
	private List<ItemStack> getBeds() {
		List<ItemStack> items = new ArrayList<>();
		if(arena.getMode() == ArenaMode.SINGLE) {
			for(int i = 0; i < arena.getTeams().length; i++) {
				ItemStack item = new ItemStack(TeamUtil.teamMappingsBeds.get(i), 1);
				Component name = Component.text(TeamUtil.teamMappingsName.get(i), NamedTextColor.GOLD);
				final ItemMeta meta = item.getItemMeta();
				meta.displayName(name);
				if(arena.getTeams()[i].getMembers()[0] == null) {
					List<Component> lore = new ArrayList<>();
					lore.add(Component.text(" "));
					lore.add(Component.text("Leer", NamedTextColor.GRAY));
					lore.add(Component.text(" "));
					meta.lore(lore);
					item.setItemMeta(meta);
					items.add(item);
				}
				else {
					List<Component> lore = new ArrayList<>();
					lore.add(Component.text(" "));
					lore.add(Component.text(arena.getTeams()[i].getMembers()[0].getName(), NamedTextColor.GRAY));
					lore.add(Component.text(" "));
					meta.lore(lore);
					item.setItemMeta(meta);
					items.add(item);
				}
			}
			return items;
		}
		if(arena.getMode() == ArenaMode.TEAM) {
			for(int i = 0; i < arena.getTeams().length; i++) {
				ItemStack item = new ItemStack(TeamUtil.teamMappingsBeds.get(i), 1);
				Component name = Component.text(TeamUtil.teamMappingsName.get(i), NamedTextColor.GOLD);
				final ItemMeta meta = item.getItemMeta();
				meta.displayName(name);
				List<Component> lore = new ArrayList<>();
				lore.add(Component.text(" "));
				if(arena.getTeams()[i].getMembers()[0] != null) {
					lore.add(Component.text(arena.getTeams()[i].getMembers()[0].getName(), NamedTextColor.GRAY));
				}
				if(arena.getTeams()[i].getMembers()[1] != null) {
					lore.add(Component.text(arena.getTeams()[i].getMembers()[1].getName(), NamedTextColor.GRAY));
				}
				if(arena.getTeams()[i].getMembers()[0] != null && arena.getTeams()[i].getMembers()[1] != null) {
					lore.add(Component.text("Leer", NamedTextColor.GRAY));
				}
				lore.add(Component.text(" "));
				meta.lore(lore);
				item.setItemMeta(meta);
				items.add(item);
			}
			return items;
		}
		return null;
	}
	
	public void openForPlayer(Player player) {
		updateInv();
		player.openInventory(inv);
	}
	
	public Inventory getInv() {
		return inv;
	}
}
