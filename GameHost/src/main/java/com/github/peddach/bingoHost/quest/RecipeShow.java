package com.github.peddach.bingoHost.quest;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import com.github.peddach.bingoHost.GeneralSettings;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class RecipeShow implements Listener{
	
	private static final List<Inventory> invs = new ArrayList<>();
	
	public RecipeShow() {
		
	}
	
	public RecipeShow(Player player, Recipe recipe) {
		if(recipe instanceof FurnaceRecipe furnaceRecipe) {
			Inventory inv = createInv(InventoryType.FURNACE);
			inv.setItem(0, furnaceRecipe.getInput());
			inv.setItem(2, furnaceRecipe.getResult());
			invs.add(inv);
			player.openInventory(inv);
		}
		if(recipe instanceof ShapedRecipe shapedRecipe) {
			Inventory inv = createInv(InventoryType.DROPPER);
			String[] shape = shapedRecipe.getShape();
			for(int c = 0 ; c < shape.length; c ++) {
				String string = shape[c];
				char[] chars = string.toCharArray();
				for(int i = 0; i < 3; i++) {
					if(i >= chars.length) {
						break;
					}
					inv.setItem(3*c + i, shapedRecipe.getIngredientMap().get(chars[i]));
				}
			}
			invs.add(inv);
			player.openInventory(inv);
		}
		if(recipe instanceof ShapelessRecipe shapelessRecipe) {
			Inventory inv = createInv(InventoryType.DROPPER);
			for(int i = 0; i < shapelessRecipe.getIngredientList().size(); i++) {
				inv.setItem(i, shapelessRecipe.getIngredientList().get(i));
			}
			player.openInventory(inv);
			invs.add(inv);
		}
	}
	
	private Inventory createInv(InventoryType invType) {
		return Bukkit.createInventory(null , invType, Component.text("Craftingrezept").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD, TextDecoration.ITALIC));
	}
	
	@EventHandler 
	public void onPlayerCloseInvEvent(InventoryCloseEvent event){
		if(!invs.contains(event.getInventory())) {
			return;
		}
		invs.remove(event.getInventory());
		Bukkit.getScheduler().runTaskLater(GeneralSettings.plugin, () -> {
			QuestGui.openGuiForPlayer((Player)event.getPlayer());
		}, 1);
	}
	
	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent event) {
		if(!invs.contains(event.getInventory())) {
			return;
		}
		event.setCancelled(true);
	}
}
