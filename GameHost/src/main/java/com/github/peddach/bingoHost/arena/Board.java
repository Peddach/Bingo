package com.github.peddach.bingoHost.arena;

import com.github.peddach.bingoHost.GeneralSettings;
import de.petropia.turtleServer.server.TurtleServer;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.github.peddach.bingoHost.quest.Quest;
import com.github.peddach.bingoHost.quest.QuestType;
import com.github.peddach.bingoHost.teamSelector.TeamUtil;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.Arrays;

public class Board {
	
	private final boolean[] quests = new boolean[25];
	private Quest[] quest = new Quest[25];
	private final BingoTeam team;
	
	public Board(Quest[] quest, BingoTeam team) {
		this.team = team;
		this.quest = quest;
		Arrays.fill(quests, false);
	}
	
	public void setSucess(int number) {
		quests[number] = true;
		if(quest[number].getType() == QuestType.BLOCK) {
			successMessage(quest[number].getBlock());
		}
		update();
		for (Player player : team.getMembers()) {
			if(player == null){
				continue;
			}
			TurtleServer.getMongoDBHandler().getPetropiaPlayerByOnlinePlayer(player).thenAccept(petropiaPlayer -> {
				petropiaPlayer.increateStats("Bingo_Points", 1);
				petropiaPlayer.increateStats("Bingo_Quests", 1);
			});
		}
	}
	
	public void update() {
		//Horizontal
		if(quests[0] && quests[1] && quests[2] && quests[3] && quests[4]) {
			win();
			return;
		}
		if(quests[5] && quests[6] && quests[7] && quests[8] && quests[9]) {
			win();
			return;
		}
		if(quests[10] && quests[11] && quests[12] && quests[13] && quests[14]) {
			win();
			return;
		}
		if(quests[15] && quests[16] && quests[17] && quests[18] && quests[19]) {
			win();
			return;
		}
		if(quests[20] && quests[21] && quests[22] && quests[23] && quests[24]) {
			win();
			return;
		}
		//Vertically
		if(quests[0] && quests[5] && quests[10] && quests[15] && quests[20]) {
			win();
			return;
		}
		if(quests[1] && quests[6] && quests[11] && quests[16] && quests[21]) {
			win();
			return;
		}
		if(quests[2] && quests[7] && quests[12] && quests[17] && quests[22]) {
			win();
			return;
		}
		if(quests[3] && quests[8] && quests[13] && quests[18] && quests[23]) {
			win();
			return;
		}
		if(quests[4] && quests[9] && quests[14] && quests[19] && quests[24]) {
			win();
			return;
		}
		
		//Cross
		if(quests[0] && quests[6] && quests[12] && quests[18] && quests[24]) {
			win();
			return;
		}
		if(quests[4] && quests[8] && quests[12] && quests[16] && quests[20]) {
			win();
			return;
		}
	}

	private void win() {
		team.getArena().setWinner(team);
		for(Player player : team.getMembers()) {
			if(player == null){
				continue;
			}
			TurtleServer.getMongoDBHandler().getPetropiaPlayerByOnlinePlayer(player).thenAccept(petropiaPlayer -> {
				petropiaPlayer.increateStats("Bingo_Wins", 1);
				petropiaPlayer.increateStats("Bingo_Points", 5);
			});
		}
	}
	private void successMessage(Material block) {
		Component blockname = Component.translatable(block).color(TextColor.color(255, 255, 50));
		TextColor teamcolor = TextColor.fromCSSHexString(TeamUtil.teamMappingsNamedTextColor.get(team.getNumber()));
		Component message = Component.text(team.getName(), teamcolor).append(Component.text(" hat die Aufgabe ").color(NamedTextColor.GRAY).append(blockname).append(Component.text(" erfolgreich abgeschlossen", NamedTextColor.GRAY)));
		for(Player player : team.getMembers()) {
			if(player == null) {
				continue;
			}
			player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1F, 1F);
		}
		GeneralSettings.plugin.getMessageUtil().sendMessage(Audience.audience(team.getArena().getPlayers()), message);
	}

	public Quest[] getQuest() {
		return quest;
	}
	
	public boolean[] getQuests() {
		return quests;
	}
}
