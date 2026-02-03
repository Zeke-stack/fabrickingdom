package com.kingdom;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.ChatColor;

public class EarthWrap extends JavaPlugin implements Listener {
    
    private int worldSize = 30000; // 30k block diameter for Earth illusion
    private int halfSize = 15000; // Half of world size
    
    @Override
    public void onEnable() {
        getLogger().info("Earth Wrap Plugin Enabled - Creating Earth Illusion!");
        getServer().getPluginManager().registerEvents(this, this);
        
        // Load configuration
        getConfig().addDefault("earth.worldSize", 30000);
        getConfig().addDefault("earth.wrapMessage", true);
        getConfig().options().copyDefaults(true);
        saveConfig();
        
        worldSize = getConfig().getInt("earth.worldSize", 30000);
        halfSize = worldSize / 2;
    }
    
    @Override
    public void onDisable() {
        getLogger().info("Earth Wrap Plugin Disabled!");
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && 
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return; // Only looking for actual movement, not head movement
        }
        
        Player player = event.getPlayer();
        Location to = event.getTo();
        World world = to.getWorld();
        
        // Check X coordinate wrapping
        if (Math.abs(to.getX()) > halfSize) {
            double newX = wrapCoordinate(to.getX());
            Location newLocation = new Location(world, newX, to.getY(), to.getZ(), to.getYaw(), to.getPitch());
            
            if (getConfig().getBoolean("earth.wrapMessage", true)) {
                player.sendMessage(ChatColor.YELLOW + "🌍 You've traveled around the world!");
            }
            
            // Use teleport for smooth wrapping
            player.teleport(newLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
            event.setCancelled(true);
            return;
        }
        
        // Check Z coordinate wrapping
        if (Math.abs(to.getZ()) > halfSize) {
            double newZ = wrapCoordinate(to.getZ());
            Location newLocation = new Location(world, to.getX(), to.getY(), newZ, to.getYaw(), to.getPitch());
            
            if (getConfig().getBoolean("earth.wrapMessage", true)) {
                player.sendMessage(ChatColor.YELLOW + "🌍 You've traveled around the world!");
            }
            
            // Use teleport for smooth wrapping
            player.teleport(newLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
            event.setCancelled(true);
        }
    }
    
    private double wrapCoordinate(double coordinate) {
        if (coordinate > halfSize) {
            return -halfSize + (coordinate - halfSize);
        } else if (coordinate < -halfSize) {
            return halfSize - (-coordinate - halfSize);
        }
        return coordinate;
    }
    
    // Command to check current position relative to world
    public boolean onCommand(org.bukkit.command.CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("earth")) {
            if (sender instanceof Player) {
                Player player = (Player) Player.class.cast(sender);
                Location loc = player.getLocation();
                
                player.sendMessage(ChatColor.GOLD + "🌍 Earth Illusion World Info:");
                player.sendMessage(ChatColor.WHITE + "Position: " + ChatColor.AQUA + 
                    Math.round(loc.getX()) + ", " + Math.round(loc.getZ()));
                player.sendMessage(ChatColor.WHITE + "World Size: " + ChatColor.GREEN + worldSize + "x" + worldSize + " blocks");
                player.sendMessage(ChatColor.WHITE + "Distance to Edge: " + ChatColor.YELLOW + 
                    Math.max(0, halfSize - Math.max(Math.abs(loc.getX()), Math.abs(loc.getZ()))) + " blocks");
                player.sendMessage(ChatColor.GRAY + "You can travel around the world - it wraps around!");
            } else {
                sender.sendMessage("This command can only be used by players.");
            }
            return true;
        }
        return false;
    }
}
