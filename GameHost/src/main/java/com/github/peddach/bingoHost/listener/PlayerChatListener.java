package com.github.peddach.bingoHost.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.github.peddach.bingoHost.arena.Arena;

import io.papermc.paper.event.player.AsyncChatEvent;

public class PlayerChatListener implements Listener {

	@EventHandler
	public void playerChatEvent(AsyncChatEvent event) {
		Player player = event.getPlayer();
		Arena arena = null;
		for (Arena iarena : Arena.getArenas()) {
			if (iarena.getPlayers().contains(player)) {
				arena = iarena;
			}
		}
		if(arena == null) {
			event.setCancelled(true);
		}
		event.viewers().clear();
		for(Player i : arena.getPlayers()) {
			event.viewers().add(i);
		}

	}

}
