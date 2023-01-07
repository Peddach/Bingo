package com.github.peddach.bingoHost.arena;

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
import de.petropia.turtleServer.api.worlds.WorldManager;

import java.util.ArrayList;
import java.util.Random;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;

public class Arena {
    private final ArenaMode mode;

    private final ArrayList<Player> players = new ArrayList<>();

    private final BingoTeam[] teams;

    private GameState gameState;

    private final String name;

    private final World world;

    private final World nether;

    private final int maxPlayers;

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
        this.pvp = false;
        this.name = getRandomString();
        this.gameState = GameState.WAITING;
        WorldCreator worldCreator = new WorldCreator(this.name + "_world");
        worldCreator.environment(World.Environment.NORMAL);
        worldCreator.generateStructures(true);
        worldCreator.keepSpawnLoaded(TriState.FALSE);
        worldCreator.type(WorldType.NORMAL);
        worldCreator.generateStructures(true);
        this.world = worldCreator.createWorld();
        WorldCreator netherCreator = new WorldCreator(this.name + "_nether");
        netherCreator.environment(World.Environment.NETHER);
        netherCreator.generateStructures(true);
        netherCreator.keepSpawnLoaded(TriState.FALSE);
        netherCreator.type(WorldType.NORMAL);
        netherCreator.generateStructures(true);
        this.nether = netherCreator.createWorld();
        this.world.getWorldBorder().setSize(4000.0D);
        this.nether.getWorldBorder().setSize(1000.0D);
        WorldManager.linkWorlds(this.world, this.nether);
        generateQuests();
        if (mode == ArenaMode.SINGLE) {
            this.teams = new BingoTeam[8];
            for (int i = 0; i < this.teams.length; i++)
                this.teams[i] = new BingoTeam(1, this.quests, this, TeamUtil.teamMappingsName.get(i), i);
            this.maxPlayers = 8;
        } else if (mode == ArenaMode.TEAM) {
            this.teams = new BingoTeam[4];
            for (int i = 0; i < this.teams.length; i++)
                this.teams[i] = new BingoTeam(2, this.quests, this, TeamUtil.teamMappingsName.get(i), i);
            this.maxPlayers = 8;
        } else {
            this.teams = null;
            throw new IllegalArgumentException("Unkown Arena mode");
        }
        this.arenaGameTimeCounter = new ArenaGameTimeCounter(this);
        this.scoreboardManager = new ScoreboardManager(this);
        this.teamGui = new TeamGui(this);
        applyGameRules();
        GeneralSettings.plugin.getLogger().info("Starting to pregenerate: " + this.name);
        WorldManager.generate(this.world, GeneralSettings.config.getInt("Pregenerate"), true).thenAccept(sucess -> {
            GeneralSettings.plugin.getLogger().info("Pregeneration done for: " + this.name);
            ARENAS.add(this);
            MySQLManager.addArena(this);
        });
    }

    public void schedulePvpEnable() {
        Bukkit.getScheduler().runTaskLater(GeneralSettings.plugin, () -> {
            if (this.gameState == GameState.INGAME)
                GeneralSettings.plugin.getMessageUtil().sendMessage(Audience.audience(this.players), (Component.text("PvP").color(NamedTextColor.RED)).decorate(TextDecoration.ITALIC).append(Component.text(" wird in 1 Minute aktiviert").color(NamedTextColor.GRAY)));
        }, 2400L);
        Bukkit.getScheduler().runTaskLater(GeneralSettings.plugin, () -> {
            if (this.gameState == GameState.INGAME) {
                this.pvp = true;
                GeneralSettings.plugin.getMessageUtil().sendMessage(Audience.audience(this.players), (Component.text("PvP").color(NamedTextColor.RED)).decorate(TextDecoration.ITALIC).append(Component.text(" ist nun aktiviert!").color(NamedTextColor.GRAY)));
            }
        }, 3600L);
    }

    private void generateQuests() {
        this.quests = new Quest[25];
        ArrayList<Material> hardblocks = new ArrayList<>(BlockList.getInstance().getHardBlocks());
        ArrayList<Material> easyblocks = new ArrayList<>(BlockList.getInstance().getEasyBlocks());
        ArrayList<Material> normalblocks = new ArrayList<>(BlockList.getInstance().getNormalBlocks());
        ArrayList<Advancement> advancements = new ArrayList<>(AdvancementList.getInstance().getAdvancements());
        for (int i = 0; i < 25; i++) {
            int randInt = (new Random()).nextInt(25);
            if (randInt == 6 || randInt == 7 || randInt == 8) {
                Advancement advancement = advancements.get((new Random()).nextInt(advancements.size()));
                advancements.remove(advancement);
                this.quests[i] = new Quest(QuestType.ADCHIEVMENT, advancement);
            } else if (randInt <= 3) {
                Material block = hardblocks.get((new Random()).nextInt(hardblocks.size()));
                hardblocks.remove(block);
                this.quests[i] = new Quest(QuestType.BLOCK, block);
            } else if (randInt >= 18) {
                Material block = normalblocks.get((new Random()).nextInt(normalblocks.size()));
                normalblocks.remove(block);
                this.quests[i] = new Quest(QuestType.BLOCK, block);
            } else {
                Material block = easyblocks.get((new Random()).nextInt(easyblocks.size()));
                easyblocks.remove(block);
                this.quests[i] = new Quest(QuestType.BLOCK, block);
            }
        }
    }

    private void applyGameRules() {
        this.world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        this.world.setGameRule(GameRule.DO_FIRE_TICK, false);
        this.world.setGameRule(GameRule.KEEP_INVENTORY, false);
        this.world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, true);
        this.world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        this.world.setDifficulty(Difficulty.NORMAL);
        this.nether.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
    }

    public void removePlayer(Player player) {
        if (!this.players.contains(player))
            return;
        this.scoreboardManager.removePlayer(player);
        this.players.remove(player);
        for (BingoTeam team : this.teams) {
            if (team.checkIfPlayerIsMember(player)) {
                team.removeMember(player);
                GeneralSettings.plugin.getMessageUtil().sendMessage(Audience.audience(this.players), player.displayName().append(Component.text(" hat das Spiel verlassen").color(NamedTextColor.GRAY)));
                break;
            }
        }
        PlayerLeaveArenaEvent event = new PlayerLeaveArenaEvent(this, player);
        Bukkit.getPluginManager().callEvent(event);
    }

    public boolean addPlayer(Player player) {
        if (this.players.size() == this.maxPlayers)
            return false;
        if (this.gameState == GameState.INGAME || this.gameState == GameState.ENDING)
            return false;
        this.scoreboardManager.addPlayer(player);
        this.players.add(player);
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
        return new Location(world, x, y, z, (float) yaw, (float) pitch);
    }

    public void delete() {
        MySQLManager.deleteArena(this.name);
        ARENAS.remove(this);
        this.scoreboardManager.deleteScordboardManager();
        for (Player player : this.players)
            CloudNetAdapter.sendPlayerToLobbyTask(player);
        Bukkit.getScheduler().runTaskLater(GeneralSettings.plugin, () -> {
            WorldManager.deleteLocalWorld(this.world);
            WorldManager.deleteLocalWorld(this.nether);
        }, 60L);
    }

    public void spreadPlayers() {
        ArrayList<Player> playerWithoutTeam = new ArrayList<>(this.players);
        for (BingoTeam bTeam : this.teams) {
            for (Player player : bTeam.getMembers()) {
                if (player != null)
                    playerWithoutTeam.remove(player);
            }
        }
        for(int i = 0; i < this.teams.length; i++) {
            for (int p = 0; p < playerWithoutTeam.size(); p++) {
                if (!this.teams[i].isFull()) {
                    if (this.mode == ArenaMode.SINGLE &&
                            this.teams[i].getMembers()[0] == null) {
                        this.teams[i].addMember(playerWithoutTeam.get(p));
                        playerWithoutTeam.remove(p);
                    }
                    if (this.mode == ArenaMode.TEAM)
                        if (this.teams[i].getMembers()[0] == null) {
                            this.teams[i].addMember(playerWithoutTeam.get(p));
                            playerWithoutTeam.remove(p);
                        } else if (this.teams[i].getMembers()[1] == null) {
                            this.teams[i].addMember(playerWithoutTeam.get(p));
                            playerWithoutTeam.remove(p);
                        }
                }
            }
        }
        for (Player player : this.players) {
            int x = 75 - (new Random()).nextInt(150);
            int z = 75 - (new Random()).nextInt(150);
            Location spawn = new Location(this.world, x + 0.5D, (this.world.getHighestBlockYAt(x, z) + 1), z + 0.5D);
            player.teleportAsync(spawn);
            player.setBedSpawnLocation(spawn, true);
            InventoryUtil.clearInvOfPlayer(player);
            GeneralSettings.plugin.getMessageUtil().sendMessage(player, Component.text("Du wirst gleich teleportiert").color(NamedTextColor.GREEN));
        }
        this.world.setTime(6000L);
    }

    private String getRandomString() {
        int leftLimit = 97;
        int rightLimit = 122;
        int targetStringLength = 5;
        Random random = new Random();
        return random.ints(leftLimit, rightLimit + 1).limit(targetStringLength).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }

    public void setWinner(BingoTeam team) {
        if (this.mode == ArenaMode.SINGLE) {
            Title title = MessageUtil.titlebuilder(team.getMembers()[0].getName(), "hat gewonnen", 1000, 5000, 1000);
            for (Player player : this.players)
                player.showTitle(title);
            setGameState(GameState.ENDING);
        }
        if (this.mode == ArenaMode.TEAM) {
            String splayers = "";
            for (Player iplayer : team.getMembers())
                splayers = splayers + iplayer.getName() + " ";
            Title title = MessageUtil.titlebuilder(splayers, "haben gewonnen", 1000, 5000, 1000);
            for (Player player : this.players)
                player.showTitle(title);
            setGameState(GameState.ENDING);
        }
    }

    public static ArrayList<Arena> getArenas() {
        return ARENAS;
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState) {
        GameStateChangeEvent event = new GameStateChangeEvent(this, this.gameState, gameState);
        Bukkit.getPluginManager().callEvent(event);
        this.gameState = gameState;
    }

    public ArenaMode getMode() {
        return this.mode;
    }

    public static Location getSpawn() {
        return SPAWN;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<Player> getPlayers() {
        return this.players;
    }

    public World getWorld() {
        return this.world;
    }

    public GameCountDown getCountDown() {
        return this.countDown;
    }

    public void setCountDown(GameCountDown countDown) {
        this.countDown = countDown;
    }

    public BingoTeam[] getTeams() {
        return this.teams;
    }

    public TeamGui getTeamGui() {
        return this.teamGui;
    }

    public boolean isPvp() {
        return this.pvp;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public ArenaGameTimeCounter getArenaGameTimeCounter() {
        return this.arenaGameTimeCounter;
    }
}