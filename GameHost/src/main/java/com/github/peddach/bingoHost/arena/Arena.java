package com.github.peddach.bingoHost.arena;

import java.util.ArrayList;
import java.util.Random;

import de.petropia.turtleServer.api.worlds.WorldManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;


public class Arena {
	private final ArenaMode mode;
	private final ArrayList<Player> players = new ArrayList<>();
	private final BingoTeam[] teams = new BingoTeam[9];
	private GameState gameState;
	private final String name;
	private final World world;
	private final World nether;
	private int maxPlayers;
	private GameCountDown countDown;
	private Quest[] quests;
	private final TeamGui teamGui;
	private boolean pvp;
	private ArenaGameTimeCounter arenaGameTimeCounter;
	private final ScoreboardManager scoreboardManager;

	private static final Location SPAWN = loadSpawnFromConfig();
	private static final ArrayList<Arena> ARENAS = new ArrayList<>();

	public Arena(ArenaMode mode) {
		this.mode = mode;
		pvp = false;
		
		name = getRandomString();
		gameState = GameState.WAITING;

		WorldCreator worldCreator = new WorldCreator(name + "_world");
		worldCreator.environment(World.Environment.NORMAL);
		worldCreator.generateStructures(true);
		worldCreator.keepSpawnLoaded(TriState.FALSE);
		worldCreator.type(WorldType.NORMAL);
		worldCreator.generateStructures(true);
		world = worldCreator.createWorld();
		WorldCreator netherCreator = new WorldCreator(name + "_nether");
		netherCreator.environment(World.Environment.NETHER);
		netherCreator.generateStructures(true);
		netherCreator.keepSpawnLoaded(TriState.FALSE);
		netherCreator.type(WorldType.NORMAL);
		netherCreator.generateStructures(true);
		nether = netherCreator.createWorld();
		world.getWorldBorder().setSize(4000);
		nether.getWorldBorder().setSize(1000);
		WorldManager.linkWorlds(world, nether);

		generateQuests();

		if (mode == ArenaMode.SINGLE) {
			for (int i = 0; i < teams.length; i++) {
				teams[i] = new BingoTeam(1, quests, this, TeamUtil.teamMappingsName.get(i), i);
			}
			maxPlayers = 9;
		}
		if (mode == ArenaMode.TEAM) {
			for (int i = 0; i < teams.length; i++) {
				teams[i] = new BingoTeam(2, quests, this, TeamUtil.teamMappingsName.get(i), i);
			}
			maxPlayers = 18;
		}
		arenaGameTimeCounter = new ArenaGameTimeCounter(this);
		scoreboardManager = new ScoreboardManager(this);
		teamGui = new TeamGui(this);
		applyGameRules();
		GeneralSettings.plugin.getLogger().info("Starting to pregenerate: " + name);
		WorldManager.generate(world, 250, true).thenAccept(sucess -> {
			GeneralSettings.plugin.getLogger().info("Pregeneration done for: " + name);
			ARENAS.add(this);
			MySQLManager.addArena(this);
		});
	}
	
	public void schedulePvpEnable() {
		Bukkit.getScheduler().runTaskLater(GeneralSettings.plugin, () -> {
			if(gameState == GameState.INGAME) {
				GeneralSettings.plugin.getMessageUtil().sendMessage(Audience.audience(players), Component.text("PvP").color(NamedTextColor.RED).decorate(TextDecoration.ITALIC).append(Component.text(" wird in 1 Minute aktiviert").color(NamedTextColor.GRAY)));
			}
		}, 20*60*2);
		Bukkit.getScheduler().runTaskLater(GeneralSettings.plugin, () -> {
			if(gameState == GameState.INGAME) {
				pvp = true;
				GeneralSettings.plugin.getMessageUtil().sendMessage(Audience.audience(players), Component.text("PvP").color(NamedTextColor.RED).decorate(TextDecoration.ITALIC).append(Component.text(" ist nun aktiviert!").color(NamedTextColor.GRAY)));
			}
		}, 20*60*3);	//20 Ticks = 1 Sekunde; 1 Sekunde * 60 = 1 Minute; 1 Minute * 3 = 3 Minuten;
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
				GeneralSettings.plugin.getMessageUtil().sendMessage(Audience.audience(players), player.displayName().append(Component.text(" hat das Spiel verlassen").color(NamedTextColor.GRAY)));
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
		ARENAS.remove(this);
		scoreboardManager.deleteScordboardManager();
		for (Player player : players) {
			CloudNetAdapter.sendPlayerToLobbyTask(player);
		}
		Bukkit.getScheduler().runTaskLater(GeneralSettings.plugin, () -> {
			WorldManager.deleteLocalWorld(world);
			WorldManager.deleteLocalWorld(nether);
		}, 60L);
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
			GeneralSettings.plugin.getMessageUtil().sendMessage(player, Component.text("Du wirst gleich teleportiert").color(NamedTextColor.GREEN));
		}
		world.setTime(6000);
	}

	private String getRandomString() {
		int leftLimit = 97; // letter 'a'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 5;
		Random random = new Random();

		return random.ints(leftLimit, rightLimit + 1).limit(targetStringLength).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
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
		return ARENAS;
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
		return SPAWN;
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
