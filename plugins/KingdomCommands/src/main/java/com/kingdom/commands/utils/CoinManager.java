package com.kingdom.commands.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CoinManager {
    
    private final KingdomCommands plugin;
    
    public CoinManager(KingdomCommands plugin) {
        this.plugin = plugin;
    }
    
    public int countAllGold(Player player) {
        int totalCoins = 0;
        
        // Count gold in inventory
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.GOLD_NUGGET) {
                totalCoins += item.getAmount();
            } else if (item != null && item.getType() == Material.GOLD_INGOT) {
                totalCoins += item.getAmount() * 9;
            } else if (item != null && item.getType() == Material.GOLD_BLOCK) {
                totalCoins += item.getAmount() * 81;
            }
        }
        
        // Count gold in ender chest
        for (ItemStack item : player.getEnderChest().getContents()) {
            if (item != null && item.getType() == Material.GOLD_NUGGET) {
                totalCoins += item.getAmount();
            } else if (item != null && item.getType() == Material.GOLD_INGOT) {
                totalCoins += item.getAmount() * 9;
            } else if (item != null && item.getType() == Material.GOLD_BLOCK) {
                totalCoins += item.getAmount() * 81;
            }
        }
        
        return totalCoins;
    }
    
    public String formatCoins(int amount) {
        return "§e" + amount + "§6✦";
    }
}
