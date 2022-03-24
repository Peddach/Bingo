package com.github.peddach.bingoHost.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.peddach.bingoHost.arena.Arena;
import com.github.peddach.bingoHost.util.MessageUtil;

public class BingoCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player == false) {
			return false;
		}
		Player player = (Player) sender;
		if(!player.hasPermission("Bingo.admin")) {
			return false;
		}
		if(args.length == 0) {
			MessageUtil.sendMessage(player, "§cCommands: list, setGameState, delete");
		}
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("list")) {
				MessageUtil.sendMessage(player, "Arena | GameState | Players");
				for(Arena arena : Arena.getArenas()) {
					MessageUtil.sendMessage(player, arena.getName() + " | " + arena.getGameState() + " | " + arena.getPlayers().size());
				}
			}
		}
		return false;
	}

}
