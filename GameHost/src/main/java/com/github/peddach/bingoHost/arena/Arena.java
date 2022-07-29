package com.github.peddach.bingoHost.arena;

import java.util.ArrayList;
import java.util.Random;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.PortalType;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;

import com.github.peddach.bingoHost.CloudNetAdapter;
import com.github.peddach.bingoHost.GeneralSettings;
import com.github.peddach.bingoHost.events.GameStateChangeEvent;
import com.github.peddach.bingoHost.events.PlayerJoinArenaEvent;
import com.github.peddach.bingoHost.events.PlayerLeaveArenaEvent;
import com.github.peddach.bingoHost.mysql.MySQLManager;
import com.github.peddach.bingoHost.quest.AdvancementList;
import com.github.peddach.bingoHost.quest.BlockList;
import com.github.peddach.bingoHost.quest.Quest;
import com.github.peddach.bingoHost.quest.QuestType;
import com.github.peddach.bingoHost.scoreboard.ScoreboardManager;
import com.github.peddach.bingoHost.teamSelector.TeamGui;
import com.github.peddach.bingoHost.teamSelector.TeamUtil;
import com.github.peddach.bingoHost.util.InventoryUtil;
import com.github.peddach.bingoHost.util.MessageUtil;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;

public class Arena {
	private ArenaMode mode;
	private ArrayList<Player> players = new ArrayList<>();
	private BingoTeam[] teams = new BingoTeam[9];
	private GameState gameState;
	private String name;
	private World world;
	private World nether;
	private MVWorldManager worldManager;
	private MultiverseNetherPortals netherportals;
	private int maxPlayers;
	private GameCountDown countDown;
	private Quest[] quests;
	private TeamGui teamGui;
	private boolean pvp;
	private ArenaGameTimeCounter arenaGameTimeCounter;
	private ScoreboardManager scoreboardManager;

	private static Location spawn = loadSpawnFromConfig();
	private static ArrayList<Arena> arenas = new ArrayList<>();

	public Arena(ArenaMode mode) {
		this.mode = mode;
		pvp = false;
		
		name = getRandomString();
		gameState = GameState.WAITING;

		MultiverseCore mvCore = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		worldManager = mvCore.getMVWorldManager();

		worldManager.addWorld(name + "world", World.Environment.NORMAL, null, WorldType.NORMAL, true, null);
		world = Bukkit.getWorld(name + "world");
		worldManager.addWorld(name + "nether", World.Environment.NETHER, null, WorldType.NORMAL, true, null);
		nether = Bukkit.getWorld(name + "nether");

		world.getWorldBorder().setSize(6000);
		nether.getWorldBorder().setSize(3000);

		netherportals = (MultiverseNetherPortals) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-NetherPortals");
		netherportals.addWorldLink(world.getName(), nether.getName(), PortalType.NETHER);
		netherportals.addWorldLink(nether.getName(), world.getName(), PortalType.NETHER);

		generateQuests();

		if (mode == ArenaMode.SINGLE) {
			for (int i = 0; i < teams.length; i++) {
				teams[i] = new BingoTeam(1, quests, this, TeamUtil.teamMappingsName.get(i), i);
			}
			maxPlayers = 9 * 1;
		}
		if (mode == ArenaMode.TEAM) {
			for (int i = 0; i < teams.length; i++) {
				teams[i] = new BingoTeam(2, quests, this, TeamUtil.teamMappingsName.get(i), i);
			}
			maxPlayers = 9 * 2;
		}
		arenaGameTimeCounter = new ArenaGameTimeCounter(this);
		scoreboardManager = new ScoreboardManager(this);
		arenas.add(this);
		MySQLManager.addArena(this);
		applyGameRules();
		teamGui = new TeamGui(this);
		generateChunksAsync();
	}
	
	public void schedulePvpEnable() {
		Bukkit.getScheduler().runTaskLater(GeneralSettings.plugin, () -> {
			if(gameState == GameState.INGAME) {
				GeneralSettings.plugin.getMessageSender().broadcastMessage(Audience.audience(players), Component.text("PvP").color(NamedTextColor.RED).decorate(TextDecoration.ITALIC).append(Component.text(" wird in 1 Minute aktiviert").color(NamedTextColor.GRAY)));
			}
		}, 20*60*2);
		Bukkit.getScheduler().runTaskLater(GeneralSettings.plugin, () -> {
			if(gameState == GameState.INGAME) {
				pvp = true;
				GeneralSettings.plugin.getMessageSender().broadcastMessage(Audience.audience(players), Component.text("PvP").color(NamedTextColor.RED).decorate(TextDecoration.ITALIC).append(Component.text(" ist nun aktiviert!").color(NamedTextColor.GRAY)));
			}
		}, 20*60*3);	//20 Ticks = 1 Sekunde; 1 Sekunde * 60 = 1 Minute; 1 Minute * 3 = 3 Minuten;
	}
	
	private void generateChunksAsync() {
		for(int x = 0; x < 25; x++) {
			for(int y = 0; y < 25; y++) {
				world.getChunkAtAsync(-12 + x, -12 + y);
			}
		}
	}
	
	private void generateQuests() {
		quests = new Quest[25];
		ArrayList<Material> hardblocks = new ArrayList<>(BlockList.getInstance().getHardBlocks());
		ArrayList<Material> easyblocks = new ArrayList<>(BlockList.getInstance().getEasyBlocks());
		ArrayList<Material> normalblocks = new ArrayList<>(BlockList.getInstance().getNormalBlocks());
		ArrayList<Advancement> advancements = new ArrayList<>(AdvancementList.getInstance().getAdvancements());
		for (int i = 0; i < 25; i++) {
			int randInt = new Random().nextInt(25);
			if(randInt == 6 || randInt == 7 || randInt == 8) {
				Advancement advancement = advancements.get(new Random().nextInt(advancements.size()));
				advancements.remove(advancement);
				quests[i] = new Quest(QuestType.ADCHIEVMENT, advancement);
				continue;
			}
			if(randInt <= 3) {
				Material block = hardblocks.get(new Random().nextInt(hardblocks.size()));
				hardblocks.remove(block);
				quests[i] = new Quest(QuestType.BLOCK, block);
				continue;
			}
			if(randInt >= 18) {
				Material block = normalblocks.get(new Random().nextInt(normalblocks.size()));
				normalblocks.remove(block);
				quests[i] = new Quest(QuestType.BLOCK, block);
				continue;
			}
			Material block = easyblocks.get(new Random().nextInt(easyblocks.size()));
			easyblocks.remove(block);
			quests[i] = new Quest(QuestType.BLOCK, block);
			continue;
		}
	}

	private void applyGameRules() {
		world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
		world.setGameRule(GameRule.DO_FIRE_TICK, false);
		world.setGameRule(GameRule.KEEP_INVENTORY, false);
		world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, true);
		world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
		world.setDifficulty(Difficulty.NORMAL);
		nether.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);

	}

	public void removePlayer(Player player) {
		if (!players.contains(player)) {
			return;
		}
		scoreboardManager.removePlayer(player);
		players.remove(player);
		for(BingoTeam team : teams) {
			if(team.checkIfPlayerIsMember(player)) {
				team.removeMember(player);
				GeneralSettings.plugin.getMessageSender().broadcastMessage(Audience.audience(players), player.displayName().append(Component.text(" hat das Spiel verlassen").color(NamedTextColor.GRAY)));
				break;
			}
		}
		PlayerLeaveArenaEvent event = new PlayerLeaveArenaEvent(this, player);
		Bukkit.getPluginManager().callEvent(event);
	}

	public boolean addPlayer(Player player) {
		if (players.size() == maxPlayers) {
			return false;
		}
		if(gameState == GameState.INGAME || gameState == GameState.ENDING) {
			return false;
		}
		scoreboardManager.addPlayer(player);
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
		scoreboardManager.deleteScordboardManager();
		for (Player player : players) {
			CloudNetAdapter.sendPlayerToLobbyTask(player);
		}
		Bukkit.getScheduler().runTaskLater(GeneralSettings.plugin, () -> {
			netherportals.removeWorldLink(world.getName(), nether.getName(), PortalType.NETHER);
			netherportals.removeWorldLink(nether.getName(), world.getName(), PortalType.NETHER);
			worldManager.deleteWorld(world.getName());
			worldManager.deleteWorld(nether.getName());
		}, 60);
	}

	public void spreadPlayers() {
		
		ArrayList<Player> playerWithoutTeam = new ArrayList<>(players);
		for(BingoTeam bTeam: teams) {
			for(Player player : bTeam.getMembers()) {
				if(player == null) {
					continue;
				}
				playerWithoutTeam.remove(player);
			}
		}
		for(int i = 0; i < teams.length; i++) {
			for(int p = 0; p < playerWithoutTeam.size(); p++) {
				if(teams[i].isFull()) {
					continue;
				}
				if(mode == ArenaMode.SINGLE) {
					if(teams[i].getMembers()[0] == null) {
						teams[i].addMember(playerWithoutTeam.get(p));
						playerWithoutTeam.remove(p);
					}
				}
				if(mode == ArenaMode.TEAM) {
					if(teams[i].getMembers()[0] == null) {
						teams[i].addMember(playerWithoutTeam.get(p));
						playerWithoutTeam.remove(p);
						continue;
					}
					if(teams[i].getMembers()[1] == null) {
						teams[i].addMember(playerWithoutTeam.get(p));
						playerWithoutTeam.remove(p);
						continue;
					}
				}
			}
		}
		
		for (Player player : players) {
			int x = 75 - new Random().nextInt(150);
			int z = 75 - new Random().nextInt(150);
			Location spawn = new Location(world, x + 0.5, world.getHighestBlockYAt(x, z) + 1, z + 0.5);
			player.teleportAsync(spawn);
			player.setBedSpawnLocation(spawn, true);
			InventoryUtil.clearInvOfPlayer(player);
			GeneralSettings.plugin.getMessageSender().sendMessage(player, Component.text("Du wirst gleich teleportiert").color(NamedTextColor.GREEN));
		}
		world.setTime(6000);
	}

	private String getRandomString() {
		int leftLimit = 97; // letter 'a'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 5;
		Random random = new Random();

		String generatedString = random.ints(leftLimit, rightLimit + 1).limit(targetStringLength).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

		return generatedString;
	}

	public void setWinner(BingoTeam team) {
		if (mode == ArenaMode.SINGLE) {
			Title title = MessageUtil.titlebuilder(team.getMembers()[0].getName(), "hat gewonnen", 1000, 5000, 1000);
			for (Player player : players) {
				player.showTitle(title);
			}
			setGameState(GameState.ENDING);
		}
		if (mode == ArenaMode.TEAM) {
			String splayers = "";
			for (Player iplayer : team.getMembers()) {
				splayers = splayers + iplayer.getName() + " ";
			}
			Title title = MessageUtil.titlebuilder(splayers, "hat gewonnen", 1000, 5000, 1000);
			for (Player player : players) {
				player.showTitle(title);
			}
			setGameState(GameState.ENDING);
		}

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

	public BingoTeam[] getTeams() {
		return teams;
	}

	public TeamGui getTeamGui() {
		return teamGui;
	}

	public boolean isPvp() {
		return pvp;
	}

	public void setPvp(boolean pvp) {
		this.pvp = pvp;
	}

	public ArenaGameTimeCounter getArenaGameTimeCounter() {
		return arenaGameTimeCounter;
	}
}
