package com.github.peddach.bingoHost.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.github.peddach.bingoHost.GeneralSettings;
import com.github.peddach.bingoHost.arena.Arena;
import com.github.peddach.bingoHost.arena.ArenaMode;
import com.github.peddach.bingoHost.arena.GameCountDown;
import com.github.peddach.bingoHost.arena.GameState;
import com.github.peddach.bingoHost.events.PlayerJoinArenaEvent;
import com.github.peddach.bingoHost.mysql.MySQLManager;
import com.github.peddach.bingoHost.util.InventoryUtil;

import io.papermc.paper.text.PaperComponents;

public class PlayerJoinArenaListener implements Listener{
	
	@EventHandler
	public void onPlayerJoinArenaEvent(PlayerJoinArenaEvent event) {
		event.getPlayer().teleport(Arena.getSpawn());
		event.getArena().broadcastMessage(PaperComponents.plainTextSerializer().serialize(event.getPlayer().displayName()) + " &7ist dem Spiel beigetreten");
		InventoryUtil.clearInvOfPlayer(event.getPlayer());
		for(Player i : event.getArena().getPlayers()) {
			i.showPlayer(GeneralSettings.plugin, event.getPlayer());
			event.getPlayer().showPlayer(GeneralSettings.plugin, i);
		}
		if(event.getArena().getGameState() == GameState.WAITING) {
			event.getArena().setGameState(GameState.STARTING);
		}
		if(event.getArena().getPlayers().size() == 2 && event.getArena().getMode() == ArenaMode.SINGLE) {
			new GameCountDown(event.getArena());
		}
		if(event.getArena().getPlayers().size() == 3 && event.getArena().getMode() == ArenaMode.TEAM) {
			new GameCountDown(event.getArena());
		}
		MySQLManager.updateArena(event.getArena());
	}
}
