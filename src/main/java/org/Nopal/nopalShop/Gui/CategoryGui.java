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
import java.util.concurrent.ConcurrentHashMap;

public class CategoryGui extends PDC implements Listener {

    private static final ConcurrentHashMap<String, Inventory> categoriesinv = new ConcurrentHashMap<>();
    private static final Set<Inventory> savedInv = new HashSet<>();

    public static void init() {

        FileConfiguration config = Configurations.get();
        Set<String> categories = config.getKeys(false);

        for (String category : categories)  {
            createInventory(config, category);
        }
    } // Initialize CategoryGui Inventory

    private static void createInventory(FileConfiguration config, String category) {
        int size = config.getInt(category + ".Size");
        int pages = config.getInt(category + ".Page");

        for (int i = 1; i <= pages; i++) {
            String name =  category + " Shop" + (pages > 1 ? " " + i : "");
            Inventory inventory = Bukkit.createInventory(null, size, Component.text(name));

            populateItem(config, category, inventory, size, i);
            NavigationBar.Nav(size, inventory, pages, category, i);

            categoriesinv.put(category + (i > 1 ? i : ""), inventory);
            savedInv.add(inventory);

        }

    } // Create the CategoryGui inventory

    private static void populateItem(FileConfiguration config, String category, Inventory inventory, int size, int page) {

        Set<String> ids = Objects.requireNonNull(config.getConfigurationSection(category)).getKeys(false);

        for (String id : ids) {

            if (!id.matches("\\d+")) continue;
            int itemId = Integer.parseInt(id);

            if (itemId >= (size * (page - 1)) && itemId < (size * page)) {
                addItem(config, category, inventory, id);
            }
        }
    } // Populate the categoryGui inventory with items

    private static void addItem(FileConfiguration config, String category, Inventory inventory, String id) {
        String itemName = config.getString(category + "." + id + ".Item");
        String itemDisplay = config.getString(category + "." + id + ".Displayname");
        double price = config.getDouble(category + "." + id + ".Price");
        int itemSlot = config.getInt(category + "." + id + ".Slot");

        if (itemName != null) {
            ItemStack newItem = createItem(Material.valueOf(itemName.toUpperCase()), price, itemDisplay);
            inventory.setItem(itemSlot, newItem);
        }
    } // Adding the item

    protected static ItemStack createItem(Material mat, Double price, String displayname){

        ItemStack item = new ItemStack(mat);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.displayName(Component.text(TranslateColor.text('&', displayname)));
        item.setItemMeta(itemMeta);

        setdata(item, "pricetag", price);

        return item;
    } // Create the item before adding the item

    public static Inventory gui(String name, Player p) {

        Inventory inventory = categoriesinv.get(name);

        ItemStack cart = ItemStack.of(Material.MINECART);

        setdata(cart, "playerCart", p.getUniqueId()); //Set Data

        ItemMeta cartMeta = cart.getItemMeta();
        cartMeta.displayName(Component.text(TranslateColor.text('&', "&f" + p.getName() + "'s Cart")));
        cart.setItemMeta(cartMeta);

        inventory.setItem(inventory.getSize() - 5, cart);

        return categoriesinv.get(name);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void OnInventoryClick(InventoryClickEvent e) {

        ItemStack item = e.getCurrentItem();
        Player p = (Player) e.getWhoClicked();
        UUID playerID = p.getUniqueId();

        if (item == null) return;

        if (savedInv.contains(e.getInventory())) {
            e.setCancelled(true);

            if (item.getType().equals(Material.BARRIER)) {
                p.openInventory(ShopGui.maininv);
            } else if (item.getType().equals(Material.MINECART)) {
                if (item.hasItemMeta()) {
                    if (hasData(item, "playerCart")) {
                        if (PlayerCart.hasCart(playerID)) {
                            p.openInventory(PlayerCart.playerCart(playerID));
                        } else {
                            p.sendMessage(Component.text(TranslateColor.text('&', "&cYou dont have a list of items yet!")));
                        }
                    }
                }
            } else {
                if (!hasData(item, "nav")) {
                    if (PlayerCart.hasCart(playerID)) {
                        if (!PlayerCart.isFull(PlayerCart.playerCart(p.getUniqueId()), item)) {
                            PlayerCart.addItem(item, p.getUniqueId());
                            CalcData(p, "totalPrice", (double) getdata(item, "pricetag", false), "+");
                        } else {
                            p.sendMessage(Component.text(TranslateColor.text('&', "&cYour cart is full!")));
                        }
                    } else {
                        PlayerCart.createInv(p.getUniqueId());
                        setdata(p, "totalPrice", getdata(item, "pricetag", false));
                        PlayerCart.addItem(item, p.getUniqueId());
                    }
                }
            }

        }

    }

}
