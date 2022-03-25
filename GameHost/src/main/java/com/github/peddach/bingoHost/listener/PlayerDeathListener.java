package com.github.peddach.bingoHost.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.github.peddach.bingoHost.arena.Arena;

public class PlayerDeathListener implements Listener{
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		event.deathMessage(null);
		for(Arena arena : Arena.getArenas()) {
			if(arena.getPlayers().contains(event.getPlayer())) {
				arena.broadcastMessage("§7" + event.getPlayer().getName() + " §7ist gestorben");
			}
		}
	}
}
