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
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class RealisticWorld extends JavaPlugin implements Listener {
    
    private int cleanupInterval = 200; // 10 seconds
    
    @Override
    public void onEnable() {
        getLogger().info("Realistic World Plugin Enabled - Removing monsters and NPCs!");
        getServer().getPluginManager().registerEvents(this, this);
        
        // Load configuration
        getConfig().addDefault("realistic.cleanupInterval", 200);
        getConfig().addDefault("realistic.removeMonsters", true);
        getConfig().addDefault("realistic.removeVillagers", true);
        getConfig().addDefault("realistic.removeNPCs", true);
        getConfig().options().copyDefaults(true);
        saveConfig();
        
        cleanupInterval = getConfig().getInt("realistic.cleanupInterval", 200);
        
        // Start cleanup task
        startCleanupTask();
        
        getLogger().info("Realistic World cleanup started with interval: " + cleanupInterval + " ticks");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("Realistic World Plugin Disabled!");
    }
    
    private void startCleanupTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                cleanupWorld();
            }
        }.runTaskTimer(this, cleanupInterval, cleanupInterval);
    }
    
    private void cleanupWorld() {
        if (!getConfig().getBoolean("realistic.removeMonsters", true) && 
            !getConfig().getBoolean("realistic.removeVillagers", true) && 
            !getConfig().getBoolean("realistic.removeNPCs", true)) {
            return;
        }
        
        for (World world : getServer().getWorlds()) {
            List<Entity> entities = world.getEntities();
            int removed = 0;
            
            for (Entity entity : entities) {
                if (shouldRemoveEntity(entity)) {
                    entity.remove();
                    removed++;
                }
            }
            
            if (removed > 0) {
                getLogger().info("Cleaned up " + removed + " entities in world: " + world.getName());
            }
        }
    }
    
    private boolean shouldRemoveEntity(Entity entity) {
        EntityType type = entity.getType();
        
        // Remove monsters
        if (getConfig().getBoolean("realistic.removeMonsters", true)) {
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
        
        // Remove villagers and NPCs
        if (getConfig().getBoolean("realistic.removeVillagers", true)) {
            if (type == EntityType.VILLAGER || type == EntityType.WANDERING_TRADER) {
                return true;
            }
        }
        
        // Remove other NPCs
        if (getConfig().getBoolean("realistic.removeNPCs", true)) {
            if (type == EntityType.IRON_GOLEM || type == EntityType.SNOWMAN || type == EntityType.ARMOR_STAND) {
                return true;
            }
        }
        
        return false;
    }
    
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (shouldRemoveEntity(event.getEntity())) {
            event.setCancelled(true);
            getLogger().info("Prevented spawn of: " + event.getEntityType());
        }
    }
    
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (shouldRemoveEntity(event.getEntity())) {
            event.setCancelled(true);
            getLogger().info("Prevented spawn of: " + event.getEntityType());
        }
    }
    
    // Command to check world status
    public boolean onCommand(org.bukkit.command.CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("realistic")) {
            if (sender instanceof Player) {
                Player player = (Player) Player.class.cast(sender);
                
                player.sendMessage(ChatColor.GOLD + "🌍 Realistic World Status:");
                player.sendMessage(ChatColor.WHITE + "Monsters: " + 
                    (getConfig().getBoolean("realistic.removeMonsters", true) ? ChatColor.RED + "DISABLED" : ChatColor.GREEN + "ENABLED"));
                player.sendMessage(ChatColor.WHITE + "Villagers: " + 
                    (getConfig().getBoolean("realistic.removeVillagers", true) ? ChatColor.RED + "REMOVED" : ChatColor.GREEN + "ALLOWED"));
                player.sendMessage(ChatColor.WHITE + "NPCs: " + 
                    (getConfig().getBoolean("realistic.removeNPCs", true) ? ChatColor.RED + "REMOVED" : ChatColor.GREEN + "ALLOWED"));
                player.sendMessage(ChatColor.GRAY + "World is set to realistic survival mode.");
            } else {
                sender.sendMessage("Realistic World - Monsters and NPCs are disabled.");
            }
            return true;
        }
        return false;
    }
}
