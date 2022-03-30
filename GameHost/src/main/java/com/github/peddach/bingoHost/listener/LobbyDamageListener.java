package com.github.peddach.bingoHost.listener;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.github.peddach.bingoHost.arena.Arena;

public class LobbyDamageListener implements Listener {

	public static ArrayList<Player> players = new ArrayList<>();

	@EventHandler
	private void onPlayerDamage(EntityDamageEvent event) {
		if (event.getEntity()instanceof Player player) {
			if(event.getEntity().getLocation().getWorld().getName().equalsIgnoreCase("world")) {
				event.setCancelled(true);
				return;
			}
			if (!players.contains(player)) {
				return;
			}
			event.setCancelled(true);
			if (event.getCause() == DamageCause.VOID) {
				player.teleport(Arena.getSpawn());
			}
		}

	}

	@EventHandler
	private void onPlayerHunger(FoodLevelChangeEvent event) {
		if (event.getEntity()instanceof Player player) {
			if (!players.contains(player)) {
				return;
			}
			event.setCancelled(true);
		}
	}

	@EventHandler
	private void onPlayerBreak(BlockBreakEvent event) {
		if(event.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase("world")) {
			event.setCancelled(true);
			return;
		}
		if (!players.contains(event.getPlayer())) {
			return;
		}
		event.setCancelled(true);
	}
	
	@EventHandler
	private void onPlayerPlace(BlockPlaceEvent event) {
		if(event.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase("world")) {
			event.setCancelled(true);
			return;
		}
		if (!players.contains(event.getPlayer())) {
			return;
		}
		event.setCancelled(true);
	}

}
