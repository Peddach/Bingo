package com.github.peddach.bingoLobby.join;

import java.util.ArrayList;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.peddach.bingoHost.GeneralSettings;
import com.github.peddach.bingoHost.arena.ArenaMode;
import com.github.peddach.bingoHost.arena.GameState;
import com.github.peddach.bingoHost.mysql.ArenaObject;
import com.github.peddach.bingoHost.mysql.MySQLManager;
import com.github.peddach.bingoHost.util.MessageUtil;

public class ArenaData {
	private static ArrayList<ArenaObject> allArenas = new ArrayList<>();
	private static final ArrayList<Player> PINGLIST = new ArrayList<>();
	private static ArenaObject currentSignleArena;
	private static ArenaObject currentTeamArena;
	
	private static Runnable pull = () -> {
		allArenas = MySQLManager.readArenas();
		checkCurrentSingleArenaAndChoose();
		checkCurrentTeamArenaAndChoose();
	};
	
	public static void init() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(GeneralSettings.plugin, pull, 100, 60);
	}
	private static void showWarning(ArenaMode mode) {
		Bukkit.getScheduler().runTask(GeneralSettings.plugin, ()->{
			for(Player player : Bukkit.getOnlinePlayers()) {
				if(player.hasPermission("Bingo.admin") && PINGLIST.contains(player)) {
					GeneralSettings.plugin.getMessageSender().sendMessage(player, Component.text("Keine freie Quickjoin arena gefunden: " + mode.name()).color(NamedTextColor.RED));
				}
			}
		});
	}
	
	private static void checkCurrentSingleArenaAndChoose() {
		if(currentSignleArena == null) {
			chooseNewSingleArena();
			return;
		}
		for(ArenaObject arena : allArenas) {
			if(arena.getName().equalsIgnoreCase(currentSignleArena.getName())) {
				currentSignleArena = arena;
				break;
			}
		}
		if(!allArenas.contains(currentSignleArena)) {
			chooseNewSingleArena();
			return;
		}
		if(currentSignleArena.getGamestate() == GameState.INGAME || currentSignleArena.getGamestate() == GameState.ENDING) {
			chooseNewSingleArena();
		}
	}
	
	private static void chooseNewSingleArena() {
		for(ArenaObject arena : allArenas) {
			if(arena.getMode() == ArenaMode.SINGLE && (arena.getGamestate() == GameState.STARTING || arena.getGamestate() == GameState.WAITING)) {
				currentSignleArena = arena;
				return;
			}
		}
		currentSignleArena = null;
		showWarning(ArenaMode.SINGLE);
	}
	
	private static void checkCurrentTeamArenaAndChoose() {
		if(currentTeamArena == null) {
			chooseNewTeamArena();
			return;
		}
		for(ArenaObject arena : allArenas) {
			if(arena.getName().equalsIgnoreCase(currentTeamArena.getName())) {
				currentTeamArena = arena;
				break;
			}
		}
		if(!allArenas.contains(currentTeamArena)) {
			chooseNewTeamArena();
			return;
		}
		if(currentTeamArena.getGamestate() == GameState.INGAME || currentTeamArena.getGamestate() == GameState.ENDING) {
			chooseNewTeamArena();
		}
	}
	
	private static void chooseNewTeamArena() {
		for(ArenaObject arena : allArenas) {
			if(arena.getMode() == ArenaMode.TEAM && (arena.getGamestate() == GameState.STARTING || arena.getGamestate() == GameState.WAITING)) {
				currentTeamArena = arena;
				return;
			}
		}
		currentTeamArena = null;
		showWarning(ArenaMode.TEAM);
	}
	
	public static ArenaObject getCurrentSignleArena() {
		return currentSignleArena;
	}
	public static ArenaObject getCurrentTeamArena() {
		return currentTeamArena;
	}
	public static ArrayList<ArenaObject> getAllArenas(){
		return allArenas;
	}
	public static ArrayList<Player> getPinglist() {
		return PINGLIST;
	}
}
