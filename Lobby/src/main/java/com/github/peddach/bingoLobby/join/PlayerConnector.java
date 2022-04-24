package com.github.peddach.bingoLobby.join;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.peddach.bingoHost.GeneralSettings;
import com.github.peddach.bingoHost.mysql.ArenaObject;
import com.github.peddach.bingoHost.mysql.MySQLManager;
import com.github.peddach.bingoHost.util.MessageUtil;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class PlayerConnector {
	
	private static ArrayList<Player> tempBlackList = new ArrayList<>();

	public PlayerConnector(String arena, Player player) {
		if(tempBlackList.contains(player)) {
			return;
		}
		addToBlackList(player);
		if(player.getProtocolVersion() < 758) {
			MessageUtil.sendMessage(player, Component.text("Du nutzt nicht die neuste Version von Minecraft! Bingo unterstützt nur die Version 1.18.2").color(NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD, TextDecoration.ITALIC));
			return;
		}
		for(ArenaObject arenaObj : ArenaData.getAllArenas()) {
			if(arenaObj.getName().equalsIgnoreCase(arena)) {
				Bukkit.getScheduler().runTaskLater(GeneralSettings.plugin, () -> {
					ByteArrayDataOutput out = ByteStreams.newDataOutput();
					out.writeUTF("Connect");
					out.writeUTF(arenaObj.getServer());
					player.sendPluginMessage(GeneralSettings.plugin, "BungeeCord", out.toByteArray());
				}, 20);
				MySQLManager.addPlayerToTeleport(player.getName(), arena, arenaObj.getServer());
				return;
			}
		}
		MessageUtil.sendMessage(player, "§cKeine Arena gefunden: " + arena);
	}
	
	private static void addToBlackList(Player player) {
		tempBlackList.add(player);
		Bukkit.getScheduler().runTaskLater(GeneralSettings.plugin, () -> {
			tempBlackList.remove(player);
		}, 100);
	}
}
