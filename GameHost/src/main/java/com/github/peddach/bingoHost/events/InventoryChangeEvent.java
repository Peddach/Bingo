package com.github.peddach.bingoHost.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class InventoryChangeEvent extends Event{
	private static final HandlerList HANDLERS = new HandlerList();
	private ItemStack item;
	private Player player;
	
	public InventoryChangeEvent(ItemStack item, Player player){
		this.player = player;
		this.item = item;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	public ItemStack getItem() {
		return item;
	}
	public Player getPlayer() {
		return player;
	}
}
