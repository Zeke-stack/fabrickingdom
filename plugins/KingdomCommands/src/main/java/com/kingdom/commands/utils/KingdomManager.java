package com.kingdom.commands.utils;

import com.kingdom.commands.KingdomCommands;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class KingdomManager {
    
    private final KingdomCommands plugin;
    private FileConfiguration kingdomConfig;
    private final Map<UUID, String> playerRanks = new ConcurrentHashMap<>();
    private final Map<String, Location> kingdomLocations = new HashMap<>();
    private final List<String> kingdomLaws = new ArrayList<>();
    private long lastTaxCollection = 0;
    
    public KingdomManager(KingdomCommands plugin) {
        this.plugin = plugin;
        loadKingdomConfig();
        startTaxCollectionTask();
        startEventScheduler();
    }
    
    private void loadKingdomConfig() {
        File configFile = new File(plugin.getDataFolder(), "kingdom.yml");
        if (!configFile.exists()) {
            plugin.saveResource("kingdom.yml", false);
        }
        
        kingdomConfig = YamlConfiguration.loadConfiguration(configFile);
        loadKingdomLaws();
        loadKingdomLocations();
    }
    
    private void loadKingdomLaws() {
        kingdomLaws.clear();
        kingdomLaws.addAll(kingdomConfig.getStringList("kingdom.laws"));
    }
    
    private void loadKingdomLocations() {
        kingdomLocations.clear();
        if (kingdomConfig.contains("kingdom.locations")) {
            for (String locName : kingdomConfig.getConfigurationSection("kingdom.locations").getKeys(false)) {
                String path = "kingdom.locations." + locName;
                World world = Bukkit.getWorld(kingdomConfig.getString(path + ".world"));
                if (world != null) {
                    double x = kingdomConfig.getDouble(path + ".x");
                    double y = kingdomConfig.getDouble(path + ".y");
                    double z = kingdomConfig.getDouble(path + ".z");
                    kingdomLocations.put(locName, new Location(world, x, y, z));
                }
            }
        }
    }
    
    public String getKingdomName() {
        return kingdomConfig.getString("kingdom.name", "Kingdom of Minecraftia");
    }
    
    public String getRuler() {
        return kingdomConfig.getString("kingdom.ruler", "His Majesty, the King");
    }
    
    public String getMotto() {
        return kingdomConfig.getString("kingdom.motto", "Honor, Loyalty, Valor");
    }
    
    public boolean setPlayerRank(UUID playerId, String rank) {
        if (!isValidRank(rank)) {
            return false;
        }
        
        playerRanks.put(playerId, rank);
        Player player = Bukkit.getPlayer(playerId);
        if (player != null) {
            updatePlayerDisplayName(player, rank);
            player.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "You are now " + getRankPrefix(rank) + " " + player.getName());
        }
        return true;
    }
    
    public String getPlayerRank(UUID playerId) {
        return playerRanks.getOrDefault(playerId, "peasant");
    }
    
    public boolean isValidRank(String rank) {
        return kingdomConfig.contains("kingdom.ranks." + rank);
    }
    
    public String getRankPrefix(String rank) {
        return kingdomConfig.getString("kingdom.ranks." + rank + ".prefix", "🌾");
    }
    
    public ChatColor getRankColor(String rank) {
        String colorName = kingdomConfig.getString("kingdom.ranks." + rank + ".color", "GRAY");
        try {
            return ChatColor.valueOf(colorName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ChatColor.GRAY;
        }
    }
    
    public String getRankTitle(String rank) {
        return kingdomConfig.getString("kingdom.ranks." + rank + ".title", "Citizen");
    }
    
    public List<String> getKingdomLaws() {
        return new ArrayList<>(kingdomLaws);
    }
    
    public Location getKingdomLocation(String locationName) {
        return kingdomLocations.get(locationName);
    }
    
    public void updatePlayerDisplayName(Player player, String rank) {
        String prefix = getRankPrefix(rank);
        ChatColor color = getRankColor(rank);
        String displayName = prefix + " " + color + player.getName();
        
        player.setDisplayName(displayName);
        player.setPlayerListName(displayName);
    }
    
    public void sendRoyalAnnouncement(String title, String message, ChatColor color) {
        String soundName = kingdomConfig.getString("kingdom.sounds.royal_announce", "ENTITY_WITHER_SPAWN");
        Sound sound;
        try {
            sound = Sound.valueOf(soundName);
        } catch (IllegalArgumentException e) {
            sound = Sound.ENTITY_WITHER_SPAWN;
        }
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Send title
            player.sendTitle(color + "" + ChatColor.BOLD + title, ChatColor.WHITE + message, 10, 100, 20);
            
            // Play sound
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
            
            // Send chat message
            player.sendMessage(color + "" + ChatColor.BOLD + "✦ " + title + " ✦");
            player.sendMessage(ChatColor.WHITE + message);
        }
    }
    
    public void knightPlayer(Player knight, Player target) {
        setPlayerRank(target.getUniqueId(), "knight");
        
        String soundName = kingdomConfig.getString("kingdom.sounds.knight_ceremony", "ENTITY_PLAYER_LEVELUP");
        Sound sound;
        try {
            sound = Sound.valueOf(soundName);
        } catch (IllegalArgumentException e) {
            sound = Sound.ENTITY_PLAYER_LEVELUP;
        }
        
        // Announce to kingdom
        sendRoyalAnnouncement("Knighting Ceremony", 
            target.getName() + " has been knighted by " + knight.getName() + "!", 
            ChatColor.GOLD);
        
        // Play special sound effects
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }
        
        // Give knight a special item
        if (target.getInventory().firstEmpty() != -1) {
            // Could give a special knight sword or armor here
            target.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "You've been granted a knight's sword!");
        }
    }
    
    public void grantNobility(Player granter, Player target) {
        setPlayerRank(target.getUniqueId(), "count");
        
        // Announce to kingdom
        sendRoyalAnnouncement("Nobility Granted", 
            target.getName() + " has been granted nobility by the crown!", 
            ChatColor.DARK_PURPLE);
        
        // Special effects
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.0f);
        }
    }
    
    public void collectTaxes() {
        if (!kingdomConfig.getBoolean("kingdom.settings.enable_tax_system", true)) {
            return;
        }
        
        int taxRate = kingdomConfig.getInt("kingdom.economy.tax_rate", 10);
        int totalCollected = 0;
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            String rank = getPlayerRank(player.getUniqueId());
            if (rank.equals("peasant") || rank.equals("merchant")) {
                // Calculate tax based on player's coins
                int playerCoins = plugin.getCoinManager().countAllGold(player);
                int tax = (playerCoins * taxRate) / 100;
                
                if (tax > 0) {
                    // Remove tax from player (simplified - would need actual coin removal)
                    totalCollected += tax;
                    player.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "Tax collected: " + tax + " gold coins");
                }
            }
        }
        
        if (totalCollected > 0) {
            sendRoyalAnnouncement("Tax Collection", 
                "Kingdom treasury collected " + totalCollected + " gold coins in taxes!", 
                ChatColor.GOLD);
        }
        
        lastTaxCollection = System.currentTimeMillis();
    }
    
    private void startTaxCollectionTask() {
        long interval = kingdomConfig.getLong("kingdom.settings.auto_save_interval", 300) * 20L; // Convert to ticks
        
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            // Check if it's tax day (simplified - would check actual day)
            collectTaxes();
        }, interval, interval);
    }
    
    private void startEventScheduler() {
        if (!kingdomConfig.getBoolean("kingdom.settings.enable_events", true)) {
            return;
        }
        
        // Schedule events based on configuration
        long tournamentInterval = kingdomConfig.getLong("kingdom.events.tournament.interval_days", 14) * 24 * 60 * 60 * 20L;
        long ceremonyInterval = kingdomConfig.getLong("kingdom.events.royal_ceremony.interval_days", 30) * 24 * 60 * 60 * 20L;
        long festivalInterval = kingdomConfig.getLong("kingdom.events.festival.interval_days", 7) * 24 * 60 * 60 * 20L;
        
        // Tournament scheduler
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            startTournament();
        }, tournamentInterval, tournamentInterval);
        
        // Royal ceremony scheduler
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            startRoyalCeremony();
        }, ceremonyInterval, ceremonyInterval);
        
        // Festival scheduler
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            startFestival();
        }, festivalInterval, festivalInterval);
    }
    
    private void startTournament() {
        sendRoyalAnnouncement("Royal Tournament", 
            "The royal tournament is starting at the castle! All knights are invited to compete!", 
            ChatColor.YELLOW);
        
        // Could teleport players to tournament area, give special items, etc.
    }
    
    private void startRoyalCeremony() {
        sendRoyalAnnouncement("Royal Ceremony", 
            "A royal ceremony is being held at the throne room! All nobility are requested to attend!", 
            ChatColor.GOLD);
        
        // Could teleport nobles to throne room, start special effects, etc.
    }
    
    private void startFestival() {
        sendRoyalAnnouncement("Kingdom Festival", 
            "The kingdom festival has begun in the village! All citizens are invited to celebrate!", 
            ChatColor.GREEN);
        
        // Could spawn fireworks, give festival items, etc.
    }
    
    public void showKingdomInfo(Player player) {
        player.sendMessage(ChatColor.GOLD + "✦✦✦ " + ChatColor.WHITE + getKingdomName() + " ✦✦✦");
        player.sendMessage(ChatColor.YELLOW + "Ruler: " + ChatColor.WHITE + getRuler());
        player.sendMessage(ChatColor.YELLOW + "Motto: " + ChatColor.WHITE + getMotto());
        player.sendMessage(ChatColor.YELLOW + "Population: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size() + " citizens");
        player.sendMessage(ChatColor.YELLOW + "Laws: " + ChatColor.WHITE + kingdomLaws.size() + " royal decrees");
        player.sendMessage(ChatColor.GRAY + "Use /kingdom laws to view all laws");
        player.sendMessage(ChatColor.GRAY + "Use /kingdom ranks to view all ranks");
        player.sendMessage(ChatColor.GRAY + "Long live the King! 🏰");
    }
    
    public void showKingdomLaws(Player player) {
        player.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "Kingdom Laws " + ChatColor.GOLD + "✦");
        for (int i = 0; i < kingdomLaws.size(); i++) {
            player.sendMessage(ChatColor.RED + (i + 1) + ". " + ChatColor.WHITE + kingdomLaws.get(i));
        }
        player.sendMessage(ChatColor.GRAY + "Punishment: Exile or imprisonment");
    }
    
    public void showKingdomRanks(Player player) {
        player.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "Kingdom Ranks " + ChatColor.GOLD + "✦");
        
        if (kingdomConfig.contains("kingdom.ranks")) {
            for (String rank : kingdomConfig.getConfigurationSection("kingdom.ranks").getKeys(false)) {
                String prefix = getRankPrefix(rank);
                ChatColor color = getRankColor(rank);
                String title = getRankTitle(rank);
                player.sendMessage(color + prefix + " " + ChatColor.WHITE + title);
            }
        }
    }
    
    public void showPeasantLife(Player player) {
        player.sendMessage(ChatColor.GREEN + "🌾 " + ChatColor.WHITE + "Life as a Peasant");
        player.sendMessage(ChatColor.YELLOW + "Work hard, pay taxes, and serve the kingdom!");
        player.sendMessage(ChatColor.YELLOW + "You may one day become a knight or noble!");
        player.sendMessage(ChatColor.GRAY + "Current status: Loyal citizen");
        player.sendMessage(ChatColor.GRAY + "Daily tasks: Farm, mine, build, trade");
        player.sendMessage(ChatColor.GRAY + "Tax rate: " + kingdomConfig.getInt("kingdom.economy.tax_rate", 10) + "%");
    }
    
    public Map<UUID, String> getAllPlayerRanks() {
        return new HashMap<>(playerRanks);
    }
    
    public void saveKingdomData() {
        // Save player ranks to config
        File configFile = new File(plugin.getDataFolder(), "kingdom.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        
        // Clear existing player ranks
        if (config.contains("player_ranks")) {
            config.set("player_ranks", null);
        }
        
        // Save current player ranks
        for (Map.Entry<UUID, String> entry : playerRanks.entrySet()) {
            config.set("player_ranks." + entry.getKey().toString(), entry.getValue());
        }
        
        try {
            config.save(configFile);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save kingdom data: " + e.getMessage());
        }
    }
    
    public void loadKingdomData() {
        File configFile = new File(plugin.getDataFolder(), "kingdom.yml");
        if (configFile.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            
            // Load player ranks
            if (config.contains("player_ranks")) {
                for (String uuidStr : config.getConfigurationSection("player_ranks").getKeys(false)) {
                    try {
                        UUID uuid = UUID.fromString(uuidStr);
                        String rank = config.getString("player_ranks." + uuidStr);
                        if (isValidRank(rank)) {
                            playerRanks.put(uuid, rank);
                        }
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid UUID in kingdom data: " + uuidStr);
                    }
                }
            }
        }
    }
}
