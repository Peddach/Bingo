package com.github.peddach.bingoHost.listener;

import com.github.peddach.bingoHost.GeneralSettings;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
				GeneralSettings.plugin.getMessageUtil().broadcastMessage(Audience.audience(arena.getPlayers()), event.getPlayer().displayName().append(Component.text(" ist gestorben").color(NamedTextColor.GRAY)));
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
