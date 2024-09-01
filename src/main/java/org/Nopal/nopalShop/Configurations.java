package org.Nopal.nopalShop;

import org.Nopal.nopalShop.Gui.AddressGui;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Configurations {

    public static File file;
    public static FileConfiguration cconfig;
    public static File addressFolder;
    public static ConcurrentHashMap<OfflinePlayer, FileConfiguration> addresses = new ConcurrentHashMap();

    public Configurations(Plugin plugin , FileConfiguration config) {

        List<String> header = Arrays.asList("Nopal Default Configurations", "Learn how to edit .yml before changing all of this", "configuration's behavior\n",
                "[0] [1] [2] [3] [4] [5] [6] [7] [8]",
                "[9] [10] [11] [12] [13] [14] [15] [16] [17]",
                "[18] [19] [20] [21] [22] [23] [24] [25] [26]");

        config.options().setHeader(header);

        config.addDefault("Prefix", "§8[§a§l!§8] §a§lNShop §8» §r");
        config.addDefault("Mainshop.Size", 27);
        config.addDefault("Mainshop.GuiName", "Nopal Shop");
        config.addDefault("Mainshop.Category.Blocks.Item", "stone");
        config.addDefault("Mainshop.Category.Blocks.Slot", 10);
        config.addDefault("Mainshop.Category.Blocks.displayName", "&aBlocks");
        config.addDefault("Mainshop.Category.Blocks.Pages", 1);

        config.options().copyDefaults(true);
        plugin.saveConfig();
    }

    public static void CategoryConfig(FileConfiguration mainfile) {

        file = new File(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("NopalShop")).getDataFolder(), "category.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        cconfig = YamlConfiguration.loadConfiguration(file);

        Set<String> categories = Objects.requireNonNull(mainfile.getConfigurationSection("Mainshop.Category")).getKeys(false);

        for (String category : categories) {

            cconfig.addDefault(category + ".Size", 54);
            cconfig.addDefault(category + ".Page", 1);
            cconfig.addDefault(category + ".1.Item", "stone");
            cconfig.addDefault(category + ".1.Slot", 1);
            cconfig.addDefault(category + ".1.Displayname", "&aStone");
            cconfig.addDefault(category + ".1.Price", 10.0);

        }

    }

    public static void AddressFolder() {
        addressFolder = new File(NopalShop.plugin().getDataFolder(), "playerAddress");
        if (!addressFolder.exists()) {
            addressFolder.mkdirs();
        }

        File[] files = addressFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {

                FileConfiguration userfile = YamlConfiguration.loadConfiguration(file);
                String filename = file.getName().replace(".yml", "");
                addresses.put(Bukkit.getOfflinePlayer(UUID.fromString(filename)), userfile);
                Location loc = userfile.getLocation("address");
                AddressGui.createItemAddress(loc, Bukkit.getOfflinePlayer(UUID.fromString(filename)));

            }
        }

    }

    public static void createAddress(UUID playerID, Location loc) {
        File userAddress = new File(addressFolder, playerID + ".yml");
        if (!userAddress.exists()) {
            try {

                userAddress.createNewFile();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        FileConfiguration userConfig = YamlConfiguration.loadConfiguration(userAddress);
        userConfig.set("address", loc);
        try {

            userConfig.save(userAddress);
            addresses.put(Bukkit.getOfflinePlayer(playerID), userConfig);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        YamlConfiguration.loadConfiguration(userAddress);
    }

    public static void save() {
        try {
            cconfig.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static FileConfiguration get(){
        return cconfig;
    }

    public static void reload() {
        cconfig = YamlConfiguration.loadConfiguration(file);

    }

}
