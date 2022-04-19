package com.github.peddach.bingoHost.arena;

import java.time.LocalTime;

import org.bukkit.Bukkit;

import com.github.peddach.bingoHost.GeneralSettings;

public class ArenaGameTimeCounter {
	private int seconds = 0;
	private int taskId;
	
	public ArenaGameTimeCounter(Arena arena) {
		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(GeneralSettings.plugin, () -> {
			if(arena.getGameState() == GameState.ENDING) {
				Bukkit.getScheduler().cancelTask(taskId);
				return;
			}
			if(arena.getGameState() == GameState.INGAME) {
				seconds++;
			}
		}, 20, 20);
	}
	
	public String getTimeAsString() {
		LocalTime timeOfDay = LocalTime.ofSecondOfDay(seconds);
		return timeOfDay.toString();
	}
}
