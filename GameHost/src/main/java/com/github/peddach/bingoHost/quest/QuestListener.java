package com.github.peddach.bingoHost.quest;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.peddach.bingoHost.arena.Arena;
import com.github.peddach.bingoHost.arena.BingoTeam;
import com.github.peddach.bingoHost.arena.GameState;
import com.github.peddach.bingoHost.events.InventoryChangeEvent;

public class QuestListener implements Listener {

	@EventHandler
	public void onPickUp(EntityPickupItemEvent event) {
		if (event.getEntity()instanceof Player player) {
			Bukkit.getPluginManager().callEvent(new InventoryChangeEvent(event.getItem().getItemStack(), player));
		}
	}

	@EventHandler
	public void onCraftEvent(CraftItemEvent event) {
		Bukkit.getPluginManager().callEvent(new InventoryChangeEvent(event.getCurrentItem(), (Player) event.getWhoClicked()));
	}
	
	@EventHandler
	public void onMilkEvent(PlayerInteractEvent event) {
		Bukkit.getPluginManager().callEvent(new InventoryChangeEvent(event.getPlayer().getInventory().getItemInMainHand(), event.getPlayer()));
		Bukkit.getPluginManager().callEvent(new InventoryChangeEvent(event.getPlayer().getInventory().getItemInOffHand(), event.getPlayer()));
	}

	@EventHandler
	public void onInvChangeEvent(InventoryChangeEvent event) {
		Arena arena = null;
		BingoTeam team = null;
		for (Arena i : Arena.getArenas()) {
			if (i.getPlayers().contains(event.getPlayer())) {
				arena = i;
				for (BingoTeam ii : arena.getTeams()) {
					if (ii.checkIfPlayerIsMember(event.getPlayer())) {
						team = ii;
					}
				}
			}
		}
		if (arena == null || team == null) {
			return;
		}
		if (arena.getGameState() != GameState.INGAME) {
			return;
		}
		if(event.getItem() == null) {
			return;
		}
		for (int i = 0; i < team.getBoard().getQuest().length; i++) {
			if (team.getBoard().getQuest()[i].getType() == QuestType.BLOCK) {
				if (team.getBoard().getQuest()[i].getBlock() == event.getItem().getType()) {
					if(team.getBoard().getQuests()[i] == false) {
						event.getItem().setAmount(event.getItem().getAmount() - 1);
						team.getBoard().setSucess(i);
						return;
					}
				}
			} 
		}
	}
}
