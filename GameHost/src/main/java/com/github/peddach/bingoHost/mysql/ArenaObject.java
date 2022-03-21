package com.github.peddach.bingoHost.mysql;

import com.github.peddach.bingoHost.arena.ArenaMode;
import com.github.peddach.bingoHost.arena.GameState;

public class ArenaObject {
	private String name;
	private ArenaMode mode;
	private int players;
	private GameState gamestate;
	private String server;
	
	public String getName() {
		return name;
	}

	public ArenaMode getMode() {
		return mode;
	}

	public int getPlayers() {
		return players;
	}

	public GameState getGamestate() {
		return gamestate;
	}

	public String getServer() {
		return server;
	}

	public ArenaObject(String name, ArenaMode mode, int players, GameState gamestate, String server) {
		super();
		this.name = name;
		this.mode = mode;
		this.players = players;
		this.gamestate = gamestate;
		this.server = server;
	}
}
