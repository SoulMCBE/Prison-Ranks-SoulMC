package me.SoulMC.prisonRanks.util;

import me.SoulMC.prisonRanks.PrisonRanks;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessageUtil {

    private static YamlConfiguration messagesConfig;

    public static void load(PrisonRanks plugin) {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        messagesConfig = YamlConfiguration.loadConfiguration(file);
    }

    public static String get(String path) {
        if (messagesConfig == null) {
            load(PrisonRanks.getInstance());
        }

        String prefix = color(messagesConfig.getString("prefix", ""));
        String message = color(messagesConfig.getString(path, path));
        return prefix + message;
    }

    public static String raw(String path) {
        if (messagesConfig == null) {
            load(PrisonRanks.getInstance());
        }

        return color(messagesConfig.getString(path, path));
    }

    public static void send(CommandSender sender, String path) {
        sender.sendMessage(get(path));
    }

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
