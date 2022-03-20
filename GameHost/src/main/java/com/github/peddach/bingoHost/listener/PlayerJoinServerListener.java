package com.github.peddach.bingoHost.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.peddach.bingoHost.GeneralSettings;

public class PlayerJoinServerListener implements Listener{
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		for(Player p : Bukkit.getOnlinePlayers()) {
			event.getPlayer().hidePlayer(GeneralSettings.plugin, p);
			p.hidePlayer(GeneralSettings.plugin, event.getPlayer());
		}
	}

}
