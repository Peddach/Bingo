package com.github.peddach.bingoHost.listener;

import com.github.peddach.bingoHost.ArenaPublishHelper;
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
import com.github.peddach.bingoHost.util.InventoryUtil;
import com.github.peddach.bingoHost.utilItems.BackpackItem;
import com.github.peddach.bingoHost.utilItems.BingoCard;

public class GameStateChangeListener implements Listener {

	@EventHandler
	public void onGameStateChangeEvent(GameStateChangeEvent event) {
		ArenaPublishHelper.publishArena(event.getArena());
		if (event.getAfter() == GameState.INGAME) {
			for (Player player : event.getArena().getPlayers()) {
				player.getInventory().setItem(0, new ItemStack(Material.BREAD, 15));
				player.getInventory().setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				player.getInventory().setItem(8, BingoCard.getItem());
				player.getInventory().setItem(7, BackpackItem.getItem());
				player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 250, false, false));
				player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 20*60*5, 1, false, false));
			}
			event.getArena().schedulePvpEnable();
			new TraderSpawner(event.getArena());
		}
		if (event.getAfter() == GameState.ENDING) {
			event.getArena().setPvp(false);
			new ScheduledArenaDelete(event.getArena());
			Vector vector = new Vector(0, 4, 0);
			for (Player player : event.getArena().getPlayers()) {
				InventoryUtil.clearInvOfPlayer(player);
				player.setVelocity(vector);
				player.getInventory().setItem(8, BingoCard.getItem());
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
