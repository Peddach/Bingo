package com.github.peddach.bingoHost.util;

import org.bukkit.entity.Player;

public class InventoryUtil {
	public static void clearInvOfPlayer(Player player) {
		player.getInventory().clear();
		player.setFireTicks(0);
		player.setHealth(20);
		player.setFoodLevel(20);
		player.getActivePotionEffects().clear();
		player.setArrowsInBody(0);
	}
}
