package me.SoulMC.prisonRanks.listener;

import me.SoulMC.prisonRanks.PrisonRanks;
import me.SoulMC.prisonRanks.data.PlayerData;
import me.SoulMC.prisonRanks.util.MessageUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final PrisonRanks plugin;

    public PlayerJoinListener(PrisonRanks plugin) {
        this.plugin = plugin;
        MessageUtil.load(plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PlayerData data = plugin.getDataManager().getPlayerData(event.getPlayer().getUniqueId());

        String message = MessageUtil.get("player-stats")
                .replace("%rank%", data.getRank())
                .replace("%prestige%", String.valueOf(data.getPrestige()))
                .replace("%rebirth%", String.valueOf(data.getRebirth()));

        event.getPlayer().sendMessage(message);
    }

}
