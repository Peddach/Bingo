package com.github.peddach.bingoHost.arena;

import org.bukkit.entity.Player;

public class BingoTeam {
	private Player members[];
	
	public BingoTeam(int size) {
		 members = new Player[size];
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
	
}
