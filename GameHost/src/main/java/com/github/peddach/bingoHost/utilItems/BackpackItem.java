package com.github.peddach.bingoHost.utilItems;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryCloseEvent.Reason;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import com.github.peddach.bingoHost.GeneralSettings;
import com.github.peddach.bingoHost.arena.Arena;
import com.github.peddach.bingoHost.arena.BingoTeam;
import com.github.peddach.bingoHost.arena.GameState;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class BackpackItem implements Listener {

	private static final ItemStack ITEM = createBackpackItem();

	private static ItemStack createBackpackItem() {
		ItemStack item = new ItemStack(Material.BUNDLE);
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
				if (player.getOpenInventory() != null && player.getOpenInventory().getTopInventory().equals(team.getBackpack())) {
					return;
				}
				player.closeInventory(Reason.OPEN_NEW);
				Bukkit.getScheduler().runTaskLater(GeneralSettings.plugin, () -> {
					player.openInventory(team.getBackpack());
				}, 1);
				break;
			}
		}
	}

	public static ItemStack getItem() {
		return ITEM;
	}

	@EventHandler
	public void onPlayerClickBackpackItemEvent(PlayerInteractEvent event) {
		if (event.getItem() == null) {
			return;
		}
		if (!event.getItem().equals(ITEM)) {
			return;
		}
		event.setCancelled(true);
		openBackPack(event.getPlayer());
	}

	@EventHandler
	public void onPlayerClickBackpackItem(InventoryClickEvent event) {
		if (event.getCurrentItem() == null) {
			return;
		}
		if (!event.getCurrentItem().equals(ITEM)) {
			return;
		}
		event.setCancelled(true);
		event.getCurrentItem().setType(Material.AIR);
		event.getWhoClicked().getInventory().setItem(7, ITEM);
		Player p = (Player) event.getWhoClicked();
		openBackPack(p);

	}

	@EventHandler
	public void onPlayerPickUpEvent(EntityPickupItemEvent event) {
		if (event.getEntity()instanceof Player player) {
			if (event.getItem().getItemStack().equals(ITEM)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		if (event.getDrops().contains(ITEM)) {
			event.getDrops().remove(ITEM);
		}
	}

	@EventHandler
	public void onPlayerRespawnEvent(PlayerPostRespawnEvent event) {
		for (Arena arena : Arena.getArenas()) {
			if (arena.getPlayers().contains(event.getPlayer())) {
				if (arena.getGameState() == GameState.INGAME) {
					event.getPlayer().getInventory().setItem(7, ITEM);
					break;
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		if (event.getItemDrop() == null) {
			return;
		}
		if (event.getItemDrop().getItemStack() == null) {
			return;
		}
		if (event.getItemDrop().getItemStack().equals(ITEM)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerPlaceEvent(BlockPlaceEvent event) {
		if (!event.getItemInHand().equals(ITEM)) {
			return;
		}
		event.setCancelled(true);
		openBackPack(event.getPlayer());
	}
}
