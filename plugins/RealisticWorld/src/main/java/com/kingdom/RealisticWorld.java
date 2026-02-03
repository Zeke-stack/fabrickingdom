package com.kingdom;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.ChatColor;

public class RealisticWorld extends JavaPlugin implements Listener {
    
    @Override
    public void onEnable() {
        getLogger().info("Realistic World Plugin Enabled - Preventing monster and NPC spawns!");
        getServer().getPluginManager().registerEvents(this, this);
        
        // Load configuration
        getConfig().addDefault("realistic.preventMonsters", true);
        getConfig().addDefault("realistic.preventVillagers", true);
        getConfig().addDefault("realistic.preventNPCs", true);
        getConfig().options().copyDefaults(true);
        saveConfig();
        
        getLogger().info("Realistic World spawn prevention active");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("Realistic World Plugin Disabled!");
    }
    
    private boolean shouldPreventSpawn(Entity entity) {
        EntityType type = entity.getType();
        
        // Prevent monsters
        if (getConfig().getBoolean("realistic.preventMonsters", true)) {
            if (type == EntityType.ZOMBIE || type == EntityType.SKELETON || type == EntityType.CREEPER ||
                type == EntityType.SPIDER || type == EntityType.ENDERMAN || type == EntityType.WITCH ||
                type == EntityType.SLIME || type == EntityType.MAGMA_CUBE || type == EntityType.BLAZE ||
                type == EntityType.GHAST || type == EntityType.PIGLIN || type == EntityType.HOGLIN ||
                type == EntityType.ZOGLIN || type == EntityType.PILLAGER || type == EntityType.RAVAGER ||
                type == EntityType.VINDICATOR || type == EntityType.EVOKER || type == EntityType.VEX ||
                type == EntityType.GUARDIAN || type == EntityType.ELDER_GUARDIAN || type == EntityType.SHULKER ||
                type == EntityType.PHANTOM || type == EntityType.DROWNED || type == EntityType.HUSK ||
                type == EntityType.STRAY || type == EntityType.WITHER_SKELETON || type == EntityType.CAVE_SPIDER ||
                type == EntityType.SILVERFISH || type == EntityType.ENDERMITE || type == EntityType.POLAR_BEAR) {
                return true;
            }
        }
        
        // Prevent villagers and NPCs
        if (getConfig().getBoolean("realistic.preventVillagers", true)) {
            if (type == EntityType.VILLAGER || type == EntityType.WANDERING_TRADER) {
                return true;
            }
        }
        
        // Prevent other NPCs
        if (getConfig().getBoolean("realistic.preventNPCs", true)) {
            if (type == EntityType.IRON_GOLEM || type == EntityType.SNOW_GOLEM || type == EntityType.ARMOR_STAND) {
                return true;
            }
        }
        
        return false;
    }
    
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (shouldPreventSpawn(event.getEntity())) {
            event.setCancelled(true);
            // No logging - silent prevention
        }
    }
    
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (shouldPreventSpawn(event.getEntity())) {
            event.setCancelled(true);
            // No logging - silent prevention
        }
    }
    
    // Command to check world status
    public boolean onCommand(org.bukkit.command.CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("realistic")) {
            if (sender instanceof Player) {
                Player player = (Player) Player.class.cast(sender);
                
                player.sendMessage(ChatColor.GOLD + "🌍 Realistic World Status:");
                player.sendMessage(ChatColor.WHITE + "Monsters: " + 
                    (getConfig().getBoolean("realistic.preventMonsters", true) ? ChatColor.RED + "PREVENTED" : ChatColor.GREEN + "ALLOWED"));
                player.sendMessage(ChatColor.WHITE + "Villagers: " + 
                    (getConfig().getBoolean("realistic.preventVillagers", true) ? ChatColor.RED + "PREVENTED" : ChatColor.GREEN + "ALLOWED"));
                player.sendMessage(ChatColor.WHITE + "NPCs: " + 
                    (getConfig().getBoolean("realistic.preventNPCs", true) ? ChatColor.RED + "PREVENTED" : ChatColor.GREEN + "ALLOWED"));
                player.sendMessage(ChatColor.GRAY + "World is set to realistic survival mode.");
            } else {
                sender.sendMessage("Realistic World - Monsters and NPCs are prevented from spawning.");
            }
            return true;
        }
        return false;
    }
}
