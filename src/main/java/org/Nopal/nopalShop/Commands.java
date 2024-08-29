package org.Nopal.nopalShop;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Commands implements CommandExecutor {

    public static Plugin plugin;

    public Commands(Plugin plugin) {
        Commands.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] strings) {
        if (!(sender instanceof Player)) return true;

        Player p = (Player) sender;
        OfflinePlayer op = Bukkit.getServer().getOfflinePlayer(p.getName());
        Economy econ = NopalShop.getEconomy();


        if (cmd.getName().equalsIgnoreCase("nsreload")) {
            plugin.reloadConfig();
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            plugin.getServer().getPluginManager().enablePlugin(plugin);
            return true;
        } else if (cmd.getName().equalsIgnoreCase("shop")) {
            ShopGui.OpenInventory(p);
            return true;
        }

        return false;
    }
}
