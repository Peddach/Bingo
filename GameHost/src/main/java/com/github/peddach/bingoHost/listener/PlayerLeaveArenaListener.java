package com.github.peddach.bingoHost.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.github.peddach.bingoHost.arena.GameState;
import com.github.peddach.bingoHost.events.PlayerLeaveArenaEvent;
import com.github.peddach.bingoHost.mysql.MySQLManager;

public class PlayerLeaveArenaListener implements Listener{
	
	@EventHandler
	public void onPlayerLeaveArena(PlayerLeaveArenaEvent event) {
		MySQLManager.updateArena(event.getArena());
		if(event.getArena().getPlayers().size() == 1 && event.getArena().getGameState() == GameState.INGAME) {
			event.getArena().setGameState(GameState.ENDING);
		}
		
	}
}
