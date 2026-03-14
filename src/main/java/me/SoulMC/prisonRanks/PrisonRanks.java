package me.SoulMC.prisonRanks;

import me.SoulMC.prisonRanks.command.PrestigeCommand;
import me.SoulMC.prisonRanks.command.RankupCommand;
import me.SoulMC.prisonRanks.command.RebirthCommand;
import me.SoulMC.prisonRanks.data.DataManager;
import me.SoulMC.prisonRanks.listener.PlayerJoinListener;
import me.SoulMC.prisonRanks.manager.RankManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class PrisonRanks extends JavaPlugin {

    private static PrisonRanks instance;

    private Economy economy;
    private DataManager dataManager;
    private RankManager rankManager;

    public static PrisonRanks getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        saveResourceIfNotExists("messages.yml");

        if (!setupEconomy()) {
            getLogger().severe("Vault economy provider not found. Disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.dataManager = new DataManager(this);
        this.rankManager = new RankManager(this);

        registerCommands();
        registerListeners();

        getLogger().info("PrisonRanks enabled.");
    }

    @Override
    public void onDisable() {
        if (dataManager != null) {
            dataManager.save();
        }
        getLogger().info("PrisonRanks disabled.");
    }

    private void registerCommands() {
        PluginCommand rankup = getCommand("rankup");
        if (rankup != null) {
            RankupCommand command = new RankupCommand(this);
            rankup.setExecutor(command);
            rankup.setTabCompleter(command);
        }

        PluginCommand prestige = getCommand("prestige");
        if (prestige != null) {
            prestige.setExecutor(new PrestigeCommand(this));
        }

        PluginCommand rebirth = getCommand("rebirth");
        if (rebirth != null) {
            rebirth.setExecutor(new RebirthCommand(this));
        }
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }

    private void saveResourceIfNotExists(String fileName) {
        if (!new java.io.File(getDataFolder(), fileName).exists()) {
            saveResource(fileName, false);
        }
    }

    public Economy getEconomy() {
        return economy;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public RankManager getRankManager() {
        return rankManager;
    }
}
