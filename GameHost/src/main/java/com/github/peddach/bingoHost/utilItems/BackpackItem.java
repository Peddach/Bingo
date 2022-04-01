package com.github.peddach.bingoHost.utilItems;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import com.github.peddach.bingoHost.arena.Arena;
import com.github.peddach.bingoHost.arena.BingoTeam;
import com.github.peddach.bingoHost.arena.GameState;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class BackpackItem implements Listener {

	private static final ItemStack item = createBackpackItem();

	private static ItemStack createBackpackItem() {
		ItemStack item = new ItemStack(Material.CHEST);
		item.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 1);
		item.editMeta(meta -> {
			meta.displayName(Component.text("Rucksack").color(NamedTextColor.GOLD));
			List<Component> lore = new ArrayList<>();
			lore.add(Component.text(" "));
			lore.add(Component.text("Klicke zum öffnen").color(NamedTextColor.GRAY));
			lore.add(Component.text(" "));
			meta.lore(lore);
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
		});
		return item;
	}

	private void openBackPack(Player player) {
		for (Arena arena : Arena.getArenas()) {
			if (!arena.getPlayers().contains(player)) {
				continue;
			}
			for (BingoTeam team : arena.getTeams()) {
				if (!team.checkIfPlayerIsMember(player)) {
					continue;
				}
				player.openInventory(team.getBackpack());
				break;
			}
		}
	}
	
	public static ItemStack getItem() {
		return item;
	}

	@EventHandler
	public void onPlayerClickBackpackItemEvent(PlayerInteractEvent event) {
		if (event.getItem() == null) {
			return;
		}
		if (!event.getItem().equals(item)) {
			return;
		}
		openBackPack(event.getPlayer());
	}

	@EventHandler
	public void onPlayerClickBackpackItem(InventoryClickEvent event) {
		if (event.getCurrentItem() == null) {
			return;
		}
		if (!event.getCurrentItem().equals(item)) {
			return;
		}
		openBackPack((Player) event.getWhoClicked());
	}
	
	@EventHandler
	public void onPlayerPickUpEvent(EntityPickupItemEvent event) {
		if(event.getEntity() instanceof Player player) {
			if(event.getItem().getItemStack().equals(item)) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent event){
		if(event.getDrops().contains(item)) {
			event.getDrops().remove(item);
		}
	}
	
	@EventHandler
	public void onPlayerRespawnEvent(PlayerPostRespawnEvent event) {
		for(Arena arena : Arena.getArenas()) {
			if(arena.getPlayers().contains(event.getPlayer())) {
				if(arena.getGameState() == GameState.INGAME) {
					event.getPlayer().getInventory().setItem(9, item);
					break;
				}
			}
		}
	}

}
