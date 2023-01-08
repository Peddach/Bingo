package com.github.peddach.bingoHost.quest;

import com.github.peddach.bingoHost.GeneralSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RecipeShow implements Listener{
	
	private static final List<Inventory> invs = new ArrayList<>();
	public RecipeShow() {}
	
	public RecipeShow(Player player, Recipe recipe) {
		if(recipe instanceof FurnaceRecipe furnaceRecipe) {
			Inventory inv = createInv(InventoryType.FURNACE);
			inv.setItem(0, furnaceRecipe.getInput());
			inv.setItem(2, furnaceRecipe.getResult());
			invs.add(inv);
			openInv(player, inv);
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
			openInv(player, inv);
		}
		if(recipe instanceof ShapelessRecipe shapelessRecipe) {
			Inventory inv = createInv(InventoryType.DROPPER);
			for(int i = 0; i < shapelessRecipe.getIngredientList().size(); i++) {
				inv.setItem(i, shapelessRecipe.getIngredientList().get(i));
			}
			openInv(player, inv);
		}
	}

	private void openInv(Player player, Inventory inventory){
		invs.add(inventory);
		player.openInventory(inventory);
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
		if(event.getReason() == InventoryCloseEvent.Reason.OPEN_NEW) {
			return;
		}
		Bukkit.getScheduler().runTaskLater(GeneralSettings.plugin, () -> QuestGui.openGuiForPlayer((Player) event.getPlayer()), 1);
	}
	
	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent event) {
		if(!invs.contains(event.getInventory())) {
			return;
		}
		event.setCancelled(true);
		openRecipeForPlayer(event.getCurrentItem(), (Player) event.getWhoClicked());
	}

	public static void openRecipeForPlayer(ItemStack item, Player player) {
		if(item == null || item.getType() == Material.DRAGON_EGG || item.getType() == Material.AIR) {
			return;
		}
		List<Recipe> recipeList = new ArrayList<>(Bukkit.getServer().getRecipesFor(new ItemStack(item.getType())));
		if(Bukkit.getServer().getRecipesFor(new ItemStack(item.getType())).isEmpty()) {
			GeneralSettings.plugin.getMessageUtil().sendMessage(player, Component.text("Das Item ").color(NamedTextColor.GRAY).append(item.displayName().color(NamedTextColor.GOLD).append(Component.text(" besitzt kein Rezept").color(NamedTextColor.GRAY))));
			return;
		}
		Random random = new Random();
		Recipe recipe = recipeList.get(random.nextInt(0, recipeList.size()));
		new RecipeShow(player, recipe);
	}

}
