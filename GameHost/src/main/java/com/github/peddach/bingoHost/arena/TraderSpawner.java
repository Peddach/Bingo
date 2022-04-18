package com.github.peddach.bingoHost.arena;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.github.peddach.bingoHost.GeneralSettings;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class TraderSpawner {
	
	private Arena arena;
	private int taskID;
	private final Runnable task = () -> {
		if(!Arena.getArenas().contains(arena) || arena.getGameState() != GameState.INGAME) {
			Bukkit.getScheduler().cancelTask(taskID);
			return;
		}
		arena.broadcastMessage(Component.text("Ein Trader ist bei dir gespawned!").color(NamedTextColor.GRAY));
		for(Player player : arena.getPlayers()) {
			player.getWorld().spawnEntity(player.getLocation(), EntityType.WANDERING_TRADER);
		}
	};
	
	public TraderSpawner(Arena arena) {
		this.arena = arena;
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(GeneralSettings.plugin, task, 20*60*15, 20*60*15);
	}
}
