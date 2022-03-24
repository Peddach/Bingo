package com.github.peddach.bingoHost.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.peddach.bingoHost.CloudNetAdapter;
import com.github.peddach.bingoHost.GeneralSettings;
import com.github.peddach.bingoHost.arena.Arena;
import com.github.peddach.bingoHost.mysql.MySQLManager;
import com.github.peddach.bingoHost.util.MessageUtil;

public class PlayerJoinServerListener implements Listener{
	
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		event.joinMessage(null);
		for(Player p : Bukkit.getOnlinePlayers()) {
			event.getPlayer().hidePlayer(GeneralSettings.plugin, p);
			p.hidePlayer(GeneralSettings.plugin, event.getPlayer());
		}
		Bukkit.getScheduler().runTaskAsynchronously(GeneralSettings.plugin, () -> {
			String arena = MySQLManager.readPlayerTeleport(event.getPlayer());
			MySQLManager.deletePlayerFromTeleport(event.getPlayer().getName());
			joinPlayerArenaIfExistsSync(event.getPlayer(), arena);
		});
	}
	
	private void joinPlayerArenaIfExistsSync(Player player, String arena) {
		Bukkit.getScheduler().runTask(GeneralSettings.plugin, () -> {
			for(Arena i : Arena.getArenas()) {
				if(i.getName().equalsIgnoreCase(arena)) {
					if(!i.addPlayer(player)) {
						player.sendMessage("&cDas Spiel welchem du versuchst beizutreten ist voll oder schon gestartet!");
						CloudNetAdapter.sendPlayerToLobbyTask(player);
					}
					LobbyDamageListener.players.add(player);
					return;
				}
			}
			MessageUtil.sendMessage(player, "&cFehler beim Laden der Daten!");
			CloudNetAdapter.sendPlayerToLobbyTask(player);
		});
	}

}
