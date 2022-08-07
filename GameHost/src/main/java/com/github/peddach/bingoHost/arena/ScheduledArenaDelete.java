package com.github.peddach.bingoHost.arena;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
					GeneralSettings.plugin.getMessageUtil().sendMessage(player, Component.text("Der Server stoppt in ").color(NamedTextColor.RED)
							.append(Component.text(count).color(NamedTextColor.GOLD))
							.append(Component.text(" Sekunden").color(NamedTextColor.RED)));
				}
				else {
					GeneralSettings.plugin.getMessageUtil().sendMessage(player, Component.text("Der Server stoppt jetzt! Du wirst zurück in die Lobby teleportiert").color(NamedTextColor.RED));
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
