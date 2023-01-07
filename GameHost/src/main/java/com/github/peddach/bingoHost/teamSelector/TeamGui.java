package com.github.peddach.bingoHost.teamSelector;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.github.peddach.bingoHost.arena.Arena;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class TeamGui {
	
	private final Inventory inv = Bukkit.createInventory(null, 9, Component.text("Wähle dein Team", NamedTextColor.GOLD));
	private final Arena arena;
	public TeamGui(Arena arena) {
		this.arena = arena;
		updateInv();
	}
	
	public void updateInv() {
		for(int i = 0; i < arena.getTeams().length; i++){
			inv.setItem(i, TeamUtil.getTeamBedRepresentation(arena.getTeams()[i]));
		}
	}
	
	public void openForPlayer(Player player) {
		updateInv();
		player.openInventory(inv);
	}
	
	public Inventory getInv() {
		return inv;
	}
}
