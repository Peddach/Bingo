package com.github.peddach.bingoLobby.join;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.peddach.bingoHost.GeneralSettings;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class PlayerConnector {
	
	private static ArrayList<Player> tempBlackList = new ArrayList<>();

	public PlayerConnector(String arena, Player player) {
		if(tempBlackList.contains(player)) {
			return;
		}
		addToBlackList(player);
		Bukkit.getScheduler().runTaskLater(GeneralSettings.plugin, () -> {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Connect");
			out.writeUTF(arena);
			player.sendPluginMessage(GeneralSettings.plugin, "BungeeCord", out.toByteArray());
		}, 20);
	}
	
	private static void addToBlackList(Player player) {
		tempBlackList.add(player);
		Bukkit.getScheduler().runTaskLater(GeneralSettings.plugin, () -> {
			tempBlackList.remove(player);
		}, 100);
	}
}
