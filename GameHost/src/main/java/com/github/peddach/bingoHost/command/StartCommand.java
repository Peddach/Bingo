package com.github.peddach.bingoHost.command;

import com.github.peddach.bingoHost.GeneralSettings;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.github.peddach.bingoHost.arena.Arena;
import com.github.peddach.bingoHost.util.MessageUtil;

import java.awt.*;

public class StartCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(sender instanceof Player == false) {
			return false;
		}
		Player player = (Player) sender;
		if(!player.hasPermission("Bingo.start")) {
			GeneralSettings.plugin.getMessageUtil().sendMessage(player, Component.text("Dazu hast du keine Rechte").color(NamedTextColor.RED));
		}
		for(Arena arena : Arena.getArenas()) {
			for(Player i : arena.getPlayers()) {
				if(i == player) {
					if(arena.getCountDown() == null) {
						GeneralSettings.plugin.getMessageUtil().sendMessage(player, Component.text("Es läuft grade kein Countdown").color(NamedTextColor.RED));
						return false;
					}
					if(arena.getCountDown().getCountDown() < 10) {
						GeneralSettings.plugin.getMessageUtil().sendMessage(player, Component.text("Du kannst jetzt nicht starten").color(NamedTextColor.RED));
						return false;
					}
					if(arena.getCountDown().isForceStarted()) {
						GeneralSettings.plugin.getMessageUtil().sendMessage(player, Component.text("Der Countdown wurde bereits verkürzt").color(NamedTextColor.RED));
						return false;
					}
					arena.getCountDown().setCountDown(11);
					GeneralSettings.plugin.getMessageUtil().broadcastMessage(Audience.audience(arena.getPlayers()), player.displayName().append(Component.text(" hat den Countdown verkürzt").color(NamedTextColor.GRAY)));
				}
			}
		}
		return false;
	}

}
