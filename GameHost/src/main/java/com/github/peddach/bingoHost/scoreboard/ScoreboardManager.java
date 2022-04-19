package com.github.peddach.bingoHost.scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.peddach.bingoHost.GeneralSettings;
import com.github.peddach.bingoHost.arena.Arena;
import com.github.peddach.bingoHost.arena.ArenaMode;
import com.github.peddach.bingoHost.arena.BingoTeam;
import com.github.peddach.bingoHost.arena.GameState;

import fr.mrmicky.fastboard.FastBoard;

public class ScoreboardManager {
	
	private HashMap<Player, FastBoard> playerBoardMap = new HashMap<>();
	private int taskID;
	
	public ScoreboardManager(final Arena arena) {
		Runnable updateTask = () -> {
			if(arena.getGameState() == GameState.WAITING) {
				for(FastBoard fastBoard : playerBoardMap.values()) {
					fastBoard.updateLines(
							" ",
							"§7Warte auf weitere",
							"§7Spieler",
							" "
							);
				}
			}
			if(arena.getGameState() == GameState.STARTING){
				int maxPlayer = 9;
				if(arena.getMode() == ArenaMode.TEAM) {
					maxPlayer = 18;
				}
				int secoundToStart = 60;
				if(arena.getCountDown() != null) {
					secoundToStart = arena.getCountDown().getCountDown() + 1;
				}
				for(FastBoard fastboard : playerBoardMap.values()) {
					fastboard.updateLines(
							" ",
							"§a§lSpielstart",
							"§7>> " + secoundToStart + " Sekunden",
							" ",
							"§a§lSpieler",
							"§7>> " + arena.getPlayers().size() + "/" + maxPlayer
							);
				}
			}
			if(arena.getGameState() == GameState.INGAME) {
				for(BingoTeam team : arena.getTeams()) {
					for(Player player : team.getMembers()) {
						if(player == null) {
							continue;
						}
						FastBoard fastboard = playerBoardMap.get(player);
						List<String> lines = new ArrayList<>();
						lines.add(" ");
						lines.add("§a§lDeine Aufgaben");
						for(String string : getQuestRowsFromTeam(team)) {
							lines.add(string);
						}
						lines.add(" ");
						lines.add("§a§lZeit");
						lines.add("§7>> " + arena.getArenaGameTimeCounter().getTimeAsString());
						lines.add(" ");
						fastboard.updateLines(lines);
					}
				}
			}
			if(arena.getGameState() == GameState.ENDING) {
				for(Player player : arena.getPlayers()) {
					FastBoard fastboard = playerBoardMap.get(player);
					fastboard.updateLines(
							" ",
							"§a§lZeit:",
							"§7>> " + arena.getArenaGameTimeCounter().getTimeAsString(),
							" "
							);
				}
			}
		};
		
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(GeneralSettings.plugin, updateTask, 20, 20);
		
	}
	
	private List<String> getQuestRowsFromTeam(BingoTeam team) {
		String rowsCombined = "";
		for(boolean quest : team.getBoard().getQuests()) {
			if(quest) {
				rowsCombined = rowsCombined + "§2█";
				continue;
			}
			rowsCombined = rowsCombined + "§7█";
		}
		List<String> rows = new ArrayList<>();
		String row = "";
		String[] splillted = rowsCombined.split("");
		for(int i = 0; i < splillted.length; i++) {
			if((i % 15) == 0) {
				rows.add(row);
				row = "";
			}
			row = row + splillted[i];
			if(i == (splillted.length - 1)) {
				rows.add(row);
			}
		}
		return rows;
	}
	
	public void addPlayer(Player player) {
		FastBoard fastboard = new FastBoard(player);
		fastboard.updateTitle("§6§lPetropia.de");
		playerBoardMap.put(player, fastboard);
	}
	
	public void removePlayer(Player player) {
		if(!playerBoardMap.get(player).isDeleted()) {
			playerBoardMap.get(player).delete();
		}
		playerBoardMap.remove(player);
	}
	
	public void deleteScordboardManager() {
		Bukkit.getScheduler().cancelTask(taskID);
		for(FastBoard fastboard : playerBoardMap.values()) {
			if(!fastboard.isDeleted()) {
				fastboard.delete();
			}
		}
		playerBoardMap.clear();
	}
	
}
