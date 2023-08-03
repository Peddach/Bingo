package com.github.peddach.bingoHost.listener;

import com.github.peddach.bingoHost.ArenaPublishHelper;
import com.github.peddach.bingoHost.arena.Arena;
import de.petropia.turtleServer.api.minigame.ArenaUpdateResendRequestEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ArenaRepublishListener implements Listener {

    @EventHandler
    public void onResendEvent(ArenaUpdateResendRequestEvent event){
        Arena.getArenas().forEach(ArenaPublishHelper::publishArena);
    }
}
