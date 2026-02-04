package com.kingdom;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
        
        // Initialize Lottcha as OWNER with 10k coins
        playerRanks.put(UUID.fromString("5bbf23d0-e968-4e47-854a-02090deeba3a"), "OWNER");
        playerCoins.put(UUID.fromString("5bbf23d0-e968-4e47-854a-02090deeba3a"), 10000.0);
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        // Initialize player data
        if (!playerCoins.containsKey(uuid)) playerCoins.put(uuid, 0.0);
        if (!playerRanks.containsKey(uuid)) playerRanks.put(uuid, "MEMBER");
        
        // Auto-detect gold in inventory
        updateCoinsFromInventory(player);
        
        String rank = playerRanks.get(uuid);
        double coins = playerCoins.get(uuid);
        
        event.setJoinMessage(ChatColor.GOLD + "✨ " + ChatColor.GREEN + player.getName() + 
                          ChatColor.GRAY + " [" + ChatColor.YELLOW + rank + ChatColor.GRAY + "] " +
                          ChatColor.GOLD + "joined the Kingdom!");
        
        player.sendMessage(ChatColor.GOLD + "💰 Coins: " + ChatColor.GREEN + coins);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(ChatColor.GRAY + player.getName() + " left the Kingdom");
    }
    
    private void updateCoinsFromInventory(Player player) {
        UUID uuid = player.getUniqueId();
        double currentCoins = playerCoins.getOrDefault(uuid, 0.0);
        
        int goldNuggets = 0, goldIngots = 0, goldBlocks = 0;
        
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                if (item.getType() == Material.GOLD_NUGGET) goldNuggets += item.getAmount();
                else if (item.getType() == Material.GOLD_INGOT) goldIngots += item.getAmount();
                else if (item.getType() == Material.GOLD_BLOCK) goldBlocks += item.getAmount();
            }
        }
        
        double totalGoldValue = (goldNuggets * 1.0) + (goldIngots * 9.0) + (goldBlocks * 81.0);
        
        if (totalGoldValue > currentCoins) {
            playerCoins.put(uuid, totalGoldValue);
            player.sendMessage(ChatColor.GOLD + "💰 Coins updated: " + ChatColor.GREEN + totalGoldValue + 
                             ChatColor.GRAY + " (+" + (totalGoldValue - currentCoins) + ")");
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        String rank = playerRanks.getOrDefault(uuid, "MEMBER");
        
        // Update coins from inventory before commands
        updateCoinsFromInventory(player);
        
        switch (command.getName().toLowerCase()) {
            case "kingdom": return handleKingdom(player);
            case "coins": return handleCoins(player);
            case "rank": return handleRank(player);
            case "online": return handleOnline(player);
            case "menu": return handleMenu(player, rank);
            case "admin": return handleAdmin(player, rank);
            case "givecoins": return handleGiveCoins(player, args, rank);
            case "setrank": return handleSetRank(player, args, rank);
            case "fly": return handleFly(player, rank);
            case "god": return handleGod(player, rank);
            case "tp": return handleTeleport(player, args, rank);
            case "spawn": return handleSpawn(player);
            case "clear": return handleClear(player, rank);
            case "heal": return handleHeal(player, rank);
            case "inventory": return handleInventory(player, args, rank);
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
        updateCoinsFromInventory(player);
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
    
    private boolean handleMenu(Player player, String rank) {
        if (!rank.equals("OWNER") && !rank.equals("ADMIN") && !player.isOp()) {
            player.sendMessage(ChatColor.RED + "No permission!");
            return true;
        }
        
        Inventory menu = Bukkit.createInventory(null, 27, ChatColor.RED + "Admin Menu");
        
        // Fly toggle
        ItemStack fly = new ItemStack(Material.FEATHER);
        ItemMeta flyMeta = fly.getItemMeta();
        flyMeta.setDisplayName(ChatColor.GREEN + "Toggle Fly");
        fly.setItemMeta(flyMeta);
        menu.setItem(0, fly);
        
        // God mode
        ItemStack god = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemMeta godMeta = god.getItemMeta();
        godMeta.setDisplayName(ChatColor.AQUA + "Toggle God Mode");
        god.setItemMeta(godMeta);
        menu.setItem(1, god);
        
        // Heal all
        ItemStack heal = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta healMeta = heal.getItemMeta();
        healMeta.setDisplayName(ChatColor.RED + "Heal All Players");
        heal.setItemMeta(healMeta);
        menu.setItem(2, heal);
        
        player.openInventory(menu);
        player.sendMessage(ChatColor.GREEN + "Admin menu opened!");
        return true;
    }
    
    private boolean handleAdmin(Player player, String rank) {
        if (!rank.equals("OWNER") && !rank.equals("ADMIN") && !player.isOp()) {
            player.sendMessage(ChatColor.RED + "No permission!");
            return true;
        }
        
        player.sendMessage(ChatColor.RED + "=== ADMIN COMMANDS ===");
        player.sendMessage(ChatColor.YELLOW + "/menu" + ChatColor.GRAY + " - Admin menu");
        player.sendMessage(ChatColor.YELLOW + "/inventory <player>" + ChatColor.GRAY + " - View inventory");
        player.sendMessage(ChatColor.YELLOW + "/givecoins <player> <amount>" + ChatColor.GRAY + " - Give coins");
        player.sendMessage(ChatColor.YELLOW + "/setrank <player> <rank>" + ChatColor.GRAY + " - Set rank");
        player.sendMessage(ChatColor.YELLOW + "/fly" + ChatColor.GRAY + " - Toggle fly");
        player.sendMessage(ChatColor.YELLOW + "/god" + ChatColor.GRAY + " - Toggle god mode");
        player.sendMessage(ChatColor.YELLOW + "/tp <player>" + ChatColor.GRAY + " - Teleport");
        player.sendMessage(ChatColor.YELLOW + "/spawn" + ChatColor.GRAY + " - Go to spawn");
        player.sendMessage(ChatColor.YELLOW + "/clear" + ChatColor.GRAY + " - Clear inventory");
        player.sendMessage(ChatColor.YELLOW + "/heal" + ChatColor.GRAY + " - Heal");
        return true;
    }
    
    private boolean handleGiveCoins(Player player, String[] args, String rank) {
        if (!rank.equals("OWNER") && !rank.equals("ADMIN") && !player.isOp()) {
            player.sendMessage(ChatColor.RED + "No permission!");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /givecoins <player> <amount>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }
        
        try {
            double amount = Double.parseDouble(args[1]);
            UUID targetUuid = target.getUniqueId();
            playerCoins.put(targetUuid, playerCoins.getOrDefault(targetUuid, 0.0) + amount);
            
            player.sendMessage(ChatColor.GREEN + "Gave " + amount + " coins to " + target.getName());
            target.sendMessage(ChatColor.GOLD + "💰 Received " + amount + " coins from " + player.getName());
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid amount!");
        }
        return true;
    }
    
    private boolean handleSetRank(Player player, String[] args, String rank) {
        if (!rank.equals("OWNER")) {
            player.sendMessage(ChatColor.RED + "Only OWNER can set ranks!");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /setrank <player> <rank>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }
        
        String newRank = args[1].toUpperCase();
        playerRanks.put(target.getUniqueId(), newRank);
        
        player.sendMessage(ChatColor.GREEN + "Set " + target.getName() + "'s rank to " + newRank);
        target.sendMessage(ChatColor.GOLD + "Rank updated: " + ChatColor.YELLOW + newRank);
        return true;
    }
    
    private boolean handleFly(Player player, String rank) {
        if (!rank.equals("OWNER") && !rank.equals("ADMIN") && !player.isOp()) {
            player.sendMessage(ChatColor.RED + "No permission!");
            return true;
        }
        
        boolean flying = !player.getAllowFlight();
        player.setAllowFlight(flying);
        player.setFlying(flying);
        player.sendMessage(ChatColor.GREEN + "Fly " + (flying ? "enabled" : "disabled"));
        return true;
    }
    
    private boolean handleGod(Player player, String rank) {
        if (!rank.equals("OWNER") && !rank.equals("ADMIN") && !player.isOp()) {
            player.sendMessage(ChatColor.RED + "No permission!");
            return true;
        }
        
        boolean god = !player.isInvulnerable();
        player.setInvulnerable(god);
        player.sendMessage(ChatColor.AQUA + "God mode " + (god ? "enabled" : "disabled"));
        return true;
    }
    
    private boolean handleTeleport(Player player, String[] args, String rank) {
        if (!rank.equals("OWNER") && !rank.equals("ADMIN") && !player.isOp()) {
            player.sendMessage(ChatColor.RED + "No permission!");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /tp <player>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }
        
        player.teleport(target.getLocation());
        player.sendMessage(ChatColor.GREEN + "Teleported to " + target.getName());
        return true;
    }
    
    private boolean handleSpawn(Player player) {
        player.teleport(player.getWorld().getSpawnLocation());
        player.sendMessage(ChatColor.GREEN + "Teleported to spawn!");
        return true;
    }
    
    private boolean handleClear(Player player, String rank) {
        if (!rank.equals("OWNER") && !rank.equals("ADMIN") && !player.isOp()) {
            player.sendMessage(ChatColor.RED + "No permission!");
            return true;
        }
        
        player.getInventory().clear();
        player.sendMessage(ChatColor.GREEN + "Inventory cleared!");
        return true;
    }
    
    private boolean handleHeal(Player player, String rank) {
        if (!rank.equals("OWNER") && !rank.equals("ADMIN") && !player.isOp()) {
            player.sendMessage(ChatColor.RED + "No permission!");
            return true;
        }
        
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.sendMessage(ChatColor.RED + "Healed!");
        return true;
    }
    
    private boolean handleInventory(Player player, String[] args, String rank) {
        if (!rank.equals("OWNER") && !rank.equals("ADMIN") && !player.isOp()) {
            player.sendMessage(ChatColor.RED + "No permission!");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /inventory <player>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }
        
        Inventory invView = Bukkit.createInventory(null, 54, ChatColor.GREEN + target.getName() + "'s Inventory");
        
        for (int i = 0; i < Math.min(36, target.getInventory().getSize()); i++) {
            ItemStack item = target.getInventory().getItem(i);
            if (item != null) {
                invView.setItem(i, item.clone());
            }
        }
        
        player.openInventory(invView);
        player.sendMessage(ChatColor.GREEN + "Viewing " + target.getName() + "'s inventory!");
        return true;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals(ChatColor.RED + "Admin Menu")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            String rank = playerRanks.getOrDefault(player.getUniqueId(), "MEMBER");
            
            if (!rank.equals("OWNER") && !rank.equals("ADMIN") && !player.isOp()) {
                player.closeInventory();
                return;
            }
            
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;
            
            String itemName = clicked.getItemMeta().getDisplayName();
            
            switch (itemName) {
                case "§aToggle Fly":
                    handleFly(player, rank);
                    break;
                case "§bToggle God Mode":
                    handleGod(player, rank);
                    break;
                case "§cHeal All Players":
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.setHealth(20.0);
                        p.setFoodLevel(20);
                        p.sendMessage(ChatColor.RED + "You have been healed!");
                    }
                    player.sendMessage(ChatColor.GREEN + "Healed all players!");
                    break;
            }
            
            player.closeInventory();
        }
    }
}
