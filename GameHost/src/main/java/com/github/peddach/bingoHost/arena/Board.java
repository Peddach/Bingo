package com.github.peddach.bingoHost.arena;

import com.github.peddach.bingoHost.GeneralSettings;
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

public class Board {
	
	private boolean quests[] = new boolean[25];
	private Quest quest[] = new Quest[25]; 
	private BingoTeam team;
	
	public Board(Quest[] quest, BingoTeam team) {
		this.team = team;
		this.quest = quest;
		for(int i = 0; i < quests.length; i++) {
			quests[i] = false;
		}
	}
	
	public void setSucess(int number) {
		quests[number] = true;
		if(quest[number].getType() == QuestType.BLOCK) {
			successMessage(quest[number].getBlock());
		}
		update();
	}
	
	public void update() {
		//Horizontal
		if(quests[0] && quests[1] && quests[2] && quests[3] && quests[4]) {
			team.getArena().setWinner(team);
			return;
		}
		if(quests[5] && quests[6] && quests[7] && quests[8] && quests[9]) {
			team.getArena().setWinner(team);
			return;
		}
		if(quests[10] && quests[11] && quests[12] && quests[13] && quests[14]) {
			team.getArena().setWinner(team);
			return;
		}
		if(quests[15] && quests[16] && quests[17] && quests[18] && quests[19]) {
			team.getArena().setWinner(team);
			return;
		}
		if(quests[20] && quests[21] && quests[22] && quests[23] && quests[24]) {
			team.getArena().setWinner(team);
			return;
		}
		//Vertically
		if(quests[0] && quests[5] && quests[10] && quests[15] && quests[20]) {
			team.getArena().setWinner(team);
			return;
		}
		if(quests[1] && quests[6] && quests[11] && quests[16] && quests[21]) {
			team.getArena().setWinner(team);
			return;
		}
		if(quests[2] && quests[7] && quests[12] && quests[17] && quests[22]) {
			team.getArena().setWinner(team);
			return;
		}
		if(quests[3] && quests[8] && quests[13] && quests[18] && quests[23]) {
			team.getArena().setWinner(team);
			return;
		}
		if(quests[4] && quests[9] && quests[14] && quests[19] && quests[24]) {
			team.getArena().setWinner(team);
			return;
		}
		
		//Cross
		if(quests[0] && quests[6] && quests[12] && quests[18] && quests[24]) {
			team.getArena().setWinner(team);
			return;
		}
		if(quests[4] && quests[8] && quests[12] && quests[16] && quests[20]) {
			team.getArena().setWinner(team);
			return;
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
		GeneralSettings.plugin.getMessageSender().broadcastMessage(Audience.audience(team.getArena().getPlayers()), message);
	}

	public Quest[] getQuest() {
		return quest;
	}
	
	public boolean[] getQuests() {
		return quests;
	}
}
