package org.Nopal.nopalShop;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.Set;

public class ShopGui implements Listener {

    private static Inventory inv;
    public static Plugin plugin;

    public ShopGui(Plugin plugin) {
        ShopGui.plugin = plugin;
        
        FileConfiguration config = plugin.getConfig();

        int size = config.getInt("Mainshop.Size");
        String Guiname = config.getString("Mainshop.GuiName");
        Set<String> categories = Objects.requireNonNull(config.getConfigurationSection("Mainshop.Category")).getKeys(false);

        assert Guiname != null;
        inv = Bukkit.createInventory(null, size, Component.text(Guiname));

        for (String category : categories) {
            String itemName = config.getString("Mainshop.Category." + category + ".Item");
            String displayName = config.getString("Mainshop.Category." + category + ".displayName");
            int itemslot = config.getInt("Mainshop.Category." + category + ".Slot");

            if (itemName != null) {

                Material material = Material.getMaterial(itemName.toUpperCase());

                if (material != null) {
                    ItemStack itemstack = new ItemStack(material);
                    ItemMeta itemmeta = itemstack.getItemMeta();

                    if (displayName != null) {
                        itemmeta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', displayName)));
                    } else {
                        itemmeta.displayName(Component.text(category));
                    }

                    itemstack.setItemMeta(itemmeta);

                    inv.setItem(itemslot, itemstack);
                }
            }

        }

    }

    public static void OpenInventory(Player p) {
        p.openInventory(inv);
    }

    public static void items(Inventory inv) {
        return;
    }

    protected void Items() {
        return;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void OnPlayerClick(final InventoryClickEvent e) {
        if (e.getInventory().equals(inv)) {
            e.setCancelled(true);
        }
    }

}
