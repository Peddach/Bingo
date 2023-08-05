package com.github.peddach.bingoHost.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.peddach.bingoHost.GeneralSettings;
import com.github.peddach.bingoHost.arena.Arena;

import java.util.HashMap;
import java.util.UUID;

public class PlayerJoinServerListener implements Listener{
	
	private final HashMap<UUID, String> joiningPlayers = new HashMap<>();

	public PlayerJoinServerListener(){
		GeneralSettings.plugin.getCloudNetAdapter().setJoinRequestResolver((id, uuid) -> {
			for(Arena arena : Arena.getArenas()){
				if(arena.getName().equals(id) && arena.getPlayers().size() < arena.getMaxPlayers()){
					joiningPlayers.put(uuid, id);
					Bukkit.getScheduler().runTaskLater(GeneralSettings.plugin, () -> joiningPlayers.remove(uuid), 20*6);
					return true;
				}
			}
			return false;
		});
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		event.joinMessage(null);
		for(Player p : Bukkit.getOnlinePlayers()) {
			event.getPlayer().hidePlayer(GeneralSettings.plugin, p);
			p.hidePlayer(GeneralSettings.plugin, event.getPlayer());
		}
		String arena = joiningPlayers.get(event.getPlayer().getUniqueId());
		if(arena == null){
			GeneralSettings.plugin.getMessageUtil().sendMessage(event.getPlayer(), Component.text("Das hat nicht geklappt!", NamedTextColor.RED));
			GeneralSettings.plugin.getCloudNetAdapter().sendPlayerToLobby(event.getPlayer());
			return;
		}
		joiningPlayers.remove(event.getPlayer().getUniqueId());
		for(Arena i : Arena.getArenas()) {
			if(i.getName().equalsIgnoreCase(arena)) {
				if(!i.addPlayer(event.getPlayer())) {
					event.getPlayer().sendMessage("&cDas Spiel welchem du versuchst beizutreten ist voll oder schon gestartet!");
					GeneralSettings.plugin.getCloudNetAdapter().sendPlayerToLobby(event.getPlayer());
					return;
				}
				LobbyDamageListener.players.add(event.getPlayer());
				return;
			}
		}

	}

}
