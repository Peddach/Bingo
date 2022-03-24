package com.github.peddach.bingoLobby.join;

import java.util.ArrayList;

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
	private static ArrayList<ArenaObject> visibleArenas = new ArrayList<>();
	private static ArenaObject currentSignleArena;
	private static ArenaObject currentTeamArena;
	
	private static Runnable pull = () -> {
		allArenas = MySQLManager.readArenas();
		chooseNewCurrenQuickJoinArenaSingle();
		chooseNewCurrenQuickJoinArenaTeam();
		visibleArenas = allArenas;
		if(currentSignleArena != null) {
			visibleArenas.remove(currentSignleArena);
		}
		if(currentTeamArena != null) {
			visibleArenas.remove(currentTeamArena);
		}
	};
	
	public static void init() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(GeneralSettings.plugin, pull, 100, 60);
	}
	private static void showWarning(ArenaMode mode) {
		Bukkit.getScheduler().runTask(GeneralSettings.plugin, ()->{
			for(Player player : Bukkit.getOnlinePlayers()) {
				if(player.hasPermission("Bingo.admin")) {
					MessageUtil.sendMessage(player, "§cKeine freie Quickjoin arena gefunden: " + mode.name());
				}
			}
		});
	}
	
	private static void chooseNewCurrenQuickJoinArenaSingle() {
		if((allArenas.contains(currentSignleArena) && (currentSignleArena.getGamestate() == GameState.WAITING || currentSignleArena.getGamestate() == GameState.STARTING))) {
			return;
		}
		for(ArenaObject arena : allArenas) {
			if(arena.getPlayers() == 0 && arena.getGamestate() == GameState.WAITING) {
				currentSignleArena = arena;
				return;
			}
		}
		currentSignleArena = null;
		showWarning(ArenaMode.SINGLE);
	}
	
	private static void chooseNewCurrenQuickJoinArenaTeam() {
		if((allArenas.contains(currentTeamArena) && (currentTeamArena.getGamestate() == GameState.WAITING || currentTeamArena.getGamestate() == GameState.STARTING))) {
			return;
		}
		for(ArenaObject arena : allArenas) {
			if(arena.getPlayers() <= 1 && arena.getGamestate() == GameState.WAITING) {
				currentTeamArena = arena;
				return;
			}
		}
		currentTeamArena = null;
		showWarning(ArenaMode.TEAM);
	}
	
	public static ArrayList<ArenaObject> getArenas() {
		return visibleArenas;
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
}
