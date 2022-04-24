package com.github.peddach.bingoHost.util;

import java.time.Duration;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;

public class MessageUtil {
	public static void sendMessage(Player player, String message) {
		sendMessage(player, message, true);
	}
	
	public static void sendMessage(Player player, String message, boolean showprefix) {
		if(showprefix) {
			message = "§7[§6Bingo§7] " + message;
		}
		message = ChatColor.translateAlternateColorCodes('&', message);
		player.sendMessage(message);
	}
	
	public static void sendMessage(Player player, Component component) {
		Component prefix = Component.text("[").color(NamedTextColor.GRAY).append(Component.text("Bingo").color(NamedTextColor.GOLD).append(Component.text("] ").color(NamedTextColor.GRAY)));
		player.sendMessage(prefix.append(component));
	}
	
	public static Title titlebuilder(String title, String subtitle, int fadein, int stay, int fadeout) {
		return Title.title(Component.text(title, NamedTextColor.GOLD), Component.text(subtitle, NamedTextColor.GRAY), Times.times(Duration.ofMillis(fadein), Duration.ofMillis(stay), Duration.ofMillis(fadeout)));
	}
	
}
