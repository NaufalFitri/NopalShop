package org.Nopal.nopalShop.Gui;

import net.kyori.adventure.text.Component;
import org.Nopal.nopalShop.Data.PDC;
import org.Nopal.nopalShop.Data.TranslateColor;
import org.Nopal.nopalShop.NopalShop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.Nopal.nopalShop.Data.PDC.*;

public class PlayerCart implements Listener {

    public static ConcurrentHashMap<UUID, Inventory> cartInventories = new ConcurrentHashMap<>();

    public static void createInv(UUID playerID) {

        Inventory inventory = Bukkit.createInventory(null, 36, Component.text(Objects.requireNonNull(Bukkit.getOfflinePlayer(playerID).getName()) + "'s Cart"));

        NavigationBar.bar(36, inventory, 1, "checkout", "checkout");

        cartInventories.put(playerID, inventory);

    } // Create player cart inventory

    public static void addItem(ItemStack item, UUID playerID) {

        Inventory inventory = cartInventories.get(playerID);
        inventory.addItem(item);

    } // Add item to the inventory

    public static Inventory playerCart(UUID playerID) {

        Inventory inventory = cartInventories.get(playerID);

        inventory.setItem(inventory.getSize() - 5, checkOutItem((Player) Bukkit.getOfflinePlayer(playerID)));
        inventory.setItem(inventory.getSize() - 6, AddressGui.AddressNav());

        return inventory;

    } // Return and adjust item navigation bar of inventory

    public static boolean hasCart(UUID playerID) {

        return cartInventories.containsKey(playerID);

    } // Detect if player has a cart

    private static void checkOut(UUID playerID) {

        OfflinePlayer player = Bukkit.getOfflinePlayer(playerID);
        double totalprice = (double) getdata(player.getPlayer(), "totalprice", false);

        if (NopalShop.econ.getBalance(player) < totalprice) {

            Objects.requireNonNull(player.getPlayer()).sendMessage
                    (TranslateColor.text('&',"&cYou dont have enough money"));

        } else {
            NopalShop.econ.withdrawPlayer(player, totalprice);
            Objects.requireNonNull(player.getPlayer()).sendMessage
                    (TranslateColor.text('&',"&aYour items are on the delivery!"));
            Objects.requireNonNull(player.getPlayer()).sendMessage
                    (TranslateColor.text('&',"&aYou paid &e$" + totalprice));

            PDC.unsetdata(player.getPlayer(), "totalprice");
            cartInventories.remove(playerID);
        }

    } // Checkout System where deletes the cart in the ConcurrentHashMap

    private static ItemStack checkOutItem(Player player) {
        ItemStack checkout = ItemStack.of(Material.PAPER);

        PDC.setdata(checkout, "playerCart", player); //Set Data

        ItemMeta cartMeta = checkout.getItemMeta();
        cartMeta.displayName(Component.text(TranslateColor.text('&', "&eCheckout " + PDC.getdata(player, "totalprice", false))));
        checkout.setItemMeta(cartMeta);

        return checkout;
    } // Add Checkout Item in the navigation bar

    public static boolean isFull(Inventory inventory, ItemStack theitem) {
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.isSimilar(theitem)) {
                if (item.getAmount() < item.getMaxStackSize()) {
                    return false;
                }
            } else if (item == null || item.getType() == Material.AIR) {
                return false;
            }
        }
        return true;
    } // Detect if Inventory is full / not

    @EventHandler(priority = EventPriority.LOW)
    private static void OnInventoryClick(InventoryClickEvent e) {

        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();

        if (item == null) return;

        if (hasCart(p.getUniqueId())) {

            if (Objects.equals(e.getInventory(), playerCart(p.getUniqueId()))) {
                e.setCancelled(true);
                if (item.getType().equals(Material.BARRIER)) {

                    p.openInventory(ShopGui.maininv);

                } else if (item.getType().equals(Material.PAPER)) {

                    if (PDC.hasData(item, "addressNav")) {
                        p.openInventory(AddressGui.get());
                    } else {
                        checkOut(p.getUniqueId());
                        p.closeInventory();
                    }


                } else {
                    if (!PDC.hasData(item, "nav")) {

                        Inventory inventory = cartInventories.get(p.getUniqueId());

                        CalcData(p, "totalPrice", (double) getdata(item, "pricetag", false), "-");

                        ItemStack oneItem = item.clone();
                        oneItem.setAmount(1);
                        inventory.removeItem(oneItem);

                        inventory.setItem(e.getInventory().getSize() - 5, checkOutItem(p));

                    }
                }
            }
        }


    } // Events Detection in the Cart inventory

    @EventHandler(priority = EventPriority.LOW)
    private static void OnInventoryClose(InventoryCloseEvent e) {

        Player p = (Player) e.getPlayer();

        if (hasCart(p.getUniqueId())) {
            if (Objects.equals(getdata(p, "totalPrice", false), 0.0)) {
                cartInventories.remove(p.getUniqueId());
            }
        }


    } // Events Detection in all the Gui

}
