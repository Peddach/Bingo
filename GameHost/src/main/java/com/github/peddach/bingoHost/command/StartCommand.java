package com.github.peddach.bingoHost.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.github.peddach.bingoHost.arena.Arena;
import com.github.peddach.bingoHost.util.MessageUtil;

public class StartCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(sender instanceof Player == false) {
			return false;
		}
		Player player = (Player) sender;
		if(!player.hasPermission("Bingo.start")) {
			MessageUtil.sendMessage(player, "§cDazu hast du keine Rechte");
		}
		
		for(Arena arena : Arena.getArenas()) {
			for(Player i : arena.getPlayers()) {
				if(i == player) {
					if(arena.getCountDown() == null) {
						MessageUtil.sendMessage(player, "§cEs läuft grade kein Countdown");
						return false;
					}
					if(arena.getCountDown().getCountDown() < 15) {
						MessageUtil.sendMessage(player, "§cDu kannst jetzt nicht starten");
						return false;
					}
					arena.getCountDown().setCountDown(15);
					arena.broadcastMessage("§7Der Countdown wird verkürzt durch " + player.getName());				}
			}
		}
		return false;
	}

}
