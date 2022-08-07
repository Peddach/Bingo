package com.github.peddach.bingoHost.utilItems;

import java.util.ArrayList;
import java.util.List;

import com.github.peddach.bingoHost.GeneralSettings;
import net.kyori.adventure.audience.Audience;
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

import com.github.peddach.bingoHost.arena.Arena;
import com.github.peddach.bingoHost.arena.GameState;
import com.github.peddach.bingoHost.events.PlayerJoinArenaEvent;
import com.github.peddach.bingoHost.util.MessageUtil;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class StartItem implements Listener {
	private static final ItemStack item = createStartItem();

	private static ItemStack createStartItem() {
		ItemStack item = new ItemStack(Material.FIREWORK_STAR, 1);
		item.editMeta(meta -> {
			meta.displayName(Component.text("Spiel starten").color(NamedTextColor.GOLD));
			List<Component> lore = new ArrayList<>();
			lore.add(Component.text(" "));
			lore.add(Component.text("Klicke zum starten").color(NamedTextColor.GRAY));
			lore.add(Component.text(" "));
			meta.lore(lore);
		});

		FireworkEffectMeta firemeta = (FireworkEffectMeta) item.getItemMeta();
		firemeta.setEffect(FireworkEffect.builder().withColor(Color.ORANGE).build());
		item.setItemMeta(firemeta);
		return item;
	}

	@EventHandler
	public void onPlayerClickLeaveItemEvent(PlayerInteractEvent event) {
		if (event.getItem() == null) {
			return;
		}
		if (!event.getItem().equals(item)) {
			return;
		}
		startGame(event.getPlayer());
	}

	@EventHandler
	public void onPlayerClickLeaveItem(InventoryClickEvent event) {
		if (event.getCurrentItem() == null) {
			return;
		}
		if (!event.getCurrentItem().equals(item)) {
			return;
		}
		startGame((Player) event.getWhoClicked());
	}
	
	private void startGame(Player player) {
		if(!player.hasPermission("Bingo.start")) {
			return;
		}
		for(Arena arena : Arena.getArenas()) {
			if(!arena.getPlayers().contains(player)) {
				continue;
			}
			if(arena.getCountDown() == null) {
				GeneralSettings.plugin.getMessageUtil().sendMessage(player, Component.text("Es läuft grade kein Countdown").color(NamedTextColor.RED));
				return;
			}
			if(arena.getCountDown().getCountDown() < 10) {
				GeneralSettings.plugin.getMessageUtil().sendMessage(player, Component.text("Du kannst jetzt nicht starten").color(NamedTextColor.RED));
				return;
			}
			if(arena.getCountDown().isForceStarted()) {
				GeneralSettings.plugin.getMessageUtil().sendMessage(player, Component.text("Der Countdown wurde bereits verkürzt").color(NamedTextColor.RED));
				return;
			}
			arena.getCountDown().setCountDown(11);
			GeneralSettings.plugin.getMessageUtil().broadcastMessage(Audience.audience(arena.getPlayers()), player.displayName().append(Component.text(" hat den Countdown verkürzt").color(NamedTextColor.GRAY)));
		}
	}

	@EventHandler
	public void onPlayerJoinArenaEvent(PlayerJoinArenaEvent event) {
		if (event.getArena().getGameState() == GameState.WAITING || event.getArena().getGameState() == GameState.STARTING) {
			if(event.getPlayer().hasPermission("Bingo.start")) {
				event.getPlayer().getInventory().setItem(4, item);
			}
		}
	}
}
