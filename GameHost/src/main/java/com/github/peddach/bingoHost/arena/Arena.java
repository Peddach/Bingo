package com.github.peddach.bingoHost.arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.PortalType;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import com.github.peddach.bingoHost.GeneralSettings;
import com.github.peddach.bingoHost.events.PlayerJoinArenaEvent;
import com.github.peddach.bingoHost.events.PlayerLeaveArenaEvent;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals;

public class Arena {
	private ArenaMode mode;
	private ArrayList<Player> players;
	private BingoTeam[] teams = new BingoTeam[9];
	private HashMap<Player, Board> boards;
	private GameState gameState;
	private String name;
	private World world;
	private World nether;
	private MVWorldManager worldManager;
	private MultiverseNetherPortals netherportals;
	private int maxPlayers;
	
	private static Location spawn = loadSpawnFromConfig();
	private static ArrayList<Arena> arenas;

	public Arena(ArenaMode mode) {
		this.mode = mode;

		name = "Arena" + new Random().ints(400, 20000);
		setGameState(GameState.WAITING);

		MultiverseCore mvCore = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		worldManager = mvCore.getMVWorldManager();

		worldManager.addWorld(name + "_world", World.Environment.NORMAL, null, WorldType.NORMAL, true, null);
		world = Bukkit.getWorld(name + "_world");
		worldManager.addWorld(name + "_nether", World.Environment.NETHER, null, WorldType.NORMAL, true, null);
		nether = Bukkit.getWorld(name + "_nether");

		netherportals = (MultiverseNetherPortals) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-NetherPortals");
		netherportals.addWorldLink(world.getName(), nether.getName(), PortalType.NETHER);
		netherportals.addWorldLink(nether.getName(), world.getName(), PortalType.NETHER);

		if (mode == ArenaMode.SINGLE) {
			for (int i = 0; i < teams.length; i++) {
				teams[i] = new BingoTeam(1);
			}
			maxPlayers = 9*1;
		}
		if (mode == ArenaMode.TEAM) {
			for (int i = 0; i < teams.length; i++) {
				teams[i] = new BingoTeam(2);
			}
			maxPlayers = 9*2;
		}
		
	}

	public void removePlayer(Player player) {
		players.remove(player);
		
		PlayerLeaveArenaEvent event = new PlayerLeaveArenaEvent(this, player);
		Bukkit.getPluginManager().callEvent(event);
	}

	public boolean addPlayer(Player player) {
		if(players.size() == maxPlayers) {
			return false;
		}
		players.add(player);
		
		PlayerJoinArenaEvent event = new PlayerJoinArenaEvent(this, player);
		Bukkit.getPluginManager().callEvent(event);
		
		return true;
	}
	
	private static Location loadSpawnFromConfig() {
		double x = GeneralSettings.config.getLong("Spawn.X");
		double y = GeneralSettings.config.getLong("String.Y");
		double z = GeneralSettings.config.getLong("Spawn.Z");
		long yaw = GeneralSettings.config.getLong("Spawn.Yaw");
		long pitch = GeneralSettings.config.getLong("Spawn.Pitch");
		World world = Bukkit.getWorld(GeneralSettings.config.getString("Spawn.World"));
		
		return new Location(world, x, y, z, yaw, pitch);
	}

	public void delete() {
		netherportals.removeWorldLink(world.getName(), nether.getName(), PortalType.NETHER);
		netherportals.removeWorldLink(nether.getName(), world.getName(), PortalType.NETHER);
		worldManager.deleteWorld(world.getName());
		worldManager.deleteWorld(nether.getName());
	}

	public static ArrayList<Arena> getArenas() {
		return arenas;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public HashMap<Player, Board> getBoards() {
		return boards;
	}

	public void setBoards(HashMap<Player, Board> boards) {
		this.boards = boards;
	}

	public ArenaMode getMode() {
		return mode;
	}

	public static Location getSpawn() {
		return spawn;
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<Player> getPlayers() {
		return players;
	}
}
