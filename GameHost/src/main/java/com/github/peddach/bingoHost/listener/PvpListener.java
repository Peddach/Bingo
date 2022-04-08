package com.github.peddach.bingoHost.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.peddach.bingoHost.arena.Arena;

public class PvpListener implements Listener{
	
	@EventHandler
	public void onPlayerAttackPlayerEvent(EntityDamageByEntityEvent event) {
		if(event.getEntity() instanceof Player == false) {
			return;
		}
		if(event.getDamager() instanceof Player == false) {
			return;
		}
		Player player = (Player) event.getEntity();
		for(Arena arena : Arena.getArenas()) {
			if(!arena.getPlayers().contains(player)) {
				continue;
			}
			if(arena.isPvp()) {
				return;
			}
			event.setCancelled(true);
		}
	}

}
