package com.github.peddach.bingoHost;

import org.bukkit.entity.Player;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.dytanic.cloudnet.wrapper.Wrapper;

public class CloudNetAdapter {

	public static String getServerInstanceName() {
		return Wrapper.getInstance().getCurrentServiceInfoSnapshot().getName();
	}

	public static void sendPlayerToLobbyTask(Player player) {
		IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
		playerManager.getPlayerExecutor(playerManager.getOnlinePlayer(player.getUniqueId())).connectToFallback();
	}

}
