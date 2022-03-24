package com.github.peddach.bingoLobby.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.peddach.bingoHost.mysql.ArenaObject;
import com.github.peddach.bingoHost.util.MessageUtil;
import com.github.peddach.bingoLobby.join.ArenaData;
import com.github.peddach.bingoLobby.join.PlayerConnector;

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
				MessageUtil.sendMessage(player, "§cSingle null");
			} else {
				MessageUtil.sendMessage(player, "§cSinge: " + ArenaData.getCurrentSignleArena().getName());
			}
			if (ArenaData.getCurrentTeamArena() == null) {
				MessageUtil.sendMessage(player, "§cTeam null");
			} else {
				MessageUtil.sendMessage(player, "§cTeam: " + ArenaData.getCurrentSignleArena().getName());
			}
			return false;
		}
		if (args.length == 1 && args[0].equalsIgnoreCase("ListVisible")) {
			MessageUtil.sendMessage(player, "§7Alle sichbaren Arenen:");
			for (ArenaObject arena : ArenaData.getArenas()) {
				MessageUtil.sendMessage(player, arena.getName() + " | " + arena.getGamestate().name() + " | " + arena.getPlayers());
			}
			return false;
		}
		if (args.length == 1 && args[0].equalsIgnoreCase("ListAll")) {
			MessageUtil.sendMessage(player, "§7Alle Arenen (auch eigentlich unsichtbare):");
			for (ArenaObject arena : ArenaData.getAllArenas()) {
				MessageUtil.sendMessage(player, arena.getName() + " | " + arena.getGamestate().name() + " | " + arena.getPlayers());
			}
			return false;
		}
		if (args.length >= 1 && args[0].equalsIgnoreCase("join")) {
			if (args.length == 2) {
				new PlayerConnector(args[1], player);
			} else {
				MessageUtil.sendMessage(player, "§cNutze: join [arena]");
			}
			return false;
		}
		MessageUtil.sendMessage(player, "§cCommand nicht gefunden!");
		return false;
	}

}