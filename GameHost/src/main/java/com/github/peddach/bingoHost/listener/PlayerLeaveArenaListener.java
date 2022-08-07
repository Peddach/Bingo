package com.github.peddach.bingoHost.listener;

import com.github.peddach.bingoHost.GeneralSettings;
import net.kyori.adventure.audience.Audience;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.github.peddach.bingoHost.arena.ArenaMode;
import com.github.peddach.bingoHost.arena.BingoTeam;
import com.github.peddach.bingoHost.arena.GameState;
import com.github.peddach.bingoHost.events.PlayerLeaveArenaEvent;
import com.github.peddach.bingoHost.mysql.MySQLManager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class PlayerLeaveArenaListener implements Listener{
	
	@EventHandler
	public void onPlayerLeaveArena(PlayerLeaveArenaEvent event) {
		MySQLManager.updateArena(event.getArena());
		if(event.getArena().getMode() == ArenaMode.SINGLE) {
			if(event.getArena().getPlayers().size() == 1 && event.getArena().getGameState() == GameState.INGAME) {
				event.getArena().setGameState(GameState.ENDING);
			}
		}
		if(event.getArena().getMode() == ArenaMode.TEAM) {
			if(event.getArena().getGameState() == GameState.INGAME) {
				int teamsWithPlayers = 0;
				for(BingoTeam team : event.getArena().getTeams()) {
					if(team.getMembers()[0] != null || team.getMembers()[1] != null) {
						teamsWithPlayers++;
					}
				}
				GeneralSettings.plugin.getMessageUtil().broadcastMessage(Audience.audience(event.getArena().getPlayers()), Component.text("Es sind noch ").color(NamedTextColor.GRAY)
						.append(Component.text(teamsWithPlayers).color(NamedTextColor.GOLD))
						.append(Component.text(" Teams übrig").color(NamedTextColor.GRAY)));
				if(teamsWithPlayers <= 1) {
					event.getArena().setGameState(GameState.ENDING);
				}
			}
		}
		event.getArena().getTeamGui().updateInv();
	}
}
