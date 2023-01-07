package com.github.peddach.bingoHost.teamSelector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.peddach.bingoHost.arena.BingoTeam;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class TeamUtil implements Listener {

    public static final HashMap<Integer, String> teamMappingsName = loadTeamNameMappings();
    public static final HashMap<Integer, Material> teamMappingsBeds = loadTeamBedMappings();
    public static final HashMap<Integer, String> teamMappingsNamedTextColor = loadTeamNamedTextColor();

    private static HashMap<Integer, String> loadTeamNameMappings() {
        HashMap<Integer, String> mappings = new HashMap<>();
        mappings.put(9, "Orange");
        mappings.put(7, "Lila");
        mappings.put(1, "Blau");
        mappings.put(0, "Rot");
        mappings.put(5, "Grün");
        mappings.put(4, "Hellblau");
        mappings.put(8, "Gelb");
        mappings.put(3, "Magenta");
        mappings.put(2, "Hellgrün");
        mappings.put(6, "Pink");
        return mappings;
    }

    private static HashMap<Integer, String> loadTeamNamedTextColor() {
        HashMap<Integer, String> mappings = new HashMap<>();
        mappings.put(9, "#e36e1b");
        mappings.put(7, "#7313bd");
        mappings.put(1, "#1316bd");
        mappings.put(0, "#a30707");
        mappings.put(5, "#2e7d0c");
        mappings.put(4, "#0aa2cc");
        mappings.put(8, "#ccbf0a");
        mappings.put(3, "#a12a97");
        mappings.put(2, "#1cd40f");
        mappings.put(6, "#db469b");
        return mappings;
    }

    private static HashMap<Integer, Material> loadTeamBedMappings() {
        HashMap<Integer, Material> mappings = new HashMap<>();
        mappings.put(9, Material.ORANGE_BED);
        mappings.put(7, Material.PURPLE_BED);
        mappings.put(1, Material.BLUE_BED);
        mappings.put(0, Material.RED_BED);
        mappings.put(5, Material.GREEN_BED);
        mappings.put(4, Material.LIGHT_BLUE_BED);
        mappings.put(8, Material.YELLOW_BED);
        mappings.put(3, Material.MAGENTA_BED);
        mappings.put(2, Material.LIME_BED);
        mappings.put(6, Material.PINK_BED);
        return mappings;
    }

    public static ItemStack getChooseTeamItem() {
        ItemStack item = new ItemStack(Material.GREEN_BED, 1);
        final ItemMeta meta = item.getItemMeta();
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(" "));
        lore.add(Component.text("Wähle dein Team", NamedTextColor.GRAY));
        lore.add(Component.text(" "));
        meta.lore(lore);
        meta.displayName(Component.text("Wähle dein Team", NamedTextColor.GOLD));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getTeamBedRepresentation(BingoTeam team) {
        ItemStack item = new ItemStack(TeamUtil.teamMappingsBeds.get(team.getNumber()), 1);
        Component name = Component.text(team.getName()).color(TextColor.fromCSSHexString(TeamUtil.teamMappingsNamedTextColor.get(team.getNumber())));
        final ItemMeta meta = item.getItemMeta();
        meta.displayName(name);
        final List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        if(team.isEmpty()){
            lore.add(Component.text("Leer").color(NamedTextColor.GRAY));
        }
        for(Player member : team.getMembers()){
            if(member == null){
                continue;
            }
            lore.add(Component.text(member.getName()).color(NamedTextColor.GRAY));
        }
        lore.add(Component.empty());
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
