package com.github.peddach.bingoHost.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import com.github.peddach.bingoHost.arena.Arena;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class AdvancementMessageListener implements Listener {

	@EventHandler
	public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
		if(event.getAdvancement().getDisplay() == null) {
			return;
		}
		if (!event.getAdvancement().getDisplay().doesAnnounceToChat()) {
			return;
		}
		for (Arena arena : Arena.getArenas()) {
			if (!arena.getPlayers().contains(event.getPlayer())) {
				continue;
			}
			Component name = event.getAdvancement().getDisplay().title().color(NamedTextColor.GOLD);
			Component message = Component.text(event.getPlayer().getName()).color(NamedTextColor.GRAY).append(Component.text(" hat das Advancement ").color(NamedTextColor.GRAY)).append(name).append(Component.text(" erhalten").color(NamedTextColor.GRAY));
			arena.broadcastMessage(message);
			break;
		}
	}
}
