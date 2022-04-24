package com.github.peddach.bingoHost.quest;

import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import com.github.peddach.bingoHost.arena.Arena;
import com.github.peddach.bingoHost.arena.BingoTeam;
import com.github.peddach.bingoHost.arena.GameState;
import com.github.peddach.bingoHost.teamSelector.TeamUtil;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class AdvancememtQuestListener implements Listener {

	@EventHandler
	public void onPlayerAdchiveAdvancement(PlayerAdvancementDoneEvent event) {
		checkIfAdvancementIsQuest(event.getAdvancement(), event.getPlayer());
	}

	private void checkIfAdvancementIsQuest(Advancement advancement, Player player) {
		if (!AdvancementList.getInstance().getAdvancements().contains(advancement)) {
			return;
		}
		Arena arena = null;
		BingoTeam team = null;
		for (Arena i : Arena.getArenas()) {
			if (i.getPlayers().contains(player)) {
				arena = i;
				for (BingoTeam ii : arena.getTeams()) {
					if (ii.checkIfPlayerIsMember(player)) {
						team = ii;
					}
				}
			}
		}
		if (arena == null || team == null) {
			return;
		}
		if (arena.getGameState() != GameState.INGAME) {
			return;
		}
		for (int i = 0; i < team.getBoard().getQuest().length; i++) {
			if (team.getBoard().getQuest()[i].getType() == QuestType.ADCHIEVMENT) {
				if (team.getBoard().getQuest()[i].getAdvancement() != advancement) {
					continue;
				}
				if (team.getBoard().getQuests()[i] == true) {
					continue;
				}
				team.getBoard().setSucess(i);
				announceAdvancements(advancement, arena, team);
				return;
			}
		}
		return;
	}

	private void announceAdvancements(Advancement advancement, Arena arena, BingoTeam team) {
		if (advancement.getDisplay() == null) {
			return;
		}
		Component name = Component.text(AdvancementList.getInstance().getAdvancementTitleMappings().get(advancement)).color(NamedTextColor.GOLD);
		TextColor teamcolor = TextColor.fromCSSHexString(TeamUtil.teamMappingsNamedTextColor.get(team.getNumber()));
		Component message = Component.text(team.getName()).color(teamcolor).append(Component.text(" hat das Advancement ").color(NamedTextColor.GRAY)).append(name).append(Component.text(" erhalten").color(NamedTextColor.GRAY));
		arena.broadcastMessage(message);
	}

}
