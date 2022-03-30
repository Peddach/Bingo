package com.github.peddach.bingoHost.teamSelector;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.peddach.bingoHost.arena.Arena;
import com.github.peddach.bingoHost.arena.BingoTeam;
import com.github.peddach.bingoHost.arena.GameState;
import com.github.peddach.bingoHost.events.PlayerJoinArenaEvent;
import com.github.peddach.bingoHost.util.MessageUtil;

public class TeamGuiListener implements Listener{
	
	@EventHandler
	public void onPlayerClick(InventoryClickEvent event) {
		for(Arena arena : Arena.getArenas()) {
			if(arena.getTeamGui().getInv() == event.getInventory()) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerJoinArena(PlayerJoinArenaEvent event){
		if(!(event.getArena().getGameState() == GameState.WAITING || event.getArena().getGameState() == GameState.STARTING)) {
			return;
		}
		event.getPlayer().getInventory().setItem(0, TeamUtil.getTeamItem());
	}
	
	@EventHandler
	public void onPlayerClickTeamItem(PlayerInteractEvent event) {
		if(event.getItem() == null) {
			return;
		}
		if(!event.getItem().equals(TeamUtil.getTeamItem())) {
			return;
		}
		for(Arena arena : Arena.getArenas()) {
			if(!arena.getPlayers().contains(event.getPlayer())) {
				continue;
			}
			arena.getTeamGui().openForPlayer(event.getPlayer());
		}
	}
	
	@EventHandler
	public void onPlayerChooseTeam(InventoryClickEvent event) {
		for(Arena arena : Arena.getArenas()) {
			if(!arena.getTeamGui().getInv().equals(event.getInventory())) {
				continue;
			}
			event.setCancelled(true);
			if(event.getSlot() >= 10 || event.getSlot() < 0) {
				return;
			}
			int slot = event.getSlot();
			Player player = (Player) event.getWhoClicked();
			if(arena.getTeams()[slot].checkIfPlayerIsMember(player)) {
				MessageUtil.sendMessage(player, "§7Du hast das Team verlassen");
				arena.getTeamGui().updateInv();
				return;
			}
			if(arena.getTeams()[slot].isFull()) {
				MessageUtil.sendMessage(player, "§cDas Team ist bereits voll");
				arena.getTeamGui().updateInv();
				return;
			}
			for(BingoTeam team : arena.getTeams()) {
				if(team.checkIfPlayerIsMember(player)) {
					team.removeMember(player);
				}
			}
			arena.getTeams()[slot].addMember(player);
			MessageUtil.sendMessage(player, "§7Du hast das Team betreten");
			arena.getTeamGui().updateInv();
			return;
		}
	}
}
