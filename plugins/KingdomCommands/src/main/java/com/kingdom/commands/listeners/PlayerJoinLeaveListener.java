package com.kingdom.commands.listeners;

import com.kingdom.commands.KingdomCommands;
import com.kingdom.commands.utils.MessageUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinLeaveListener implements Listener {
    
    private final KingdomCommands plugin;
    
    public PlayerJoinLeaveListener(KingdomCommands plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Remove default join message
        event.joinMessage(null);
        
        // Send beautiful medieval join message to all players
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            MessageUtils.sendJoinMessage(player, event.getPlayer().getName());
        }
        
        // Send special welcome to the joining player
        MessageUtils.sendJoinMessage(event.getPlayer(), event.getPlayer().getName());
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Remove default quit message
        event.quitMessage(null);
        
        // Send beautiful medieval leave message to all remaining players
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (!player.equals(event.getPlayer())) {
                MessageUtils.sendLeaveMessage(player, event.getPlayer().getName());
            }
        }
    }
}
