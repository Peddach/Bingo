package com.github.peddach.bingoLobby;

import de.petropia.turtleServer.api.PetropiaPlugin;

import com.github.peddach.bingoHost.GeneralSettings;
import com.github.peddach.bingoHost.mysql.MySQLManager;
import com.github.peddach.bingoLobby.commands.BingoLobbyCommand;
import com.github.peddach.bingoLobby.join.ArenaData;
import com.github.peddach.bingoLobby.listener.EnterPortalListener;

public class BingoLobby extends PetropiaPlugin {
	
	@Override
	public void onEnable() {
		
		saveDefaultConfig();
		saveConfig();
		reloadConfig();
		
		GeneralSettings.plugin = this;
		GeneralSettings.config = this.getConfig();
		GeneralSettings.servername = "LobbyInstance";
		GeneralSettings.setupFile = this.getResource("setupdb.sql");
		
		if(MySQLManager.setup() == false) {
			getServer().getPluginManager().disablePlugin(this);
		}
		
		this.getCommand("BingoLobby").setExecutor(new BingoLobbyCommand());
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		
		getServer().getPluginManager().registerEvents(new EnterPortalListener(), this);
		
		ArenaData.init();
	}
}
