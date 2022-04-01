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
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import com.github.peddach.bingoHost.arena.Arena;
import com.github.peddach.bingoHost.arena.GameState;
import com.github.peddach.bingoHost.quest.QuestGui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class BingoCard implements Listener{
	
	private final static ItemStack item = createCard();
	
	private static ItemStack createCard() {
		ItemStack item = new ItemStack(Material.PAPER, 1);
		item.editMeta(meta -> {
			meta.displayName(Component.text("Bingo Karte").color(NamedTextColor.GOLD).decorate(TextDecoration.ITALIC));
			List<Component> lore = new ArrayList<>();
			lore.add(Component.text(" "));
			lore.add(Component.text("Klicke um die Bingokarte zu sehen").color(NamedTextColor.GRAY));
			lore.add(Component.text(" "));
			meta.lore(lore);
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		});
		item.addUnsafeEnchantment(Enchantment.LUCK, 1);
		return item;
	}
	
	public static ItemStack getItem() {
		return item;
	}
	
	@EventHandler
	public void onPlayerClickCard(PlayerInteractEvent event) {
		if(event.getItem() == null) {
			return;
		}
		if(!event.getItem().equals(item)) {
			return;
		}
		for(Arena arena : Arena.getArenas()) {
			if(!arena.getPlayers().contains(event.getPlayer())) {
				continue;
			}
			QuestGui.openGuiForPlayer(event.getPlayer());
			event.setCancelled(true);
			break;
		}
	}
	
	@EventHandler
	public void onPlayerClickEvent(InventoryClickEvent event) {
		if(event.getCurrentItem() == null) {
			return;
		}
		if(!event.getCurrentItem().equals(item)) {
			return;
		}
		for(Arena arena : Arena.getArenas()) {
			if(!arena.getPlayers().contains((Player)event.getWhoClicked())) {
				continue;
			}
			QuestGui.openGuiForPlayer((Player)event.getWhoClicked());
			event.setCancelled(true);
			break;
		}
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
					event.getPlayer().getInventory().setItem(8, item);
					break;
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerDrop(PlayerDropItemEvent event) {
		if(event.getItemDrop() == null) {
			return;
		}
		if(event.getItemDrop().getItemStack() == null) {
			return;
		}
		if(event.getItemDrop().getItemStack().equals(item)) {
			event.setCancelled(true);
		}
	}
}
