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
import java.util.UUID;

import static org.Nopal.nopalShop.Data.PDC.*;

public class ShopGui implements Listener {

    public static Inventory maininv;
    public static Plugin plugin;

    public ShopGui(Plugin plugin) {
        ShopGui.plugin = plugin;
        ShopGui.maininv = createInventory(plugin);
    } // Initialize ShopGui inventory

    private Inventory createInventory(Plugin plugin) {
        FileConfiguration config = plugin.getConfig();
        int size = config.getInt("Mainshop.Size");
        String guiName = config.getString("Mainshop.GuiName");
        Inventory inventory = Bukkit.createInventory(null, size, Component.text(Objects.requireNonNull(guiName)));

        Set<String> categories = Objects.requireNonNull(config.getConfigurationSection("Mainshop.Category")).getKeys(false);
        for (String category : categories) {
            NavigationBar.bar(inventory.getSize(), inventory, 1, category, category);
            addItem(inventory, config, category);
        }
        return inventory;
    } // Create ShopGui / MainInv inventory

    private void addItem(Inventory inv, FileConfiguration config, String category) {
        String itemName = config.getString("Mainshop.Category." + category + ".Item");
        if (itemName != null) {
            Material material = Material.getMaterial(itemName.toUpperCase());
            if (material != null) {
                ItemStack itemStack = createItem(config, category, material);
                int itemSlot = config.getInt("Mainshop.Category." + category + ".Slot");
                inv.setItem(itemSlot, itemStack);
            }
        }
    } // Add Items to the inventory

    private ItemStack createItem(FileConfiguration config, String category, Material material) {

        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        String displayName = config.getString("Mainshop.Category." + category + ".displayName");
        itemMeta.displayName(Component.text(TranslateColor.text('&', Objects.requireNonNullElse(displayName, category))));

        itemStack.setItemMeta(itemMeta);
        PDC.setdata(itemStack, "category", category);

        return itemStack;
    } // Create the item

    public static void OpenInventory(Player p) {
        p.openInventory(maininv);

        ItemStack cart = ItemStack.of(Material.MINECART);

        CategoryGui.setdata(cart, "playerCart", p.getUniqueId()); //Set Data

        ItemMeta cartMeta = cart.getItemMeta();
        cartMeta.displayName(Component.text(TranslateColor.text('&', "&f" + p.getName() + "'s Cart")));
        cart.setItemMeta(cartMeta);

        maininv.setItem(maininv.getSize() - 5, cart);

    } // Opening the inventory

    @EventHandler(priority = EventPriority.LOW)
    private void OnPlayerClick(final InventoryClickEvent e) {

        Player p = (Player) e.getWhoClicked();
        ItemStack selectItem = e.getCurrentItem();
        UUID playerID = p.getUniqueId();

        if (selectItem == null) return;

        if (e.getInventory().equals(maininv)) {
            e.setCancelled(true);
            if (selectItem.getType().equals(Material.BARRIER)) {
                p.closeInventory();
            } else if (hasData(selectItem, "playerCart")) {

                if (PlayerCart.hasCart(playerID)) {

                    p.openInventory(PlayerCart.playerCart(playerID));

                } else {

                    p.sendMessage(Component.text(TranslateColor.text('&', "&cYou dont have a list of items yet!")));

                }
            }
        }

        if (!Objects.isNull(getdata(selectItem, "category", false))) {

            Object data = getdata(selectItem, "category", false);
            p.openInventory(CategoryGui.gui((String) data, p));

        } else if (!Objects.isNull(getdata(selectItem, "nextpage", false)) || !Objects.isNull(getdata(selectItem, "prevpage", false))) {

            Object data = getdata(selectItem, "nextpage", false) == null ? getdata(selectItem, "prevpage", false) : getdata(selectItem, "nextpage", false);
            p.openInventory(CategoryGui.gui((String) data, p));

        }

    } // Event detection in the ShopGui inventory

}
