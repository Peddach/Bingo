package com.github.peddach.bingoHost.arena;

import java.util.Random;

import org.bukkit.entity.Player;

import com.github.peddach.bingoHost.quest.Quest;

public class BingoTeam {
	private Player members[];
	private Board board;
	private Arena arena;
	private String name;

	
	public BingoTeam(int size, Quest[] quests, Arena arena) {
		 members = new Player[size];
		 board = new Board(quests, this);
		 this.arena = arena;
		 name = "Team-" + new Random().nextInt(500);
	}
	
	public boolean addMember(Player player) {
		boolean found = false;
		for(int i = 0; i < members.length; i++) {
			if(members[i] == null) {
				members[i] = player;
				found = true;
			}
		}
		return found;
	}
	
	public boolean checkIfPlayerIsMember(Player player) {
		for(int i = 0; i < members.length; i++) {
			if(members[i] == player) {
				return true;
			}
		}
		return false;
	}
	
	public void removeMember(Player player) {
		for(int i = 0; i < members.length; i++) {
			if(members[i] == player) {
				members[0] = null;
			}
		}
	}
	
	public boolean isFull() {
		for(int i = 0; i < members.length; i++) {
			if(members[i] == null) {
				return false;
			}
		}
		return true;
	}

	public Player[] getMembers() {
		return members;
	}

	public Board getBoard() {
		return board;
	}

	public Arena getArena() {
		return arena;
	}
	
	@Override
	public String toString() {
		String string = " ";
		for(Player player : members) {
			if(player == null) {
				string = string + " null ";
			}
			else {
				string = string + player.getName();
			}
		}
		return string;
	}

	public String getName() {
		return name;
	}
	
}
