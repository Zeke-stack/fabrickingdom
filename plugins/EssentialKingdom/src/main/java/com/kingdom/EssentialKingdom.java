package com.kingdom;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.stream.Collectors;

public class EssentialKingdom extends JavaPlugin implements Listener {
    
    private final HashMap<UUID, Double> playerCoins = new HashMap<>();
    private final HashMap<UUID, String> playerRanks = new HashMap<>();
    private final List<String> joinMessages = Arrays.asList(
        "💎 %player% %rank% has graced us with their presence!",
        "🏰 %player% %rank% enters the Kingdom!",
        "👑 Welcome back %player% %rank%!",
        "✨ The Kingdom grows stronger with %player% %rank%!",
        "🎖️ %player% %rank% joins the battle!",
        "⚔️ A new warrior %player% %rank% has arrived!",
        "🌟 %player% %rank% returns to claim their glory!"
    );
    private Random random = new Random();
    
    @Override
    public void onEnable() {
        getLogger().info("Essential Kingdom Plugin Enabled!");
        getServer().getPluginManager().registerEvents(this, this);
        
        playerRanks.put(UUID.fromString("5bbf23d0-e968-4e47-854a-02090deeba3a"), "OWNER");
        playerCoins.put(UUID.fromString("5bbf23d0-e968-4e47-854a-02090deeba3a"), 10000.0);
        
        setupNametagTeams();
    }
    
    private void setupNametagTeams() {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        
        String[] ranks = {"OWNER", "ADMIN", "MODERATOR", "VIP", "MEMBER"};
        String[] colors = {ChatColor.GOLD.toString(), ChatColor.RED.toString(), 
                          ChatColor.BLUE.toString(), ChatColor.AQUA.toString(), ChatColor.GREEN.toString()};
        
        for (int i = 0; i < ranks.length; i++) {
            Team team = board.getTeam(ranks[i]);
            if (team == null) {
                team = board.registerNewTeam(ranks[i]);
            }
            team.setPrefix(colors[i] + "[" + ranks[i] + "] " + ChatColor.RESET);
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        if (!playerCoins.containsKey(uuid)) playerCoins.put(uuid, 0.0);
        if (!playerRanks.containsKey(uuid)) playerRanks.put(uuid, "MEMBER");
        
        String rank = playerRanks.get(uuid);
        double coins = playerCoins.get(uuid);
        
        String joinMsg = joinMessages.get(random.nextInt(joinMessages.size()))
            .replace("%player%", ChatColor.GREEN + player.getName() + ChatColor.GOLD)
            .replace("%rank%", ChatColor.YELLOW + "[" + rank + "]" + ChatColor.GOLD);
        event.setJoinMessage(ChatColor.GOLD + "✨ " + joinMsg);
        
        updatePlayerNametag(player, rank);
        
        player.sendMessage(ChatColor.GOLD + "===============================");
        player.sendMessage(ChatColor.GOLD + "✨ Welcome to the Kingdom! ✨");
        player.sendMessage(ChatColor.WHITE + "Rank: " + ChatColor.YELLOW + rank);
        player.sendMessage(ChatColor.WHITE + "Coins: " + ChatColor.GREEN + coins);
        player.sendMessage(ChatColor.GRAY + "Type /kingdom for info");
        player.sendMessage(ChatColor.GOLD + "===============================");
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String rank = playerRanks.getOrDefault(player.getUniqueId(), "MEMBER");
        
        event.setQuitMessage(ChatColor.GRAY + "👋 " + player.getName() + 
                           ChatColor.GRAY + " [" + rank + "] left the Kingdom");
    }
    
    private void updatePlayerNametag(Player player, String rank) {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = board.getTeam(rank);
        if (team != null) {
            team.addPlayer(player);
        }
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
            case "leaderboard": return handleLeaderboard(player);
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
    
    private boolean handleLeaderboard(Player player) {
        player.sendMessage(ChatColor.GOLD + "+================================+");
        player.sendMessage(ChatColor.GOLD + "|   " + ChatColor.YELLOW + "WEALTH LEADERBOARD" + ChatColor.GOLD + "   |");
        player.sendMessage(ChatColor.GOLD + "+================================+");
        
        List<Map.Entry<UUID, Double>> sorted = playerCoins.entrySet().stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .limit(10)
            .collect(Collectors.toList());
        
        int rankNum = 1;
        for (Map.Entry<UUID, Double> entry : sorted) {
            Player p = Bukkit.getPlayer(entry.getKey());
            if (p != null) {
                String rankColor = getRankColor(playerRanks.get(entry.getKey()));
                String medal = rankNum == 1 ? "🥇" : rankNum == 2 ? "🥈" : rankNum == 3 ? "🥉" : "  ";
                player.sendMessage(ChatColor.GOLD + "| " + medal + " #" + rankNum + " " + 
                                 rankColor + p.getName() + ChatColor.GRAY + " - " + 
                                 ChatColor.GREEN + String.format("%.0f", entry.getValue()) + " coins" + ChatColor.GOLD + " |");
                rankNum++;
            }
        }
        player.sendMessage(ChatColor.GOLD + "+================================+");
        return true;
    }
    
    private String getRankColor(String rank) {
        switch (rank) {
            case "OWNER": return ChatColor.GOLD.toString();
            case "ADMIN": return ChatColor.RED.toString();
            case "MODERATOR": return ChatColor.BLUE.toString();
            case "VIP": return ChatColor.AQUA.toString();
            default: return ChatColor.GREEN.toString();
        }
    }
    
    private boolean handleAdmin(Player player, String rank) {
        if (!rank.equals("OWNER") && !rank.equals("ADMIN") && !player.isOp()) {
            player.sendMessage(ChatColor.RED + "No permission!");
            return true;
        }
        
        player.sendMessage(ChatColor.RED + "=========== ADMIN COMMANDS ===========");
        player.sendMessage(ChatColor.YELLOW + "/kingdom" + ChatColor.GRAY + " - Kingdom info");
        player.sendMessage(ChatColor.YELLOW + "/coins" + ChatColor.GRAY + " - Check coins");
        player.sendMessage(ChatColor.YELLOW + "/rank" + ChatColor.GRAY + " - Check rank");
        player.sendMessage(ChatColor.YELLOW + "/online" + ChatColor.GRAY + " - Online players");
        player.sendMessage(ChatColor.YELLOW + "/leaderboard" + ChatColor.GRAY + " - Wealth leaderboard");
        player.sendMessage(ChatColor.RED + "======================================");
        return true;
    }
}
