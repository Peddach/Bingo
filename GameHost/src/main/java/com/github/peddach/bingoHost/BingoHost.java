package com.github.peddach.bingoHost;

import org.bukkit.plugin.java.JavaPlugin;

public class BingoHost extends JavaPlugin{
	
	@Override
	public void onEnable() {
		GeneralSettings.plugin = this;
		GeneralSettings.config = getConfig();
	}

}
