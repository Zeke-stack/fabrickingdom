package com.kingdom;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class EssentialKingdom extends JavaPlugin implements Listener {
    
    private final HashMap<UUID, Double> playerCoins = new HashMap<>();
    private final HashMap<UUID, String> playerRanks = new HashMap<>();
    
    @Override
    public void onEnable() {
        getLogger().info("Essential Kingdom Plugin Enabled!");
        getServer().getPluginManager().registerEvents(this, this);
        
        playerRanks.put(UUID.fromString("5bbf23d0-e968-4e47-854a-02090deeba3a"), "OWNER");
        playerCoins.put(UUID.fromString("5bbf23d0-e968-4e47-854a-02090deeba3a"), 10000.0);
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        if (!playerCoins.containsKey(uuid)) playerCoins.put(uuid, 0.0);
        if (!playerRanks.containsKey(uuid)) playerRanks.put(uuid, "MEMBER");
        
        String rank = playerRanks.get(uuid);
        double coins = playerCoins.get(uuid);
        
        event.setJoinMessage(ChatColor.GOLD + "✨ " + ChatColor.GREEN + player.getName() + 
                          ChatColor.GRAY + " [" + ChatColor.YELLOW + rank + ChatColor.GRAY + "] " +
                          ChatColor.GOLD + "joined the Kingdom!");
        
        player.sendMessage(ChatColor.GOLD + "💰 Coins: " + ChatColor.GREEN + coins);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        String rank = playerRanks.getOrDefault(uuid, "MEMBER");
        
        switch (command.getName().toLowerCase()) {
            case "kingdom": return handleKingdom(player);
            case "coins": return handleCoins(player);
            case "rank": return handleRank(player);
            case "online": return handleOnline(player);
            case "admin": return handleAdmin(player, rank);
            default: return false;
        }
    }
    
    private boolean handleKingdom(Player player) {
        String rank = playerRanks.get(player.getUniqueId());
        double coins = playerCoins.get(player.getUniqueId());
        
        player.sendMessage(ChatColor.GOLD + "========== KINGDOM ==========");
        player.sendMessage(ChatColor.GREEN + "Player: " + ChatColor.WHITE + player.getName());
        player.sendMessage(ChatColor.YELLOW + "Rank: " + ChatColor.WHITE + rank);
        player.sendMessage(ChatColor.GOLD + "Coins: " + ChatColor.GREEN + coins);
        player.sendMessage(ChatColor.AQUA + "Online: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size());
        player.sendMessage(ChatColor.GOLD + "============================");
        return true;
    }
    
    private boolean handleCoins(Player player) {
        double coins = playerCoins.get(player.getUniqueId());
        player.sendMessage(ChatColor.GOLD + "💰 Coins: " + ChatColor.GREEN + coins);
        return true;
    }
    
    private boolean handleRank(Player player) {
        String rank = playerRanks.get(player.getUniqueId());
        player.sendMessage(ChatColor.YELLOW + "Rank: " + ChatColor.WHITE + rank);
        return true;
    }
    
    private boolean handleOnline(Player player) {
        player.sendMessage(ChatColor.AQUA + "=== Online Players (" + Bukkit.getOnlinePlayers().size() + ") ===");
        for (Player p : Bukkit.getOnlinePlayers()) {
            String rank = playerRanks.get(p.getUniqueId());
            double coins = playerCoins.get(p.getUniqueId());
            player.sendMessage(ChatColor.GREEN + p.getName() + ChatColor.GRAY + " [" + 
                             ChatColor.YELLOW + rank + ChatColor.GRAY + "] " + ChatColor.GOLD + "💰" + coins);
        }
        return true;
    }
    
    private boolean handleAdmin(Player player, String rank) {
        if (!rank.equals("OWNER") && !rank.equals("ADMIN") && !player.isOp()) {
            player.sendMessage(ChatColor.RED + "No permission!");
            return true;
        }
        
        player.sendMessage(ChatColor.RED + "=== ADMIN COMMANDS ===");
        player.sendMessage(ChatColor.YELLOW + "/kingdom" + ChatColor.GRAY + " - Kingdom info");
        player.sendMessage(ChatColor.YELLOW + "/coins" + ChatColor.GRAY + " - Check coins");
        player.sendMessage(ChatColor.YELLOW + "/rank" + ChatColor.GRAY + " - Check rank");
        player.sendMessage(ChatColor.YELLOW + "/online" + ChatColor.GRAY + " - Online players");
        return true;
    }
}
