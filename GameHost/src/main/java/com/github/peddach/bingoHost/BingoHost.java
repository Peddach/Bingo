package com.github.peddach.bingoHost;

import com.github.peddach.bingoHost.listener.*;
import de.petropia.turtleServer.api.PetropiaPlugin;

import com.github.peddach.bingoHost.arena.Arena;
import com.github.peddach.bingoHost.arena.ArenaMode;
import com.github.peddach.bingoHost.command.BingoCommand;
import com.github.peddach.bingoHost.command.StartCommand;
import com.github.peddach.bingoHost.quest.QuestGui;
import com.github.peddach.bingoHost.quest.RecipeShow;
import com.github.peddach.bingoHost.quest.AdvancememtQuestListener;
import com.github.peddach.bingoHost.quest.BlockQuestListener;
import com.github.peddach.bingoHost.teamSelector.TeamGuiListener;
import com.github.peddach.bingoHost.utilItems.BackpackItem;
import com.github.peddach.bingoHost.utilItems.BingoCard;
import com.github.peddach.bingoHost.utilItems.LeaveItem;
import com.github.peddach.bingoHost.utilItems.StartItem;

public class BingoHost extends PetropiaPlugin {

	@Override
	public void onEnable() {

		saveDefaultConfig();
		saveConfig();
		reloadConfig();

		GeneralSettings.plugin = this;
		GeneralSettings.config = getConfig();
		GeneralSettings.servername = getCloudNetAdapter().getServerInstanceName();

		registerListener();
		createArenas();
		getCommand("Bingo").setExecutor(new BingoCommand());
		getCommand("start").setExecutor(new StartCommand());
	}

	@Override
	public void onDisable() {
		Arena.getArenas().forEach(a -> getCloudNetAdapter().publishArenaDelete(a.getName()));
	}

	private void registerListener() {
		getServer().getPluginManager().registerEvents(new GameStateChangeListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerJoinArenaListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerJoinServerListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerLeaveArenaListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerLeaveServerListener(), this);
		getServer().getPluginManager().registerEvents(new LobbyDamageListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
		getServer().getPluginManager().registerEvents(new BlockQuestListener(), this);
		getServer().getPluginManager().registerEvents(new QuestGui(), this);
		getServer().getPluginManager().registerEvents(new TeamGuiListener(), this);
		getServer().getPluginManager().registerEvents(new BingoCard(), this);
		getServer().getPluginManager().registerEvents(new StartItem(), this);
		getServer().getPluginManager().registerEvents(new LeaveItem(), this);
		getServer().getPluginManager().registerEvents(new BackpackItem(), this);
		getServer().getPluginManager().registerEvents(new PvpListener(), this);
		getServer().getPluginManager().registerEvents(new AdvancememtQuestListener(), this);
		getServer().getPluginManager().registerEvents(new RecipeShow(), this);
		getServer().getPluginManager().registerEvents(new PortalToOverworldListener(), this);
		getServer().getPluginManager().registerEvents(new ArenaRepublishListener(), this);
	}

	private void createArenas() {
		for (int i = 0; i < getConfig().getInt("Arenas"); i++) {
			if (i % 2 == 0) {
				new Arena(ArenaMode.SINGLE);
			} else {
				new Arena(ArenaMode.TEAM);
			}

		}
	}

}
