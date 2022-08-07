package com.github.peddach.bingoHost.teamSelector;

import com.github.peddach.bingoHost.GeneralSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
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
			if(event.getSlotType() == SlotType.QUICKBAR) {
				return;
			}
			int slot = event.getSlot();
			Player player = (Player) event.getWhoClicked();
			if(arena.getTeams()[slot].checkIfPlayerIsMember(player)) {
				GeneralSettings.plugin.getMessageUtil().sendMessage(player, Component.text("Du hast das Team verlassen").color(NamedTextColor.GRAY));
				arena.getTeams()[slot].removeMember(player);
				arena.getTeamGui().updateInv();
				return;
			}
			if(arena.getTeams()[slot].isFull()) {
				GeneralSettings.plugin.getMessageUtil().sendMessage(player, Component.text("Das Team ist bereits voll").color(NamedTextColor.RED));
				arena.getTeamGui().updateInv();
				return;
			}
			for(BingoTeam team : arena.getTeams()) {
				if(team.checkIfPlayerIsMember(player)) {
					team.removeMember(player);
				}
			}
			arena.getTeams()[slot].addMember(player);
			GeneralSettings.plugin.getMessageUtil().sendMessage(player, Component.text("Du hast das Team betreten").color(NamedTextColor.GRAY));
			arena.getTeamGui().updateInv();
			return;
		}
	}
}
