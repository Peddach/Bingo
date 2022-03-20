package com.github.peddach.bingoHost.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.github.peddach.bingoHost.arena.Arena;
import com.github.peddach.bingoHost.arena.GameState;

public class GameStateChangeEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();
	private Arena arena;
	private GameState before;
	private GameState after;

	public GameStateChangeEvent(Arena arena, GameState before, GameState after){
		this.arena = arena;
		this.before = before;
		this.after = after;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public Arena getArena() {
		return arena;
	}

	public GameState getBefore() {
		return before;
	}

	public GameState getAfter() {
		return after;
	}

}
