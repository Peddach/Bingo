package com.github.peddach.bingoHost.listener;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import com.github.peddach.bingoHost.arena.Arena;
import com.github.peddach.bingoHost.arena.GameState;

public class PlayerDeathListener implements Listener{
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		event.deathMessage(null);
		for(Arena arena : Arena.getArenas()) {
			if(arena.getPlayers().contains(event.getPlayer())) {
				arena.broadcastMessage("§7" + event.getPlayer().getName() + " §7ist gestorben");
				for(Player player : arena.getPlayers()) {
					player.playSound(player.getLocation(), Sound.ENTITY_EVOKER_PREPARE_WOLOLO, 2F, 1);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerPostRespawnEvent event) {
		for(Arena arena : Arena.getArenas()) {
			if(arena.getPlayers().contains(event.getPlayer())) {
				if(arena.getGameState() == GameState.INGAME) {
					event.getPlayer().getInventory().setItem(0, new ItemStack(Material.BREAD, 10));
					event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 250, false, false));
					return;
				}
			}
		}
	}
}
