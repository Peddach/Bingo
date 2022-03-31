package com.github.peddach.bingoHost.arena;

import java.time.Duration;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.github.peddach.bingoHost.GeneralSettings;
import com.github.peddach.bingoHost.listener.LobbyDamageListener;
import com.github.peddach.bingoHost.util.MessageUtil;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;

public class GameCountDown {
	private int taskID;
	private Arena arena;		
	int count = 60;
	
	public GameCountDown(Arena arena) {
		this.arena = arena;
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(GeneralSettings.plugin, () -> {
			if(arena.getPlayers().size() <= 1) {
				for(Player player : arena.getPlayers()) {
					player.showTitle(Title.title(Component.text("Start", NamedTextColor.RED), Component.text("abgebrochen", NamedTextColor.GRAY), Times.times(Duration.ofMillis(500), Duration.ofMillis(4000), Duration.ofMillis(500))));
					player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1F, 1.2F);
					arena.setCountDown(null);
					Bukkit.getScheduler().cancelTask(taskID);
				}
			}
			for(Player player : arena.getPlayers()) {
				showTitle(player, count);
			}
			if(count == 0) {
				startGame();
				return;
			}
			count--;
		}, 20, 20);
	}
	
	private void showTitle(Player player, int countdown) {
		if(countdown == 60) {
			player.showTitle(MessageUtil.titlebuilder("60", "Sekunden", 500, 1500, 500));
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, 1);
		}
		if(countdown == 30) {
			player.showTitle(MessageUtil.titlebuilder("30", "Sekunden", 500, 1500, 500));;
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, 1);
		}
		if(countdown == 10) {
			player.showTitle(MessageUtil.titlebuilder("10", "Sekunden", 500, 1500, 500));
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, 1);
		}
		if(countdown == 5) {
			player.showTitle(MessageUtil.titlebuilder("5", "Sekunden", 100, 1000, 100));
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, 1);
		}
		if(countdown == 4) {
			player.showTitle(MessageUtil.titlebuilder("4", "Sekunden", 100, 1000, 100));
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, 0.9F);
		}
		if(countdown == 3) {
			player.showTitle(MessageUtil.titlebuilder("3", "Sekunden", 100, 1000, 100));
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, 0.8F);
		}
		if(countdown == 2) {
			player.showTitle(MessageUtil.titlebuilder("2", "Sekunden", 100, 1000, 100));
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, 0.7F);
		}
		if(countdown == 1) {;
			player.showTitle(MessageUtil.titlebuilder("1", "Sekunde", 100, 1000, 100));
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, 0.6F);
		}
		if(countdown == 0) {
			player.showTitle(MessageUtil.titlebuilder("Bingo!", "Viel Glück", 500, 2500, 500));
			player.playSound(player.getLocation(), Sound.EVENT_RAID_HORN, 100, 1.2F);
		}
	}
	
	public void startGame() {
		Bukkit.getScheduler().cancelTask(taskID);
		arena.spreadPlayers();
		arena.setGameState(GameState.INGAME);
		for(Player player : arena.getPlayers()) {
			LobbyDamageListener.players.remove(player);
		}
	}
	
	public void setCountDown(int count) {
		if(count > this.count) {
			return;
		}
		this.count = count;
	}
	public int getCountDown() {
		return count;
	}

}
