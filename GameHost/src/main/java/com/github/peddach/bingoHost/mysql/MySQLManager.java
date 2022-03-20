package com.github.peddach.bingoHost.mysql;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.Bukkit;

import com.github.peddach.bingoHost.GeneralSettings;
import com.github.peddach.bingoHost.arena.Arena;
import com.mysql.cj.jdbc.MysqlDataSource;

public class MySQLManager {
	private static MysqlDataSource datasource;
	private static InputStream setupFile;
	private static String version = "1_1_1";

	public static Boolean setup() {

		GeneralSettings.plugin.getLogger().info("§2Starting Database Setup");
		try {
			connect();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static void connect() throws SQLException {

		if (GeneralSettings.plugin.getConfig().getBoolean("debug") == true) {
			GeneralSettings.plugin.getLogger().info("MySqlCredentails [Host: " + GeneralSettings.config.getString("Database.Host") + " Port: " + GeneralSettings.config.getInt("Database.Port") + " Database: " + GeneralSettings.config.getString("Database.Database") + " User: "
					+ GeneralSettings.config.getString("Database.User") + " Password: " + GeneralSettings.config.getString("Database.Password") + "]");
		}
		datasource = new MysqlDataSource();

		datasource.setServerName(GeneralSettings.config.getString("Database.Host"));
		datasource.setPortNumber(GeneralSettings.config.getInt("Database.Port"));
		datasource.setDatabaseName(GeneralSettings.config.getString("Database.Database"));
		datasource.setUser(GeneralSettings.config.getString("Database.User"));
		datasource.setPassword(GeneralSettings.config.getString("Database.Password"));
		GeneralSettings.plugin.getLogger().info("§2Connecting to Database");
		try (Connection connection = datasource.getConnection()) {
			if (!connection.isValid(1000)) {
				disableplugin();
				throw new SQLException("Could not establish database connection.");
			}
		}

		try {
			GeneralSettings.plugin.getLogger().info("§2Initialize Tables");
			setupDB();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
			disableplugin();
		}

	}

	private static void setupDB() throws IOException, SQLException {
		String setup;
		try (InputStream in = setupFile) {
			setup = new String(in.readAllBytes());
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		String[] queries = setup.split(";");
		for (String query : queries) {
			if (query.isBlank())
				continue;
			try (Connection conn = datasource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
				stmt.execute();
			}
		}
		GeneralSettings.plugin.getLogger().info("§2Database setup complete.");
	}

	private static void disableplugin() {
		GeneralSettings.plugin.getLogger().warning("Disableing plugins because of a SQL-Exception!");
		Bukkit.broadcastMessage("§2Disable Bingo caused by an Exception!");
		Bukkit.getPluginManager().disablePlugin(GeneralSettings.plugin);
	}

	public static void updateArena(Arena arena) {
		Bukkit.getScheduler().runTaskAsynchronously(GeneralSettings.plugin, () -> {
			try (Connection conn = datasource.getConnection(); PreparedStatement stmt = conn.prepareStatement("REPLACE " + version + "_Arenas(ArenaName, ArenaState, Type, Players, Server) VALUES (?, ?, ?, ?, ?)")) {

				stmt.setString(1, arena.getName());
				stmt.setString(2, arena.getGameState().toString());
				stmt.setString(3, arena.getMode().toString());
				stmt.setInt(4, arena.getPlayers());
				stmt.setString(5, OneVsOne.getServername());
				stmt.execute();

			} catch (SQLException e) {
				e.printStackTrace();
			}

		});
	}

	public static void addArena(Arena arena) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

			@Override
			public void run() {
				try (Connection conn = datasource.getConnection(); PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + version + "_Arenas(ArenaName, ArenaState, Kit, Players, Server) VALUES (?, ?, ?, ?, ?)")) {

					stmt.setString(1, arena.getArenaName());
					stmt.setString(2, arena.getGameState().toString());
					stmt.setString(3, arena.getArenaMap().getKitName());
					stmt.setInt(4, arena.getPlayerCount());
					stmt.setString(5, OneVsOne.getServername());
					stmt.execute();

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		});
	}

	public static void deleteArena(String arenaUUID) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

			@Override
			public void run() {
				try (Connection conn = datasource.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM " + version + "_Arenas WHERE ArenaName = ?;")) {

					stmt.setString(1, arenaUUID);
					stmt.execute();

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		});
	}
}
