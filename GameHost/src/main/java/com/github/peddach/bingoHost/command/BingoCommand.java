package com.github.peddach.bingoHost.command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.kyori.adventure.text.format.NamedTextColor;
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

import net.kyori.adventure.text.Component;

public class BingoCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player player)) {
			return false;
		}
		if (!player.hasPermission("Bingo.admin")) {
			return false;
		}
		if (args.length == 0) {
			GeneralSettings.plugin.getMessageUtil().sendMessage(player, Component.text("Commands: list, BlocksToYml, gui, teams, quests, AdvancementsToYml").color(NamedTextColor.GRAY));
		}
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("list")) {
				GeneralSettings.plugin.getMessageUtil().sendMessage(player, Component.text("Arena | GameState | Players").color(NamedTextColor.GRAY));
				for (Arena arena : Arena.getArenas()) {
					GeneralSettings.plugin.getMessageUtil().sendMessage(player, Component.text(arena.getName() + " | " + arena.getGameState() + " | " + arena.getPlayers().size()).color(NamedTextColor.GRAY));
				}
			}
		}
		if (args.length == 1 && args[0].equalsIgnoreCase("teams")) {
			GeneralSettings.plugin.getMessageUtil().sendMessage(player, Component.text("Teams: ").color(NamedTextColor.GRAY));
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
							GeneralSettings.plugin.getMessageUtil().sendMessage(player, Component.text(a + " : " + string).color(NamedTextColor.GRAY));
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
									GeneralSettings.plugin.getMessageUtil().sendMessage(player, Component.text(quest.getType().name() + " : " + quest.getBlock().name()).color(NamedTextColor.GRAY));
								}
								if(quest.getType() == QuestType.ADCHIEVMENT) {
									GeneralSettings.plugin.getMessageUtil().sendMessage(player, Component.text(quest.getType().name() + " : ").append(quest.getAdvancement().getDisplay().title()));
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
			GeneralSettings.plugin.getMessageUtil().sendMessage(player, Component.text("Advancements erfolgreich konvertiert").color(NamedTextColor.GREEN));
		}
		return false;

	}

}
