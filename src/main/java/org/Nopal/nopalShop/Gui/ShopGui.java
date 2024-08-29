package org.Nopal.nopalShop.Gui;

import net.kyori.adventure.text.Component;
import org.Nopal.nopalShop.Data.PDC;
import org.Nopal.nopalShop.Data.TranslateColor;
import org.bukkit.Bukkit;
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

public class ShopGui extends CategoryGui implements Listener {

    public static Inventory maininv;
    public static Plugin plugin;

    public ShopGui(Plugin plugin) {
        ShopGui.plugin = plugin;
        
        FileConfiguration config = plugin.getConfig();

        int size = config.getInt("Mainshop.Size");
        String Guiname = config.getString("Mainshop.GuiName");
        Set<String> categories = Objects.requireNonNull(config.getConfigurationSection("Mainshop.Category")).getKeys(false);

        assert Guiname != null;
        maininv = Bukkit.createInventory(null, size, Component.text(Guiname));

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
                        itemmeta.displayName(Component.text(TranslateColor.text('&', displayName)));
                    } else {
                        itemmeta.displayName(Component.text(category));
                    }

                    itemstack.setItemMeta(itemmeta);

                    PDC.setdata(itemstack, "category", category);
                    NavigationBar.bar(maininv.getSize(), maininv, 1, category, category);
                    maininv.setItem(itemslot, itemstack);

                }
            }

        }

    }

    public static void OpenInventory(Player p) {
        p.openInventory(maininv);
    }

    public static void items(Inventory inv) {
        return;
    }

    protected void Items() {
        return;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void OnPlayerClick(final InventoryClickEvent e) {
        if (e.getInventory().equals(maininv)) {
            e.setCancelled(true);
        }

        Player p = (Player) e.getWhoClicked();
        ItemStack selectItem = e.getCurrentItem();

        if (!Objects.isNull(PDC.getdata(selectItem, "category", false))) {

            Object data = PDC.getdata(selectItem, "category", false);
            p.openInventory(gui((String) data));

        } else if (!Objects.isNull(PDC.getdata(selectItem, "nextpage", false)) || !Objects.isNull(PDC.getdata(selectItem, "prevpage", false))) {
            Object data = PDC.getdata(selectItem, "nextpage", false) == null ? PDC.getdata(selectItem, "prevpage", false) : PDC.getdata(selectItem, "nextpage", false);
            p.openInventory(gui((String) data));

        }

        assert selectItem != null;
        if (selectItem.getType().equals(Material.BARRIER)) {
            p.closeInventory();
        }

    }

}
