package com.github.peddach.bingoHost;

import com.github.peddach.bingoHost.listener.*;
import de.petropia.turtleServer.api.PetropiaPlugin;

import com.github.peddach.bingoHost.arena.Arena;
import com.github.peddach.bingoHost.arena.ArenaMode;
import com.github.peddach.bingoHost.command.BingoCommand;
import com.github.peddach.bingoHost.command.StartCommand;
import com.github.peddach.bingoHost.mysql.MySQLManager;
import com.github.peddach.bingoHost.quest.QuestGui;
import com.github.peddach.bingoHost.quest.RecipeShow;
import com.github.peddach.bingoHost.quest.AdvancememtQuestListener;
import com.github.peddach.bingoHost.quest.BlockQuestListener;
import com.github.peddach.bingoHost.teamSelector.TeamGuiListener;
import com.github.peddach.bingoHost.utilItems.BackpackItem;
import com.github.peddach.bingoHost.utilItems.BingoCard;
import com.github.peddach.bingoHost.utilItems.LeaveItem;
import com.github.peddach.bingoHost.utilItems.StartItem;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

public class BingoHost extends PetropiaPlugin {
	@Override
	public void onEnable() {
		GeneralSettings.plugin = this;
		copyDatapack();
		Bukkit.getScheduler().runTask(this, this::onEnablePostWorld);
	}

	private void onEnablePostWorld(){
		saveDefaultConfig();
		saveConfig();
		reloadConfig();

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
	}

	private void copyDatapack(){
		//Load default world name from server.properties
		this.getLogger().info("Loading Datapack");
		String levelName;
		try (InputStream inputStream = new FileInputStream(new File(Bukkit.getPluginsFolder().getParentFile(), "server.properties"))) {
			Properties properties = new Properties();
			properties.load(inputStream);
			levelName = properties.getProperty("level-name");
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		if(levelName == null || levelName.isBlank() || levelName.isEmpty()){
			this.getLogger().warning("Can't load server.properties");
			return;
		}
		File datapackDir = new File(new File(Bukkit.getWorldContainer(), levelName), "datapacks");
		datapackDir.mkdirs();
		try(InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("Bingo.zip")){
			if(inputStream == null){
				GeneralSettings.plugin.getLogger().warning("Can not load Bingo.zip datapack from resources!");
				return;
			}
			Files.copy(inputStream, new File(datapackDir, "Bingo.zip").toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
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
