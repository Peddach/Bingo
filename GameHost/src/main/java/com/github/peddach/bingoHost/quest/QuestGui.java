package com.github.peddach.bingoHost.quest;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.peddach.bingoHost.arena.Arena;
import com.github.peddach.bingoHost.arena.BingoTeam;
import com.github.peddach.bingoHost.arena.Board;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class QuestGui implements Listener {

	private static List<Inventory> guis = new ArrayList<>();

	public static void openGuiForPlayer(Player player) {
		Board board = null;
		for (Arena arena : Arena.getArenas()) {
			if (arena.getPlayers().contains(player)) {
				for (BingoTeam team : arena.getTeams()) {
					if (team.checkIfPlayerIsMember(player)) {
						board = team.getBoard();
					}
				}
			}
		}
		if (board == null) {
			Inventory inv = Bukkit.createInventory(null, InventoryType.DROPPER);
			inv.setItem(4, errorItem("Fehler (Error: 44)"));
			openAndRegist(player, inv);
			return;
		}
		Inventory inv = Bukkit.createInventory(null, 5 * 9);
		int[] slots = { 2, 3, 4, 5, 6, 11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42 };
		for (int i = 0; i < slots.length; i++) {
			if (board.getQuest()[i].getType() == null) {
				inv.setItem(slots[i], errorItem("Fehler (Error: 52) " + i));
			}
			if (board.getQuest()[i].getType() == QuestType.BLOCK) {
				ItemStack item = new ItemStack(board.getQuest()[i].getBlock(), 1);
				if (board.getQuests()[i] == true) {
					item.addUnsafeEnchantment(Enchantment.LURE, 1);
					item.editMeta(meta -> {
						meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
						List<Component> lore = new ArrayList<>();
						lore.add(Component.text(" "));
						lore.add(Component.text("Erledigt").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
						lore.add(Component.text(" "));
						meta.lore(lore);
					});
				}
				if (item.getType() == Material.AIR) {
					item = errorItem("Fehler (Error: 58) " + i);
				}
				inv.setItem(slots[i], item);
			}
			if (board.getQuest()[i].getType() == QuestType.ADCHIEVMENT) {
				ItemStack item = new ItemStack(Material.IRON_SWORD, 1);
				final Advancement advancement = board.getQuest()[i].getAdvancement();
				if (board.getQuests()[i] == true) {
					item.addUnsafeEnchantment(Enchantment.LURE, 1);
					item.editMeta(meta -> {
						meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
						List<Component> lore = new ArrayList<>();
						lore.add(Component.text(" "));
						lore.add(Component.text("Erledigt").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
						lore.add(Component.text(" "));
						meta.lore(lore);
						Component advancementName = advancement.getDisplay().title();
						meta.displayName(Component.text("Advancement: ", NamedTextColor.GOLD).decorate(TextDecoration.BOLD).append(advancementName));
					});
				} else {
					if (board.getQuest()[i].getAdvancement().getDisplay() == null) {
						item = errorItem("§4Error 89");
						inv.setItem(slots[i], item);
						continue;
					}
					item.editMeta(meta -> {
						Component advancementName = advancement.getDisplay().title().color(NamedTextColor.GRAY);
						meta.displayName(Component.text("Advancement: ", NamedTextColor.GOLD).decorate(TextDecoration.BOLD).append(advancementName));
						List<Component> lore = new ArrayList<>();
						lore.add(Component.text(" "));
						lore.add(advancement.getDisplay().description().color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC));
						lore.add(Component.text(" "));
						meta.lore(lore);
					});

				}
				inv.setItem(slots[i], item);
			}
		}
		openAndRegist(player, inv);
		return;
	}

	private static void openAndRegist(Player player, Inventory inv) {
		player.openInventory(inv);
		guis.add(inv);
		return;
	}

	private static ItemStack errorItem(String msg) {
		ItemStack errorItem = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
		ItemMeta itemMeta = errorItem.getItemMeta();
		itemMeta.displayName(Component.text(msg, NamedTextColor.DARK_RED));
		errorItem.setItemMeta(itemMeta);
		return errorItem;
	}

	@EventHandler
	public void onPlayerClose(InventoryCloseEvent event) {
		if (guis.contains(event.getInventory())) {
			guis.remove(event.getInventory());
		}
	}

	@EventHandler
	public void onPlayerInteractEvent(InventoryClickEvent event) {
		if (guis.contains(event.getInventory())) {
			event.setCancelled(true);
		}
	}

	public static List<Inventory> getGuis() {
		return guis;
	}
}
