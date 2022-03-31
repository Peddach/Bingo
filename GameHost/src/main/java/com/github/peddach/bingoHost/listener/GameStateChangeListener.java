package com.github.peddach.bingoHost.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.github.peddach.bingoHost.GeneralSettings;
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
				player.getInventory().setItem(0, new ItemStack(Material.BREAD, 5));
			}
		}
		if(event.getAfter() == GameState.ENDING) {
			new ScheduledArenaDelete(event.getArena());
			Vector vector = new Vector(0, 4, 0);
			for(Player player : event.getArena().getPlayers()) {
				InventoryUtil.clearInvOfPlayer(player);
				player.setVelocity(vector);
			}
			Bukkit.getScheduler().runTaskLater(GeneralSettings.plugin, () -> {
				for(Player player : event.getArena().getPlayers()) {
					if(!player.isOnline()) {
						continue;
					}
					player.setAllowFlight(true);
					player.setFlying(true);
				}
			}, 20);
			
		}	
	}
}
