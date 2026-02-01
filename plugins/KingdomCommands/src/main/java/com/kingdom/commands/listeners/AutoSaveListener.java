package com.kingdom.commands.listeners;

import com.kingdom.commands.KingdomCommands;
import com.kingdom.commands.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

public class AutoSaveListener implements Listener {
    
    private final KingdomCommands plugin;
    private int saveTaskId;
    
    public AutoSaveListener(KingdomCommands plugin) {
        this.plugin = plugin;
        startAutoSave();
    }
    
    private void startAutoSave() {
        // Auto-save every 60 seconds (1200 ticks)
        saveTaskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            performAutoSave();
        }, 1200L, 1200L).getTaskId();
        
        plugin.getLogger().info("Auto-save started: saves every 60 seconds");
    }
    
    private void performAutoSave() {
        try {
            // Save all worlds
            Bukkit.getWorlds().forEach(world -> {
                world.save();
                plugin.getLogger().info("Auto-saved world: " + world.getName());
            });
            
            // Save player data
            Bukkit.getOnlinePlayers().forEach(Player::saveData);
            
            // Notify admins about auto-save (optional - can be disabled for less spam)
            if (plugin.getConfig().getBoolean("auto-save.notify-admins", false)) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission("kingdom.commands.admin")) {
                        player.sendMessage(MessageUtils.colorize("&7[Auto-Save] World data saved successfully"));
                    }
                }
            }
            
        } catch (Exception e) {
            plugin.getLogger().warning("Auto-save failed: " + e.getMessage());
        }
    }
    
    public void stopAutoSave() {
        if (saveTaskId != -1) {
            Bukkit.getScheduler().cancelTask(saveTaskId);
            saveTaskId = -1;
        }
    }
    
    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        // Handle manual save commands
        if (event.getCommand().equalsIgnoreCase("save-all")) {
            performAutoSave();
        }
    }
}
