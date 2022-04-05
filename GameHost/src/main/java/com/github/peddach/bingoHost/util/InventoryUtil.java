package com.github.peddach.bingoHost.util;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

public class InventoryUtil {
	public static void clearInvOfPlayer(Player player) {
		player.getInventory().clear();
		player.setFireTicks(0);
		player.setHealth(20);
		player.setFoodLevel(20);
		player.getActivePotionEffects().clear();
		player.setArrowsInBody(0);
		Iterator<Advancement> iterator = Bukkit.getServer().advancementIterator();
		while (iterator.hasNext()) {
			AdvancementProgress progress = player.getAdvancementProgress(iterator.next());
			for (String criteria : progress.getAwardedCriteria()) {
				progress.revokeCriteria(criteria);
			}
		}
	}
}
