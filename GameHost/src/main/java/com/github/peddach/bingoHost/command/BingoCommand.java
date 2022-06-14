package com.github.peddach.bingoHost.command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.github.peddach.bingoHost.GeneralSettings;
import com.github.peddach.bingoHost.arena.Arena;
import com.github.peddach.bingoHost.arena.BingoTeam;
import com.github.peddach.bingoHost.quest.Quest;
import com.github.peddach.bingoHost.quest.QuestGui;
import com.github.peddach.bingoHost.quest.QuestType;
import com.github.peddach.bingoHost.util.MessageUtil;

import net.kyori.adventure.text.Component;

public class BingoCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player == false) {
			return false;
		}
		Player player = (Player) sender;
		if (!player.hasPermission("Bingo.admin")) {
			return false;
		}
		if (args.length == 0) {
			MessageUtil.sendMessage(player, "§cCommands: list, BlocksToYml, gui, teams, quests, AdvancementsToYml");
		}
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("list")) {
				MessageUtil.sendMessage(player, "Arena | GameState | Players");
				for (Arena arena : Arena.getArenas()) {
					MessageUtil.sendMessage(player, arena.getName() + " | " + arena.getGameState() + " | " + arena.getPlayers().size());
				}
			}
		}
		if (args.length == 1 && args[0].equalsIgnoreCase("teams")) {
			MessageUtil.sendMessage(player, "Teams: ");
			for (Arena arena : Arena.getArenas()) {
				for (Player i : arena.getPlayers()) {
					if (i == player) {
						for (int a = 0; a < arena.getTeams().length; a++) {
							String string = " ";
							for (Player p : arena.getTeams()[a].getMembers()) {
								if (p == null) {
									string = string + "null ";
								} else {
									string = string + p.getName();
								}
							}
							MessageUtil.sendMessage(player, a + " : " + string);
						}
					}
				}
			}
		}
		if (args.length == 1 && args[0].equalsIgnoreCase("quests")) {
			for (Arena arena : Arena.getArenas()) {
				if (arena.getPlayers().contains(player)) {
					for (BingoTeam team : arena.getTeams()) {
						if (team.checkIfPlayerIsMember(player)) {
							for (Quest quest : team.getBoard().getQuest()) {
								if(quest.getType() == QuestType.BLOCK) {
									MessageUtil.sendMessage(player, quest.getType().name() + " : " + quest.getBlock().name());
								}
								if(quest.getType() == QuestType.ADCHIEVMENT) {
									MessageUtil.sendMessage(player, Component.text(quest.getType().name() + " : ").append(quest.getAdvancement().getDisplay().title()));
								}
							}
						}
					}
				}
			}
		}
		if (args.length == 1 && args[0].equalsIgnoreCase("BlocksToYML")) {
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
			for (Material mat : Material.values()) {
				list.add(mat.name());
			}

			blockListConfig.set("Blocks", list);
			try {
				blockListConfig.save(blockList);
			} catch (IOException e) {
				e.printStackTrace();
			}
			player.sendMessage("Blocks to yml sucess");
		}
		if (args.length == 1 && args[0].equalsIgnoreCase("gui")) {
			QuestGui.openGuiForPlayer(player);
		}
		if (args.length == 1 && args[0].equalsIgnoreCase("AdvancementsToYml")) {
			final Iterator<Advancement> iterator = Bukkit.getServer().advancementIterator();
			Bukkit.getServer().getScheduler().runTaskAsynchronously(GeneralSettings.plugin, () -> {
				YamlConfiguration advancementListConfig = new YamlConfiguration();
				List<String> advancementList = new ArrayList<>();
				while (iterator.hasNext()) {
					try {
						Thread.sleep(1L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Advancement advancement = iterator.next();

					advancementList.add(advancement.getKey().getKey());
					player.sendMessage(advancement.getKey().getKey());

				}
				advancementListConfig.set("Advancements", advancementList);
				try {
					advancementListConfig.save(new File(GeneralSettings.plugin.getDataFolder(), "advancements.yml"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			MessageUtil.sendMessage(player, "Advancements sucess");

		}
		return false;

	}

}
