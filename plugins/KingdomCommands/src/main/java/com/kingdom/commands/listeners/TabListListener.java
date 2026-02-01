package com.kingdom.commands.listeners;

import com.kingdom.commands.KingdomCommands;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TabListListener implements Listener {
    
    private final KingdomCommands plugin;
    
    public TabListListener(KingdomCommands plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updatePlayerTabList(event.getPlayer());
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Nothing needed here
    }
    
    public void startTabUpdater() {
        // Update tab list every 5 seconds
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                updatePlayerTabList(player);
            }
        }, 20L, 100L); // Start after 1 second, repeat every 5 seconds
    }
    
    private void updatePlayerTabList(Player player) {
        int coins = plugin.getCoinManager().countAllGold(player);
        String coinDisplay = "§6✦ " + coins + " Coins";
        
        // Set player list name with coins
        String displayName = player.getName();
        String newListName = "§e" + displayName + " §7| " + coinDisplay;
        
        // Limit to 16 characters for display name
        if (newListName.length() > 16) {
            newListName = newListName.substring(0, 16);
        }
        
        player.setPlayerListName(newListName);
        
        // Also update the header and footer
        String header = "§6§l✦ Kingdom Server ✦\n§eMedieval Roleplay";
        String footer = "§7Balance: " + plugin.getCoinManager().formatCoins(coins) + "\n§7Online: " + plugin.getServer().getOnlinePlayers().size();
        
        player.setPlayerListHeaderFooter(header, footer);
    }
}
