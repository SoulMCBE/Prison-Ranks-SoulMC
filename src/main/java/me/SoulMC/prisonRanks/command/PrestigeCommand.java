package me.SoulMC.prisonRanks.command;

import me.SoulMC.prisonRanks.PrisonRanks;
import me.SoulMC.prisonRanks.data.PlayerData;
import me.SoulMC.prisonRanks.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrestigeCommand implements CommandExecutor {

    private final PrisonRanks plugin;

    public PrestigeCommand(PrisonRanks plugin) {
        this.plugin = plugin;
        MessageUtil.load(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtil.send(sender, "only-player");
            return true;
        }

        if (!player.hasPermission("prisonranks.prestige")) {
            MessageUtil.send(player, "no-permission");
            return true;
        }

        PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());

        if (!plugin.getRankManager().isMaxRank(data.getRank())) {
            MessageUtil.send(player, "prestige-not-max-rank");
            return true;
        }

        int maxPrestige = plugin.getConfig().getInt("settings.max-prestige", 10);
        if (data.getPrestige() >= maxPrestige) {
            MessageUtil.send(player, "prestige-maxed");
            return true;
        }

        data.setPrestige(data.getPrestige() + 1);
        data.setRank(plugin.getRankManager().getFirstRank());

        plugin.getDataManager().savePlayer(player.getUniqueId());
        plugin.getDataManager().save();

        player.sendMessage(MessageUtil.get("prestige-success")
                .replace("%prestige%", String.valueOf(data.getPrestige()))
                .replace("%rank%", data.getRank()));

        return true;
    }
}
