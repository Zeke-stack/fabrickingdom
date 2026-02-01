package com.kingdom.commands.listeners;

import com.kingdom.commands.KingdomCommands;
import com.kingdom.commands.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CoinEconomyListener implements Listener {
    
    private final KingdomCommands plugin;
    
    public CoinEconomyListener(KingdomCommands plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        // Check if player is holding gold and right-clicking
        if (event.getAction().toString().contains("RIGHT_CLICK") && 
            (item.getType() == Material.GOLD_NUGGET || 
             item.getType() == Material.GOLD_INGOT || 
             item.getType() == Material.GOLD_BLOCK)) {
            
            // Prevent placing blocks if sneaking
            if (player.isSneaking() && item.getType() == Material.GOLD_BLOCK) {
                return;
            }
            
            // Show coin value
            int value = 0;
            String itemName = "";
            
            if (item.getType() == Material.GOLD_NUGGET) {
                value = 1;
                itemName = "Gold Nugget";
            } else if (item.getType() == Material.GOLD_INGOT) {
                value = 9;
                itemName = "Gold Ingot";
            } else if (item.getType() == Material.GOLD_BLOCK) {
                value = 81;
                itemName = "Gold Block";
            }
            
            player.sendMessage(MessageUtils.colorize("&6✦ " + itemName + " = &e" + value + " &6coins"));
            player.sendMessage(MessageUtils.colorize("&7Total coins: " + plugin.getCoinManager().formatCoins(plugin.getCoinManager().countAllGold(player))));
        }
    }
}
