package com.github.peddach.bingoHost;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.peddach.bingoHost.arena.Arena;
import com.github.peddach.bingoHost.arena.ArenaMode;
import com.github.peddach.bingoHost.command.BingoCommand;
import com.github.peddach.bingoHost.listener.GameStateChangeListener;
import com.github.peddach.bingoHost.listener.LobbyDamageListener;
import com.github.peddach.bingoHost.listener.PlayerJoinArenaListener;
import com.github.peddach.bingoHost.listener.PlayerJoinServerListener;
import com.github.peddach.bingoHost.listener.PlayerLeaveArenaListener;
import com.github.peddach.bingoHost.listener.PlayerLeaveServerListener;
import com.github.peddach.bingoHost.mysql.MySQLManager;

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
			getLogger().warning("Cloud not Connect to database!!!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		registerListener();
		createArenas();
		getCommand("Bingo").setExecutor(new BingoCommand());
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
	}

	private void createArenas() {
		for (int i = 0; i < getConfig().getInt("Arenas"); i++) {
			if(i % 2 == 0) {
				new Arena(ArenaMode.SINGLE);
			}
			else {
				new Arena(ArenaMode.TEAM);
			}

		}
	}

}
