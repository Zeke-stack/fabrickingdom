package com.kingdom.commands;

import com.kingdom.commands.commands.*;
import com.kingdom.commands.listeners.PlayerJoinLeaveListener;
import com.kingdom.commands.listeners.CoinEconomyListener;
import com.kingdom.commands.listeners.TabListListener;
import com.kingdom.commands.utils.CoinManager;
import com.kingdom.commands.utils.MessageUtils;
import org.bukkit.plugin.java.JavaPlugin;

public class KingdomCommands extends JavaPlugin {
    
    private CoinManager coinManager;
    
    @Override
    public void onEnable() {
        getLogger().info("§6✦ Kingdom Commands has been enabled! ✦");
        
        // Initialize coin manager
        this.coinManager = new CoinManager(this);
        
        // Register commands
        getCommand("kban").setExecutor(new BanCommand(this));
        getCommand("kkick").setExecutor(new KickCommand(this));
        getCommand("kop").setExecutor(new OpCommand(this));
        getCommand("kdeop").setExecutor(new DeopCommand(this));
        getCommand("coins").setExecutor(new CoinsCommand(this));
        getCommand("balance").setExecutor(new BalanceCommand(this));
        
        // Register events
        getServer().getPluginManager().registerEvents(new PlayerJoinLeaveListener(this), this);
        getServer().getPluginManager().registerEvents(new CoinEconomyListener(this), this);
        getServer().getPluginManager().registerEvents(new TabListListener(this), this);
        
        // Save default config
        saveDefaultConfig();
        
        // Start tab list updater
        new TabListListener(this).startTabUpdater();
    }
    
    @Override
    public void onDisable() {
        getLogger().info("§6✦ Kingdom Commands has been disabled! ✦");
    }
    
    public CoinManager getCoinManager() {
        return coinManager;
    }
    
    public String getPrefix() {
        return getConfig().getString("prefix", "§6[§e✦§6Kingdom§e✦§6] §r");
    }
}
