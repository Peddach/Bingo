package com.github.peddach.bingoHost.util;

import java.time.Duration;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;

public class MessageUtil {

	public static Title titlebuilder(String title, String subtitle, int fadein, int stay, int fadeout) {
		return Title.title(Component.text(title, NamedTextColor.GOLD), Component.text(subtitle, NamedTextColor.GRAY), Times.times(Duration.ofMillis(fadein), Duration.ofMillis(stay), Duration.ofMillis(fadeout)));
	}
	
}
