package com.github.peddach.bingoHost.quest;

import java.util.ArrayList;
import java.util.List;

import com.github.peddach.bingoHost.GeneralSettings;
import com.github.peddach.bingoHost.teamSelector.TeamUtil;
import net.kyori.adventure.text.format.TextColor;
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

	private static final List<Inventory> guis = new ArrayList<>();
	private static final int[] questSlots = { 2, 3, 4, 5, 6, 11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42 };
	private static final int[] bedSlots = {0, 8, 9, 17, 18, 26, 27, 35, 36, 45};
	public static void openGuiForPlayer(Player player){
		openGuiForPlayer(player, null);
	}

	public static void openGuiForPlayer(Player player, BingoTeam bingoTeam) {
		Board board = null;
		Arena arena = null;
		BingoTeam team = bingoTeam;
		for (Arena iarena : Arena.getArenas()) {
			if (iarena.getPlayers().contains(player)) {
				for (BingoTeam iteam : iarena.getTeams()) {
					if (iteam.checkIfPlayerIsMember(player)) {
						arena = iarena;
						if(team == null){
							team = iteam;
						}
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
		Inventory inv = Bukkit.createInventory(null, 5 * 9, Component.text(team.getName()).color(TextColor.fromCSSHexString(TeamUtil.teamMappingsNamedTextColor.get(team.getNumber()))));
		for (int i = 0; i < questSlots.length; i++) {
			if (board.getQuest()[i].getType() == null) {
				inv.setItem(questSlots[i], errorItem("Fehler (Error: 52) " + i));
			}
			if (board.getQuest()[i].getType() == QuestType.BLOCK) {
				ItemStack item = new ItemStack(board.getQuest()[i].getBlock(), 1);
				if (board.getQuests()[i]) {
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
				inv.setItem(questSlots[i], item);
			}
			if (board.getQuest()[i].getType() == QuestType.ADCHIEVMENT) {
				ItemStack item = new ItemStack(Material.DRAGON_EGG, 1);
				final Advancement advancement = board.getQuest()[i].getAdvancement();
				if (board.getQuests()[i]) {
					item.addUnsafeEnchantment(Enchantment.LURE, 1);
					item.editMeta(meta -> {
						meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
						List<Component> lore = new ArrayList<>();
						lore.add(Component.text(" "));
						lore.add(Component.text("Erledigt").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
						lore.add(Component.text(" "));
						meta.lore(lore);
						Component advancementName = advancement.getDisplay().title();
						meta.displayName(Component.text("Advancement: ", NamedTextColor.GOLD).decorate(TextDecoration.BOLD).append(advancementName));
					});
				} else {
					item.editMeta(meta -> {
						Component advancementName = Component.text(AdvancementList.getInstance().getAdvancementTitleMappings().get(advancement));
						meta.displayName(Component.text("Advancement: ", NamedTextColor.GOLD).decorate(TextDecoration.BOLD).append(advancementName));
						List<Component> lore = new ArrayList<>();
						lore.add(Component.text(" "));
						lore.add(Component.text(AdvancementList.getInstance().getAdvancementDescriptionMappings().get(advancement)).color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC));
						lore.add(Component.text(" "));
						meta.lore(lore);
						meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
					});	
				}
				inv.setItem(questSlots[i], item);
			}
		}
		BingoTeam playerBingoTeam = null;
		for(BingoTeam iTeam : arena.getTeams()){
			if(iTeam.checkIfPlayerIsMember(player)){
				playerBingoTeam = iTeam;
			}
		}
		int currentTeamSlot = 0;
		for(BingoTeam iTeam : arena.getTeams()){
			if(iTeam.isEmpty()){
				continue;
			}
			ItemStack item = TeamUtil.getTeamBedRepresentation(iTeam);
			if (iTeam.equals(playerBingoTeam)) {
				item.editMeta(itemMeta -> {
					List<Component> lore = itemMeta.lore();
					lore.add(Component.text("Dein Team").color(NamedTextColor.GREEN).decorate(TextDecoration.ITALIC));
					itemMeta.lore(lore);
				});
			} else if(iTeam.equals(team)){
				item.editMeta(itemMeta -> {
					List<Component> lore = itemMeta.lore();
					lore.add(Component.text("ausgewählt").color(NamedTextColor.GOLD).decorate(TextDecoration.ITALIC));
					itemMeta.lore(lore);
				});
			}
			inv.setItem(bedSlots[currentTeamSlot], item);
			currentTeamSlot++;
		}
		openAndRegist(player, inv);
	}

	private static void openAndRegist(Player player, Inventory inv) {
		player.openInventory(inv);
		guis.add(inv);
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
		guis.remove(event.getInventory());
	}

	@EventHandler
	public void onPlayerInteractEvent(InventoryClickEvent event) {
		if (guis.contains(event.getInventory())) {
			event.setCancelled(true);
			if(arrayContains(questSlots, event.getRawSlot())) {
				RecipeShow.openRecipeForPlayer(event.getCurrentItem(), (Player) event.getWhoClicked());
				return;
			}
			if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || !arrayContains(bedSlots, event.getRawSlot())){
				return;
			}
			Arena arena = null;
			for(Arena i : Arena.getArenas()){
				if(i.getPlayers().contains((Player) event.getWhoClicked())){
					arena = i;
				}
			}
			if(arena == null){
				throw new NullPointerException("Player clicked QuestGUI, but is not member of any arena!");
			}
			BingoTeam team = null;
			for(int key : TeamUtil.teamMappingsBeds.keySet()){
				if(TeamUtil.teamMappingsBeds.get(key) != event.getCurrentItem().getType()){
					continue;
				}
				team = arena.getTeams()[key];
			}
			if(team == null){
				return;
			}
			final BingoTeam finalTeam = team;
			event.getWhoClicked().closeInventory();
			Bukkit.getScheduler().runTask(GeneralSettings.plugin, () -> openGuiForPlayer((Player) event.getWhoClicked(), finalTeam));
		}
	}

	private boolean arrayContains(int[] array, int value){
		for(int i : array){
			if(i == value){
				return true;
			}
		}
		return false;
	}
}
