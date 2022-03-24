package com.github.peddach.bingoHost.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.github.peddach.bingoHost.arena.GameState;
import com.github.peddach.bingoHost.arena.ScheduledArenaDelete;
import com.github.peddach.bingoHost.events.GameStateChangeEvent;
import com.github.peddach.bingoHost.mysql.MySQLManager;
import com.github.peddach.bingoHost.util.InventoryUtil;

public class GameStateChangeListener implements Listener{
	
	@EventHandler
	public void onGameStateChangeEvent(GameStateChangeEvent event) {
		MySQLManager.updateArena(event.getArena());
		if(event.getAfter() == GameState.INGAME) {
			for(Player player : event.getArena().getPlayers()) {
				player.getInventory().setItem(5, new ItemStack(Material.BREAD, 0));
			}
		}
		if(event.getAfter() == GameState.ENDING) {
			new ScheduledArenaDelete(event.getArena());
			for(Player player : event.getArena().getPlayers()) {
				InventoryUtil.clearInvOfPlayer(player);
				player.setAllowFlight(true);
				player.setFlying(true);
			}
		}	
	}
}
