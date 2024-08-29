package org.Nopal.nopalShop;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

public class Configurations {

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

        config.options().copyDefaults(true);

        plugin.saveConfig();
    }


}
