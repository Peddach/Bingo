package com.github.peddach.bingoLobby.commands;

import com.github.peddach.bingoHost.GeneralSettings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.peddach.bingoHost.mysql.ArenaObject;
import com.github.peddach.bingoHost.util.MessageUtil;
import com.github.peddach.bingoLobby.join.ArenaData;
import com.github.peddach.bingoLobby.join.PlayerConnector;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class BingoLobbyCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player == false) {
			return false;
		}
		Player player = (Player) sender;
		if (!player.hasPermission("Bingo.admin")) {
			return false;
		}
		if (args.length == 1 && args[0].equalsIgnoreCase("ListQuick")) {
			if (ArenaData.getCurrentSignleArena() == null) {
				GeneralSettings.plugin.getMessageSender().sendMessage(player, Component.text("Single null"));
			} else {
				GeneralSettings.plugin.getMessageSender().sendMessage(player, Component.text("Single: " + ArenaData.getCurrentSignleArena().getName()));
			}
			if (ArenaData.getCurrentTeamArena() == null) {
				GeneralSettings.plugin.getMessageSender().sendMessage(player, Component.text("Team null"));
			} else {
				GeneralSettings.plugin.getMessageSender().sendMessage(player, Component.text("Team: " + ArenaData.getCurrentTeamArena().getName()));
			}
			return false;
		}
		if (args.length == 1 && args[0].equalsIgnoreCase("List")) {
			GeneralSettings.plugin.getMessageSender().sendMessage(player, Component.text("Alle Arenen:"));
			for (ArenaObject arena : ArenaData.getAllArenas()) {
				GeneralSettings.plugin.getMessageSender().sendMessage(player, Component.text(arena.getName() + " | " + arena.getGamestate().name() + " | " + arena.getPlayers()));
			}
			return false;
		}
		if (args.length > 0 && args[0].equalsIgnoreCase("join")) {
			if (args.length == 2) {
				new PlayerConnector(args[1], player);
			} else {
				GeneralSettings.plugin.getMessageSender().sendMessage(player, Component.text("Nutze: join [arena]"));
			}
			return false;
		}
		if(args.length == 1 && args[0].equalsIgnoreCase("ping")) {
			if(ArenaData.getPinglist().contains(player)) {
				GeneralSettings.plugin.getMessageSender().sendMessage(player, Component.text("Du wurdest von der Pingliste entfernt").color(NamedTextColor.GRAY));
				ArenaData.getPinglist().remove(player);
			}
			else {
				GeneralSettings.plugin.getMessageSender().sendMessage(player, Component.text("Du wurdest zur Pingliste hinzugefügt").color(NamedTextColor.GRAY));
				ArenaData.getPinglist().add(player);
			}
			return false;
		}
		GeneralSettings.plugin.getMessageSender().sendMessage(player, Component.text("Command nicht gefunden!").color(NamedTextColor.RED));
		return false;
	}

}
