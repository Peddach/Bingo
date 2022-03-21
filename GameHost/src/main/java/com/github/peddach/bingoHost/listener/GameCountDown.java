package com.github.peddach.bingoHost.listener;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.github.peddach.bingoHost.GeneralSettings;
import com.github.peddach.bingoHost.arena.Arena;
import com.github.peddach.bingoHost.arena.GameState;

public class GameCountDown {
	private int taskID;
	private Arena arena;
	
	public GameCountDown(Arena arena) {
		this.arena = arena;
		int count = 60;
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(GeneralSettings.plugin, () -> {
			if(arena.getPlayers().size() <= 1) {
				for(Player player : arena.getPlayers()) {
					player.sendTitle("§cStart", "§7abgebrochen", 20, 40, 20);
					player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
					Bukkit.getScheduler().cancelTask(taskID);
				}
			}
			for(Player player : arena.getPlayers()) {
				showTitle(player, count);
			}
		}, 20, 20);
	}
	
	private void showTitle(Player player, int countdown) {
		if(countdown == 60) {
			player.sendTitle("§660", "§7Sekunden", 10, 20, 10);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, 1);
		}
		if(countdown == 30) {
			player.sendTitle("§630", "§7Sekunden", 10, 20, 10);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, 1);
		}
		if(countdown == 10) {
			player.sendTitle("§610", "§7Sekunden", 10, 20, 10);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, 1);
		}
		if(countdown == 5) {
			player.sendTitle("§65", "§7Sekunden", 10, 20, 0);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, 1);
		}
		if(countdown == 4) {
			player.sendTitle("§64", "§7Sekunden", 10, 20, 0);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, 0.9F);
		}
		if(countdown == 3) {
			player.sendTitle("§63", "§7Sekunden", 0, 20, 0);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, 0.8F);
		}
		if(countdown == 2) {
			player.sendTitle("§62", "§7Sekunden", 0, 20, 10);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, 0.7F);
		}
		if(countdown == 1) {
			player.sendTitle("§61", "§7Sekunde", 0, 20, 0);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, 0.6F);
		}
		if(countdown == 0) {
			player.sendTitle("§6Bingo!", "§7Viel Glück", 10, 50, 10);
			player.playSound(player.getLocation(), Sound.EVENT_RAID_HORN, 1, 1);
			startGame();
		}
	}
	
	public void startGame() {
		Bukkit.getScheduler().cancelTask(taskID);
		arena.spreadPlayers();
		arena.setGameState(GameState.INGAME);
	}

}
