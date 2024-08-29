package org.Nopal.nopalShop.Gui;

import net.kyori.adventure.text.Component;
import org.Nopal.nopalShop.Configurations;
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

import java.util.*;

public class CategoryGui extends PDC implements Listener {

    private static final Map<String, Inventory> categoriesinv = new HashMap<>();
    private static final Set<Inventory> savedInv = new HashSet<>();

    public static void init() {

        FileConfiguration config = Configurations.get();

        Set<String> categories = config.getKeys(false);

        for (String category : categories)  {

            int size = config.getInt(category + ".Size");
            int page = config.getInt(category + ".Page");

            Inventory inventory;

            if (page < 2) {

                inventory = Bukkit.createInventory(null, size, Component.text(category + " Shop"));

                Set<String> ids = Objects.requireNonNull(config.getConfigurationSection(category)).getKeys(false);

                for (String id : ids) {

                    String itemName = config.getString(category + "." + id + ".Item");
                    String itemDisplay = config.getString(category + "." + id + ".Displayname");
                    double price = config.getDouble(category + "." + id + ".Price");
                    int itemSlot = config.getInt(category + "." + id + ".Slot");

                    if (!Objects.isNull(itemName)) {

                        ItemStack newItem = createItem(Material.valueOf(itemName.toUpperCase()),
                                price , itemDisplay);

                        inventory.setItem(itemSlot, newItem);

                    }

                    NavigationBar.bar(size, inventory, page, category, category);

                    categoriesinv.put(category, inventory);
                    savedInv.add(inventory);

                }

            } else {

                for (int i = 1; i <= page; i++) {

                    inventory = Bukkit.createInventory(null, size, Component.text(category + " Shop " + i));

                    Set<String> ids = Objects.requireNonNull(config.getConfigurationSection(category)).getKeys(false);

                    for (String id : ids) {

                        String itemName = config.getString(category + "." + id + ".Item");
                        String itemDisplay = config.getString(category + "." + id + ".Displayname");
                        double price = config.getDouble(category + "." + id + ".Price");
                        int itemSlot = config.getInt(category + "." + id + ".Slot");

                        if (!Objects.isNull(itemName)) {

                            ItemStack newItem = createItem(Material.valueOf(itemName.toUpperCase()),
                                    price , itemDisplay);


                            if (Integer.parseInt(id) > 53 * (i - 1) && Integer.parseInt(id) < 53 * i) {
                                inventory.setItem(itemSlot, newItem);
                            }

                        }

                        if (i <= 2) {
                            NavigationBar.bar(size, inventory, page, category + (i + 1), category);
                            if (i != 1) {
                                categoriesinv.put(category + i, inventory);
                            } else {
                                categoriesinv.put(category, inventory);
                            }
                        } else {
                            NavigationBar.bar(size, inventory, page, category + i, category + (i - 1));
                            categoriesinv.put(category + i, inventory);
                        }
                        savedInv.add(inventory);

                    }

                }

            }





        }

    }

    protected static ItemStack createItem(Material mat, Double price, String displayname){

        ItemStack item = new ItemStack(mat);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.displayName(Component.text(TranslateColor.text('&', displayname)));
        setdata(item, "pricetag", price);

        item.setItemMeta(itemMeta);

        return item;
    }

    public static Inventory gui(String name) {
        return categoriesinv.get(name);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    final public void OnInventoryClick(InventoryClickEvent e) {
        if (savedInv.contains(e.getInventory())) {
            e.setCancelled(true);
        }

        ItemStack item = e.getCurrentItem();
        Player p = (Player) e.getWhoClicked();

        assert item != null;
        if (item.getType().equals(Material.BARRIER)) {
            p.openInventory(ShopGui.maininv);
        }

    }

}
