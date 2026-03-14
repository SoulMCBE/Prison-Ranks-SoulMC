package me.SoulMC.prisonRanks.command;

import me.SoulMC.prisonRanks.PrisonRanks;
import me.SoulMC.prisonRanks.data.PlayerData;
import me.SoulMC.prisonRanks.util.MessageUtil;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

public class RankupCommand implements CommandExecutor, TabCompleter {

    private final PrisonRanks plugin;
    private final DecimalFormat format = new DecimalFormat("#,###.##");

    public RankupCommand(PrisonRanks plugin) {
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

        if (!player.hasPermission("prisonranks.rankup")) {
            MessageUtil.send(player, "no-permission");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("max")) {
            handleRankupMax(player);
            return true;
        }

        handleSingleRankup(player);
        return true;
    }

    private void handleSingleRankup(Player player) {
        PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        String nextRank = plugin.getRankManager().getNextRank(data.getRank());

        if (nextRank == null) {
            MessageUtil.send(player, "rankup-no-next");
            return;
        }

        double cost = plugin.getRankManager().getRankCost(nextRank);
        double balance = plugin.getEconomy().getBalance(player);

        if (balance < cost) {
            player.sendMessage(
                    MessageUtil.get("rankup-not-enough")
                            .replace("%cost%", format.format(cost))
                            .replace("%rank%", nextRank)
            );
            return;
        }

        EconomyResponse response = plugin.getEconomy().withdrawPlayer(player, cost);
        if (!response.transactionSuccess()) {
            MessageUtil.send(player, "economy-missing");
            return;
        }

        data.setRank(nextRank);
        plugin.getDataManager().savePlayer(player.getUniqueId());
        plugin.getDataManager().save();

        player.sendMessage(
                MessageUtil.get("rankup-success")
                        .replace("%rank%", nextRank)
        );
    }

    private void handleRankupMax(Player player) {
        PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        int amount = 0;

        while (true) {
            String nextRank = plugin.getRankManager().getNextRank(data.getRank());

            if (nextRank == null) {
                break;
            }

            double cost = plugin.getRankManager().getRankCost(nextRank);
            double balance = plugin.getEconomy().getBalance(player);

            if (balance < cost) {
                break;
            }

            EconomyResponse response = plugin.getEconomy().withdrawPlayer(player, cost);
            if (!response.transactionSuccess()) {
                break;
            }

            data.setRank(nextRank);
            amount++;
        }

        plugin.getDataManager().savePlayer(player.getUniqueId());
        plugin.getDataManager().save();

        if (amount == 0) {
            String nextRank = plugin.getRankManager().getNextRank(data.getRank());

            if (nextRank == null) {
                MessageUtil.send(player, "rankup-no-next");
            } else {
                double cost = plugin.getRankManager().getRankCost(nextRank);
                player.sendMessage(
                        MessageUtil.get("rankup-not-enough")
                                .replace("%cost%", format.format(cost))
                                .replace("%rank%", nextRank)
                );
            }
            return;
        }

        player.sendMessage(
                MessageUtil.get("rankup-max-success")
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%rank%", data.getRank())
        );
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Collections.singletonList("max");
        }
        return Collections.emptyList();
    }
}
