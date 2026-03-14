package me.SoulMC.prisonRanks.data;

import me.SoulMC.prisonRanks.PrisonRanks;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataManager {

    private final PrisonRanks plugin;
    private final File file;
    private final YamlConfiguration config;
    private final Map<UUID, PlayerData> cache = new HashMap<>();

    public DataManager(PrisonRanks plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "data.yml");

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create data.yml");
                e.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);
        loadAll();
    }

    private void loadAll() {
        if (config.getConfigurationSection("players") == null) {
            return;
        }

        for (String uuidString : config.getConfigurationSection("players").getKeys(false)) {
            UUID uuid;
            try {
                uuid = UUID.fromString(uuidString);
            } catch (IllegalArgumentException ex) {
                continue;
            }

            String path = "players." + uuidString;
            String rank = config.getString(path + ".rank", plugin.getConfig().getString("settings.starting-rank", "A"));
            int prestige = config.getInt(path + ".prestige", 0);
            int rebirth = config.getInt(path + ".rebirth", 0);

            cache.put(uuid, new PlayerData(rank, prestige, rebirth));
        }
    }

    public PlayerData getPlayerData(UUID uuid) {
        return cache.computeIfAbsent(uuid, ignored -> new PlayerData(
                plugin.getConfig().getString("settings.starting-rank", "A"),
                0,
                0
        ));
    }

    public void savePlayer(UUID uuid) {
        PlayerData data = cache.get(uuid);
        if (data == null) {
            return;
        }

        String path = "players." + uuid;
        config.set(path + ".rank", data.getRank());
        config.set(path + ".prestige", data.getPrestige());
        config.set(path + ".rebirth", data.getRebirth());
    }

    public void save() {
        for (UUID uuid : cache.keySet()) {
            savePlayer(uuid);
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save data.yml");
            e.printStackTrace();
        }
    }
}
