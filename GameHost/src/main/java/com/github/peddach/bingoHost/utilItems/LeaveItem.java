package com.github.peddach.bingoHost.utilItems;

import java.util.ArrayList;
import java.util.List;

import com.github.peddach.bingoHost.GeneralSettings;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;

import com.github.peddach.bingoHost.arena.GameState;
import com.github.peddach.bingoHost.events.PlayerJoinArenaEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class LeaveItem implements Listener{
	
	private static final ItemStack item = createLeaveItem();
	
	private static ItemStack createLeaveItem() {
		ItemStack item = new ItemStack(Material.FIREWORK_STAR, 1);
		item.editMeta(meta -> {
			meta.displayName(Component.text("Verlassen").color(NamedTextColor.RED));
			List<Component> lore = new ArrayList<>();
			lore.add(Component.text(" "));
			lore.add(Component.text("Klicke zum verlassen").color(NamedTextColor.RED));
			lore.add(Component.text(" "));
			meta.lore(lore);
		});
		
		FireworkEffectMeta firemeta = (FireworkEffectMeta) item.getItemMeta();
		firemeta.setEffect(FireworkEffect.builder().withColor(Color.RED).build());
		item.setItemMeta(firemeta);
		return item;
	}
	
	@EventHandler
	public void onPlayerClickLeaveItemEvent(PlayerInteractEvent event) {
		if(event.getItem() == null) {
			return;
		}
		if(!event.getItem().equals(item)) {
			return;
		}
		GeneralSettings.plugin.getCloudNetAdapter().sendPlayerToLobby(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerClickLeaveItem(InventoryClickEvent event) {
		if(event.getCurrentItem() == null) {
			return;
		}
		if(!event.getCurrentItem().equals(item)) {
			return;
		}
		GeneralSettings.plugin.getCloudNetAdapter().sendPlayerToLobby((Player) event.getWhoClicked());
	}
	
	@EventHandler
	public void onPlayerJoinArenaEvent(PlayerJoinArenaEvent event) {
		if(event.getArena().getGameState() == GameState.WAITING || event.getArena().getGameState() == GameState.STARTING) {
			event.getPlayer().getInventory().setItem(8, item);
		}
	}
}
