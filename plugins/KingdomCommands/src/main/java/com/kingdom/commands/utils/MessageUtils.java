package com.kingdom.commands.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class MessageUtils {
    
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public static String getMedievalBorder() {
        return colorize("&6╔════════════════════════════════════════════════════════════╗");
    }
    
    public static String getMedievalBorderEnd() {
        return colorize("&6╚════════════════════════════════════════════════════════════╝");
    }
    
    public static String getMedievalSeparator() {
        return colorize("&6╠════════════════════════════════════════════════════════════╣");
    }
    
    public static String centerText(String text, int maxWidth) {
        int textLength = ChatColor.stripColor(colorize(text)).length();
        int padding = (maxWidth - textLength) / 2;
        if (padding < 0) padding = 0;
        
        StringBuilder sb = new StringBuilder("&6║");
        for (int i = 0; i < padding - 1; i++) {
            sb.append(" ");
        }
        sb.append(text);
        for (int i = 0; i < padding - 1; i++) {
            sb.append(" ");
        }
        sb.append("&6║");
        return colorize(sb.toString());
    }
    
    public static void sendMedievalBox(CommandSender sender, String title, List<String> lines) {
        sender.sendMessage(getMedievalBorder());
        sender.sendMessage(centerText("&e" + title, 58));
        sender.sendMessage(getMedievalSeparator());
        
        for (String line : lines) {
            sender.sendMessage(centerText("&f" + line, 58));
        }
        
        sender.sendMessage(getMedievalBorderEnd());
    }
    
    public static void sendJoinMessage(CommandSender sender, String playerName) {
        List<String> lines = new ArrayList<>();
        lines.add("A noble soul has entered the realm!");
        lines.add("Welcome, &e" + playerName + "&f, to our kingdom!");
        lines.add("May your adventures be legendary!");
        
        sendMedievalBox(sender, "✦ Royal Welcome ✦", lines);
    }
    
    public static void sendLeaveMessage(CommandSender sender, String playerName) {
        List<String> lines = new ArrayList<>();
        lines.add("A brave adventurer departs our lands");
        lines.add("Farewell, &e" + playerName + "&f!");
        lines.add("Until we meet again in the kingdom!");
        
        sendMedievalBox(sender, "✦ Royal Farewell ✦", lines);
    }
    
    public static String getBanMessage(String reason) {
        List<String> lines = new ArrayList<>();
        lines.add("You have been exiled from the kingdom!");
        lines.add("Reason: &c" + (reason.isEmpty() ? "No reason provided" : reason));
        lines.add("May you reflect upon your actions!");
        
        StringBuilder message = new StringBuilder();
        message.append(getMedievalBorder()).append("\n");
        message.append(centerText("&c✦ Royal Exile ✦", 58)).append("\n");
        message.append(getMedievalSeparator()).append("\n");
        
        for (String line : lines) {
            message.append(centerText(line, 58)).append("\n");
        }
        
        message.append(getMedievalBorderEnd());
        return colorize(message.toString());
    }
    
    public static String getKickMessage(String reason) {
        List<String> lines = new ArrayList<>();
        lines.add("You have been temporarily removed from the kingdom!");
        lines.add("Reason: &e" + (reason.isEmpty() ? "No reason provided" : reason));
        lines.add("Please reconsider your actions!");
        
        StringBuilder message = new StringBuilder();
        message.append(getMedievalBorder()).append("\n");
        message.append(centerText("&e✦ Royal Removal ✦", 58)).append("\n");
        message.append(getMedievalSeparator()).append("\n");
        
        for (String line : lines) {
            message.append(centerText(line, 58)).append("\n");
        }
        
        message.append(getMedievalBorderEnd());
        return colorize(message.toString());
    }
}
