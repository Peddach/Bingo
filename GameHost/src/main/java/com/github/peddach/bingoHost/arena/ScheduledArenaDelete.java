package com.github.peddach.bingoHost.arena;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.peddach.bingoHost.GeneralSettings;
import com.github.peddach.bingoHost.util.MessageUtil;

public class ScheduledArenaDelete {
	
	private int count = 15;
	private int taskId;
	
	public ScheduledArenaDelete(Arena arena) {
		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(GeneralSettings.plugin, () -> {
			for(Player player : arena.getPlayers()) {
				if(count >= 1) {
					MessageUtil.sendMessage(player, "§cDer Server stoppt in &6" + count + " §cSekunden");
				}
				else {
					MessageUtil.sendMessage(player, "§cDer Server stoppt jetzt! Du wirst zurück in die Lobby teleportiert");
				}
			}
			if(count == 0) {
				arena.delete();
				Bukkit.getScheduler().cancelTask(taskId);
				new Arena(arena.getMode());
			}
			count --;
		}, 20, 20);
	}
}
