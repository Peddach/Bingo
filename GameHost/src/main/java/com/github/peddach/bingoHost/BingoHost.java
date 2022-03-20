package com.github.peddach.bingoHost;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.peddach.bingoHost.mysql.MySQLManager;

public class BingoHost extends JavaPlugin{
	
	@Override
	public void onEnable() {
		GeneralSettings.plugin = this;
		GeneralSettings.config = getConfig();
		GeneralSettings.servername = CloudNetAdapter.getServerInstanceName();
		
		if(!MySQLManager.setup()) {
			getLogger().warning("Cloud not Connect to database!!!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
	}

}
