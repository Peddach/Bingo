package com.github.peddach.bingoHost.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.github.peddach.bingoHost.GeneralSettings;
import com.github.peddach.bingoHost.arena.GameState;
import com.github.peddach.bingoHost.arena.ScheduledArenaDelete;
import com.github.peddach.bingoHost.arena.TraderSpawner;
import com.github.peddach.bingoHost.events.GameStateChangeEvent;
import com.github.peddach.bingoHost.mysql.MySQLManager;
import com.github.peddach.bingoHost.util.InventoryUtil;
import com.github.peddach.bingoHost.utilItems.BackpackItem;
import com.github.peddach.bingoHost.utilItems.BingoCard;

public class GameStateChangeListener implements Listener {

	@EventHandler
	public void onGameStateChangeEvent(GameStateChangeEvent event) {
		MySQLManager.updateArena(event.getArena());
		if (event.getAfter() == GameState.INGAME) {
			for (Player player : event.getArena().getPlayers()) {
				player.getInventory().setItem(0, new ItemStack(Material.BREAD, 15));
				player.getInventory().setItem(8, BingoCard.getItem());
				player.getInventory().setItem(9, BackpackItem.getItem());
				player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 250, false, false));
			}
			new TraderSpawner(event.getArena());
		}
		if (event.getAfter() == GameState.ENDING) {
			new ScheduledArenaDelete(event.getArena());
			Vector vector = new Vector(0, 4, 0);
			for (Player player : event.getArena().getPlayers()) {
				InventoryUtil.clearInvOfPlayer(player);
				player.setVelocity(vector);
			}
			Bukkit.getScheduler().runTaskLater(GeneralSettings.plugin, () -> {
				for (Player player : event.getArena().getPlayers()) {
					if (!player.isOnline()) {
						continue;
					}
					player.setAllowFlight(true);
					player.setFlying(true);
					player.playSound(player.getLocation(), Sound.EVENT_RAID_HORN, 100F, 1.2F);
				}
			}, 20);

		}
	}
}
