package com.github.peddach.bingoHost.arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.PortalType;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import com.github.peddach.bingoHost.CloudNetAdapter;
import com.github.peddach.bingoHost.GeneralSettings;
import com.github.peddach.bingoHost.events.GameStateChangeEvent;
import com.github.peddach.bingoHost.events.PlayerJoinArenaEvent;
import com.github.peddach.bingoHost.events.PlayerLeaveArenaEvent;
import com.github.peddach.bingoHost.mysql.MySQLManager;
import com.github.peddach.bingoHost.util.InventoryUtil;
import com.github.peddach.bingoHost.util.MessageUtil;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals;

public class Arena {
	private ArenaMode mode;
	private ArrayList<Player> players = new ArrayList<>();
	private BingoTeam[] teams = new BingoTeam[9];
	private HashMap<Player, Board> boards;
	private GameState gameState;
	private String name;
	private World world;
	private World nether;
	private MVWorldManager worldManager;
	private MultiverseNetherPortals netherportals;
	private int maxPlayers;
	private GameCountDown countDown;
	
	private static Location spawn = loadSpawnFromConfig();
	private static ArrayList<Arena> arenas = new ArrayList<>();

	public Arena(ArenaMode mode) {
		this.mode = mode;

		name = getRandomString();
		gameState = GameState.WAITING;

		MultiverseCore mvCore = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		worldManager = mvCore.getMVWorldManager();
		
		worldManager.addWorld(name + "world", World.Environment.NORMAL, null, WorldType.NORMAL, true, null);
		world = Bukkit.getWorld(name + "world");
		worldManager.addWorld(name + "nether", World.Environment.NETHER, null, WorldType.NORMAL, true, null);
		nether = Bukkit.getWorld(name + "nether");
		
		world.getWorldBorder().setSize(10000);
		nether.getWorldBorder().setSize(10000);

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
		
		arenas.add(this);
		MySQLManager.addArena(this);
		
	}
	
	private void applyGameRules() {
		world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
		world.setGameRule(GameRule.DO_FIRE_TICK, false);
		world.setGameRule(GameRule.KEEP_INVENTORY, false);
		world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, true);
		world.setDifficulty(Difficulty.NORMAL);

		
	}

	public void removePlayer(Player player) {
		if(!players.contains(player)) {
			return;
		}
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
		double y = GeneralSettings.config.getLong("Spawn.Y");
		double z = GeneralSettings.config.getLong("Spawn.Z");
		long yaw = GeneralSettings.config.getLong("Spawn.Yaw");
		long pitch = GeneralSettings.config.getLong("Spawn.Pitch");
		World world = Bukkit.getWorld(GeneralSettings.config.getString("Spawn.World"));
		
		return new Location(world, x, y, z, yaw, pitch);
	}

	public void delete() {
		
		MySQLManager.deleteArena(name);
		arenas.remove(this);
		
		for(Player player : players) {
			CloudNetAdapter.sendPlayerToLobbyTask(player);
		}
		Bukkit.getScheduler().runTaskLater(GeneralSettings.plugin, () -> {
			netherportals.removeWorldLink(world.getName(), nether.getName(), PortalType.NETHER);
			netherportals.removeWorldLink(nether.getName(), world.getName(), PortalType.NETHER);
			worldManager.deleteWorld(world.getName());
			worldManager.deleteWorld(nether.getName());
		}, 60);
	}
	
	public void broadcastMessage(String message) {
		for(Player player : players) {
			MessageUtil.sendMessage(player, message);
		}
	}
	
	public void spreadPlayers() {
		for(Player player : players) {
			int x = 50 - new Random().nextInt(100);
			int z = 50 - new Random().nextInt(100);
			Location spawn = new Location(world, x + 0.5, world.getHighestBlockYAt(x, z) + 1, z + 0.5);
			player.teleportAsync(spawn);
			player.setBedSpawnLocation(spawn, true);
			InventoryUtil.clearInvOfPlayer(player);
			MessageUtil.sendMessage(player, "§2Du wirst gleich teleportiert");
		}
		world.setTime(6000);
	}
	
	private String getRandomString() {
		 int leftLimit = 97; // letter 'a'
		    int rightLimit = 122; // letter 'z'
		    int targetStringLength = 5;
		    Random random = new Random();

		    String generatedString = random.ints(leftLimit, rightLimit + 1)
		      .limit(targetStringLength)
		      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
		      .toString();

		  return generatedString;
	}

	public static ArrayList<Arena> getArenas() {
		return arenas;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		GameStateChangeEvent event = new GameStateChangeEvent(this, this.gameState, gameState);
		Bukkit.getPluginManager().callEvent(event);
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
	
	public World getWorld() {
		return world;
	}

	public GameCountDown getCountDown() {
		return countDown;
	}

	public void setCountDown(GameCountDown countDown) {
		this.countDown = countDown;
	}
}
