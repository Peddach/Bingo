package com.github.peddach.bingoHost.arena;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.github.peddach.bingoHost.GeneralSettings;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class TraderSpawner {
	private int taskID;

	public TraderSpawner(Arena arena) {
		Runnable task = () -> {
			if (!Arena.getArenas().contains(arena) || arena.getGameState() != GameState.INGAME) {
				Bukkit.getScheduler().cancelTask(taskID);
				return;
			}
			GeneralSettings.plugin.getMessageUtil().sendMessage(Audience.audience(arena.getPlayers()), Component.text("Ein Trader ist bei dir gespawned!").color(NamedTextColor.GRAY));
			for (Player player : arena.getPlayers()) {
				WanderingTrader trader = (WanderingTrader) player.getWorld().spawnEntity(player.getLocation(), EntityType.WANDERING_TRADER);
				MerchantRecipe merchantRecipe = new MerchantRecipe(new ItemStack(Material.OBSIDIAN, 2), 5);
				merchantRecipe.addIngredient(new ItemStack(Material.EMERALD, 1));
				List<MerchantRecipe> trades = new ArrayList<>(trader.getRecipes());
				trades.add(merchantRecipe);
				trader.setRecipes(trades);
			}
		};
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(GeneralSettings.plugin, task, 60*20*15, 20*60*15);
	}
}
