package com.github.peddach.bingoHost.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.github.peddach.bingoHost.arena.GameState;
import com.github.peddach.bingoHost.events.GameStateChangeEvent;

public class GameStateChangeListener implements Listener{
	
	@EventHandler
	public void onGameStateChangeEvent(GameStateChangeEvent event) {
		if(event.getAfter() == GameState.INGAME) {
			
		}
	}
}
