package com.github.peddach.bingoHost.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
}
