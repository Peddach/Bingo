package com.github.peddach.bingoHost.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class PortalToOverworldListener implements Listener{
	
	@EventHandler
	public void onPlayerTeleport(PlayerPortalEvent event) {
		if(!event.getTo().getWorld().getName().contains("world")) {
			return;
		}
		event.setCancelled(true);
		if(event.getPlayer().getBedSpawnLocation() != null) {
			event.getPlayer().teleport(event.getPlayer().getBedSpawnLocation());
			return;
		}
		event.getPlayer().teleportAsync(event.getTo().getWorld().getSpawnLocation());
	}

}
