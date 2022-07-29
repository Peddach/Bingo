package com.github.peddach.bingoLobby.listener;

import java.util.ArrayList;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.peddach.bingoHost.GeneralSettings;
import com.github.peddach.bingoHost.util.MessageUtil;
import com.github.peddach.bingoLobby.join.ArenaData;
import com.github.peddach.bingoLobby.join.PlayerConnector;

import net.kyori.adventure.text.Component;

public class EnterPortalListener implements Listener {

	private static ArrayList<Player> timeoutList = new ArrayList<>();

	@EventHandler
	public void onPlayerEnterPortalEventSingle(EntityPortalEnterEvent event) {
		int x1 = GeneralSettings.config.getInt("PortalSingle.X1");
		int x2 = GeneralSettings.config.getInt("PortalSingle.X2");
		int z1 = GeneralSettings.config.getInt("PortalSingle.Z1");
		int z2 = GeneralSettings.config.getInt("PortalSingle.Z2");
		if (event.getEntity() instanceof Player == false) {
			return;
		}
		Player player = (Player) event.getEntity();
		if (timeoutList.contains(player)) {
			return;
		}
		if (!((player.getLocation().getBlockX() == x1 && player.getLocation().getBlockZ() == z1) || (player.getLocation().getBlockX() == x2 && player.getLocation().getBlockZ() == z2))) {
			return;
		}
		addToblackList(player);
		if (ArenaData.getCurrentSignleArena() == null) {
			GeneralSettings.plugin.getMessageSender().sendMessage(player, Component.text("Es ist grade keine Arena frei! Bitte warte einen Moment und informiere später das Team").color(NamedTextColor.RED));
			return;
		}
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 5, false, false));
		new PlayerConnector(ArenaData.getCurrentSignleArena().getName(), player);
	}

	@EventHandler
	public void onPlayerEnterPortalEventTeam(EntityPortalEnterEvent event) {
		int x1 = GeneralSettings.config.getInt("PortalTeam.X1");
		int x2 = GeneralSettings.config.getInt("PortalTeam.X2");
		int z1 = GeneralSettings.config.getInt("PortalTeam.Z1");
		int z2 = GeneralSettings.config.getInt("PortalTeam.Z2");
		if (event.getEntity() instanceof Player == false) {
			return;
		}
		Player player = (Player) event.getEntity();
		if (timeoutList.contains(player)) {
			return;
		}
		if (!((player.getLocation().getBlockX() == x1 && player.getLocation().getBlockZ() == z1) || (player.getLocation().getBlockX() == x2 && player.getLocation().getBlockZ() == z2))) {
			return;
		}
		addToblackList(player);
		if (ArenaData.getCurrentTeamArena() == null) {
			GeneralSettings.plugin.getMessageSender().sendMessage(player, Component.text("Es ist grade keine Arena frei! Bitte warte einen Moment und informiere später das Team").color(NamedTextColor.RED));
			return;
		}
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 5, false, false));
		new PlayerConnector(ArenaData.getCurrentTeamArena().getName(), player);
	}
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		for(PotionEffect effect : event.getPlayer().getActivePotionEffects())
		{
		    event.getPlayer().removePotionEffect(effect.getType());
		}
	}

	private void addToblackList(Player player) {
		timeoutList.add(player);
		Bukkit.getScheduler().runTaskLater(GeneralSettings.plugin, () -> {
			timeoutList.remove(player);
			player.getActivePotionEffects().clear();
		}, 20 * 10);
	}
}
