package com.github.peddach.bingoHost;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.peddach.bingoHost.arena.Arena;
import com.github.peddach.bingoHost.arena.ArenaMode;
import com.github.peddach.bingoHost.command.BingoCommand;
import com.github.peddach.bingoHost.command.StartCommand;
import com.github.peddach.bingoHost.listener.AdvancementMessageListener;
import com.github.peddach.bingoHost.listener.GameStateChangeListener;
import com.github.peddach.bingoHost.listener.LobbyDamageListener;
import com.github.peddach.bingoHost.listener.PlayerChatListener;
import com.github.peddach.bingoHost.listener.PlayerDeathListener;
import com.github.peddach.bingoHost.listener.PlayerJoinArenaListener;
import com.github.peddach.bingoHost.listener.PlayerJoinServerListener;
import com.github.peddach.bingoHost.listener.PlayerLeaveArenaListener;
import com.github.peddach.bingoHost.listener.PlayerLeaveServerListener;
import com.github.peddach.bingoHost.mysql.MySQLManager;
import com.github.peddach.bingoHost.quest.QuestGui;
import com.github.peddach.bingoHost.quest.QuestListener;
import com.github.peddach.bingoHost.teamSelector.TeamGuiListener;
import com.github.peddach.bingoHost.utilItems.BackpackItem;
import com.github.peddach.bingoHost.utilItems.BingoCard;
import com.github.peddach.bingoHost.utilItems.LeaveItem;
import com.github.peddach.bingoHost.utilItems.StartItem;

public class BingoHost extends JavaPlugin {

	@Override
	public void onEnable() {

		saveDefaultConfig();
		saveConfig();
		reloadConfig();

		GeneralSettings.plugin = this;
		GeneralSettings.config = getConfig();
		GeneralSettings.servername = CloudNetAdapter.getServerInstanceName();
		GeneralSettings.setupFile = getResource("dbsetup.sql");

		if (!MySQLManager.setup()) {
			getLogger().warning("Could not Connect to database!!!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		registerListener();
		createArenas();
		getCommand("Bingo").setExecutor(new BingoCommand());
		getCommand("start").setExecutor(new StartCommand());
	}

	@Override
	public void onDisable() {
		MySQLManager.purgeDatabase();
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
		getServer().getPluginManager().registerEvents(new QuestListener(), this);
		getServer().getPluginManager().registerEvents(new QuestGui(), this);
		getServer().getPluginManager().registerEvents(new TeamGuiListener(), this);
		getServer().getPluginManager().registerEvents(new BingoCard(), this);
		getServer().getPluginManager().registerEvents(new StartItem(), this);
		getServer().getPluginManager().registerEvents(new LeaveItem(), this);
		getServer().getPluginManager().registerEvents(new BackpackItem(), this);
		getServer().getPluginManager().registerEvents(new AdvancementMessageListener(), this);
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
