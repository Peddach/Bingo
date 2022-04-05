package com.github.peddach.bingoHost.quest;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.github.peddach.bingoHost.arena.Arena;
import com.github.peddach.bingoHost.arena.BingoTeam;
import com.github.peddach.bingoHost.arena.GameState;
import com.github.peddach.bingoHost.util.MessageUtil;
import com.github.peddach.bingoHost.utilItems.BingoCard;

public class QuestListener implements Listener {

	@EventHandler
	public void onPickUp(EntityPickupItemEvent event) {
		if (event.getEntity()instanceof Player player) {
			if (checkIfItemIsAQuest(player, event.getItem().getItemStack())) {
				event.getItem().getItemStack().setAmount(event.getItem().getItemStack().getAmount() - 1);
			}
		}
	}

	@EventHandler
	public void itemClickEvent(InventoryClickEvent event) {
		if (QuestGui.getGuis().contains(event.getInventory())) {
			return;
		}
		if(event.getInventory().getType() == InventoryType.CRAFTING || event.getInventory().getType() == InventoryType.WORKBENCH) {
			return;
		}
		if (checkIfItemIsAQuest((Player) event.getWhoClicked(), event.getCurrentItem())) {
			event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() - 1);
			MessageUtil.sendMessage((Player) event.getWhoClicked(), "ClickEvent");
		}
	}

	@EventHandler
	public void onCraftEvent(CraftItemEvent event) {
		if (!checkIfItemIsAQuest((Player) event.getWhoClicked(), event.getInventory().getResult())) {
			return;
		}
		if(event.getClickedInventory() == null) {
			return;
		}
		event.getClickedInventory().getItem(0).setAmount(event.getClickedInventory().getItem(0).getAmount() - 1);
	}

	@EventHandler
	public void onMilkEvent(PlayerInteractEvent event) {
		if (checkIfItemIsAQuest(event.getPlayer(), event.getItem())) {
			event.getItem().setAmount(event.getItem().getAmount() - 1);
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
