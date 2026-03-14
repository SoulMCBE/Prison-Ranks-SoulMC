package me.SoulMC.prisonRanks.command;

import me.SoulMC.prisonRanks.PrisonRanks;
import me.SoulMC.prisonRanks.data.PlayerData;
import me.SoulMC.prisonRanks.util.MessageUtil;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class RebirthCommand implements CommandExecutor {

    private final PrisonRanks plugin;
    private final DecimalFormat format = new DecimalFormat("#,###.##");

    public RebirthCommand(PrisonRanks plugin) {
        this.plugin = plugin;
        MessageUtil.load(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil.send(sender, "only-player");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("prisonranks.rebirth")) {
            MessageUtil.send(player, "no-permission");
            return true;
        }

        PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());

        if (!plugin.getRankManager().isMaxRank(data.getRank())) {
            MessageUtil.send(player, "rebirth-not-max-rank");
            return true;
        }

        int requiredPrestige = plugin.getConfig().getInt("rebirth.required-prestige", 10);
        if (data.getPrestige() < requiredPrestige) {
            player.sendMessage(
                    MessageUtil.get("rebirth-not-enough-prestige")
                            .replace("%required%", String.valueOf(requiredPrestige))
            );
            return true;
        }

        double cost = plugin.getConfig().getDouble("rebirth.cost", 100000000.0);
        double balance = plugin.getEconomy().getBalance(player);

        if (balance < cost) {
            player.sendMessage(
                    MessageUtil.get("rebirth-not-enough-money")
                            .replace("%cost%", format.format(cost))
            );
            return true;
        }

        EconomyResponse response = plugin.getEconomy().withdrawPlayer(player, cost);
        if (!response.transactionSuccess()) {
            MessageUtil.send(player, "economy-missing");
            return true;
        }

        data.setRebirth(data.getRebirth() + 1);
        data.setPrestige(0);
        data.setRank(plugin.getRankManager().getFirstRank());

        plugin.getDataManager().savePlayer(player.getUniqueId());
        plugin.getDataManager().save();

        player.sendMessage(
                MessageUtil.get("rebirth-success")
                        .replace("%rebirth%", String.valueOf(data.getRebirth()))
        );

        return true;
    }
}
