package me.SoulMC.prisonRanks.manager;

import me.SoulMC.prisonRanks.PrisonRanks;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RankManager {

    private final PrisonRanks plugin;
    private final List<String> ranks = new ArrayList<>();

    public RankManager(PrisonRanks plugin) {
        this.plugin = plugin;
        loadRanks();
    }

    private void loadRanks() {
        ranks.clear();

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("ranks");
        if (section == null) {
            return;
        }

        ranks.addAll(section.getKeys(false));
        Collections.sort(ranks);
    }

    public List<String> getRanks() {
        return ranks;
    }

    public boolean isValidRank(String rank) {
        return ranks.contains(rank);
    }

    public String getFirstRank() {
        return plugin.getConfig().getString("settings.starting-rank", "A");
    }

    public String getLastRank() {
        if (ranks.isEmpty()) {
            return getFirstRank();
        }
        return ranks.get(ranks.size() - 1);
    }

    public String getNextRank(String currentRank) {
        int index = ranks.indexOf(currentRank);
        if (index == -1 || index + 1 >= ranks.size()) {
            return null;
        }
        return ranks.get(index + 1);
    }

    public double getRankCost(String rank) {
        return plugin.getConfig().getDouble("ranks." + rank + ".cost", 0.0);
    }

    public boolean isMaxRank(String rank) {
        return getLastRank().equalsIgnoreCase(rank);
    }
}
