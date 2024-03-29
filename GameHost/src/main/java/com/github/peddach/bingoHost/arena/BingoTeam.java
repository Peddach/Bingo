package com.github.peddach.bingoHost.arena;

import com.github.peddach.bingoHost.teamSelector.TeamUtil;
import de.petropia.turtleServer.server.prefix.PrefixManager;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.github.peddach.bingoHost.quest.Quest;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class BingoTeam {
	private final Player[] members;
	private final Board board;
	private final Arena arena;
	private final String name;
	private final int number;
	private final Inventory backpack = Bukkit.createInventory(null, 9, Component.text("Rucksack").color(NamedTextColor.DARK_RED));

	
	public BingoTeam(int size, Quest[] quests, Arena arena, String name, int number) {
		 members = new Player[size];
		 board = new Board(quests, this);
		 this.arena = arena;
		 this.name = name;
		 this.number = number;
	}
	
	public boolean addMember(Player player) {
		boolean found = false;
		for(int i = 0; i < members.length; i++) {
			if(members[i] == null) {
				members[i] = player;
				found = true;
				PrefixManager.getInstance().setPlayerNameColor(TextColor.fromCSSHexString(TeamUtil.teamMappingsNamedTextColor.get(number)), player);
				break;
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
				members[i] = null;
				PrefixManager.getInstance().resetPlayerNameColor(player);
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

	public boolean isEmpty(){
		for (Player member : members) {
			if (member != null) {
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

	public Inventory getBackpack() {
		return backpack;
	}

	public int getNumber() {
		return number;
	}
	
}
