package com.github.peddach.bingoHost;

import com.github.peddach.bingoHost.arena.Arena;
import com.github.peddach.bingoHost.arena.ArenaMode;
import com.github.peddach.bingoHost.arena.GameState;
import de.petropia.turtleServer.api.minigame.GameMode;
import de.petropia.turtleServer.api.minigame.MinigameNames;

public class ArenaPublishHelper {

    public static void publishArena(Arena arena){
        String id = arena.getName();
        int maxPlayers = arena.getMaxPlayers();
        GameMode mode = convertGamemode(arena.getMode());
        de.petropia.turtleServer.api.minigame.GameState state = convertGameState(arena.getGameState());
        String game = MinigameNames.BINGO.name();
        GeneralSettings.plugin.getCloudNetAdapter().publishArenaUpdate(
                game,
                id,
                mode,
                maxPlayers,
                state,
                arena.getPlayers());
    }

    private static de.petropia.turtleServer.api.minigame.GameState convertGameState(GameState gameState) {
        return switch (gameState){
            case WAITING -> de.petropia.turtleServer.api.minigame.GameState.WAITING;
            case STARTING -> de.petropia.turtleServer.api.minigame.GameState.STARTING;
            case INGAME -> de.petropia.turtleServer.api.minigame.GameState.INGAME;
            case ENDING, UNKNOWN -> de.petropia.turtleServer.api.minigame.GameState.ENDING;
        };
    }

    private static GameMode convertGamemode(ArenaMode mode){
        if(mode == ArenaMode.SINGLE){
            return GameMode.SINGLE;
        }
        return GameMode.DUO;
    }
}
