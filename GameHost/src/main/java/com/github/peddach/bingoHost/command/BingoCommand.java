package com.github.peddach.bingoHost.command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.github.peddach.bingoHost.GeneralSettings;
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
		if(args.length == 1 && args[0].equalsIgnoreCase("BlocksToYML")) {
	        File blockList;
	        FileConfiguration blockListConfig;
	        blockList = new File(GeneralSettings.plugin.getDataFolder(), "blocks.yml");
	        if (!blockList.exists()) {
	            blockList.getParentFile().mkdirs();
	            GeneralSettings.plugin.saveResource("blocks.yml", false);
	         }

	        blockListConfig = new YamlConfiguration();
	        try {
	            blockListConfig.load(blockList);
	        } catch (IOException | InvalidConfigurationException e) {
	            e.printStackTrace();
	        }
	        
			ArrayList<String> list = new ArrayList<>();
			for(Material mat : Material.values()) {
				list.add(mat.name());
			}
			
			blockListConfig.set("Blocks", list);
			try {
				blockListConfig.save(blockList);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			player.sendMessage("Blocks to yml sucess");
		}
		return false;

	}

}
