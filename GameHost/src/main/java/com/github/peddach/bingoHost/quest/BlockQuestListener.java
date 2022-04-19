package com.github.peddach.bingoHost.quest;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.github.peddach.bingoHost.arena.Arena;
import com.github.peddach.bingoHost.arena.BingoTeam;
import com.github.peddach.bingoHost.arena.GameState;
import com.github.peddach.bingoHost.utilItems.BingoCard;

public class BlockQuestListener implements Listener {
		
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if(!(event.getAction().isLeftClick() || event.getAction().isRightClick())) {
			return;
		}
		if (checkIfItemIsAQuest(event.getPlayer(), event.getItem())) {
			event.getItem().setAmount(event.getItem().getAmount() - 1);
			event.setCancelled(true);
			event.getPlayer().updateInventory();
		}
	}

	private boolean checkIfItemIsAQuest(Player player, ItemStack item) {
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
			return false;
		}
		if (arena.getGameState() != GameState.INGAME) {
			return false;
		}
		if (player == null) {
			return false;
		}
		if (item == null) {
			return false;
		}
		for (int i = 0; i < team.getBoard().getQuest().length; i++) {
			if (team.getBoard().getQuest()[i].getType() == QuestType.BLOCK) {
				if (team.getBoard().getQuest()[i].getBlock() != item.getType()) {
					continue;
				}
				if (team.getBoard().getQuests()[i] == true) {
					continue;
				}
				if(BingoCard.getItem().equals(item)) {
					continue;
				}
				team.getBoard().setSucess(i);
				return true;
			}
		}
		return false;
	}
}
