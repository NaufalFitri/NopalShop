package org.Nopal.nopalShop.Gui;

import net.kyori.adventure.text.Component;
import org.Nopal.nopalShop.Configurations;
import org.Nopal.nopalShop.Data.PDC;
import org.Nopal.nopalShop.Data.TranslateColor;
import org.Nopal.nopalShop.NopalShop;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.HashSet;
import java.util.Objects;

public class AddressGui implements Listener {

    private static Inventory AddressGui;
    public static HashSet<Location> playerLoc = new HashSet<>();

    public static void init() {

        AddressGui = Bukkit.createInventory(null, 54, Component.text("Player's Addresses"));

        NavigationBar.bar(54, AddressGui, 1, "address", "address");

    }

    public static void createItemAddress(Location loc, OfflinePlayer p) {

        // Get Config for Material of item

        ItemStack item = new ItemStack(Material.PAPER);

        PDC.setdata(item, "address", loc);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text(TranslateColor.text('&', "&a" + p.getName() + "'s Address")));
        item.setItemMeta(meta);

        AddressGui.addItem(item);

    }

    public static ItemStack AddressNav() {

        ItemStack addressNav = ItemStack.of(Material.PAPER);

        PDC.setdata(addressNav, "addressNav", true); //Set Data

        ItemMeta addressMeta = addressNav.getItemMeta();
        addressMeta.displayName(Component.text(TranslateColor.text('&', "&eAddress")));
        addressNav.setItemMeta(addressMeta);

        return addressNav;

    }

    public static Inventory get() {
        return AddressGui;
    }

    @EventHandler(priority = EventPriority.LOW)
    protected static void OnInventoryClick(InventoryClickEvent e) {

        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();

        if (e.getInventory().equals(AddressGui)) {
            e.setCancelled(true);
            if (item.getType().equals(Material.BARRIER)) {
                p.openInventory(ShopGui.maininv);
            }
        }

    }

    @EventHandler(priority = EventPriority.LOW)
    private static void OnSignPlace(SignChangeEvent e) {

        Player p = e.getPlayer();
        Component message = e.line(0);
        Location below = e.getBlock().getLocation().subtract(0, 1, 0);

        if (Objects.equals(message, Component.text("Address"))) {
            if (!PDC.hasData(p, "address")) {
                if (below.getBlock().getType().equals(Material.AIR) || below.getBlock().getType() == null) {
                    Configurations.createAddress(p.getUniqueId(), e.getBlock().getLocation());
                    PDC.setdata(p, "address", true);
                    createItemAddress(below, p);
                    p.sendMessage(TranslateColor.text('&', "&aYou have set an address!"));
                } else {
                    p.sendMessage(TranslateColor.text('&', "&cBelow sign is not safe for delivery"));
                }
            } else {
                p.sendMessage(TranslateColor.text('&', "&cYou already have an address"));
            }

        } else if (PDC.hasData(p, "address")) {

            DeleteFile(e.getBlock().getLocation(), p);
            playerLoc.remove(below);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private static void OnSignRemove(BlockBreakEvent e) {

        Player p = e.getPlayer();
        Location loc = e.getBlock().getLocation();

        if (e.getBlock().getType().toString().toLowerCase().contains("sign")) {
            if (PDC.hasData(p, "address")) {
                DeleteFile(loc, p);
            }
        }
    }

    protected static void DeleteFile(Location loc, Player p) {
        FileConfiguration playerAddress = Configurations.addresses.get(Bukkit.getOfflinePlayer(p.getUniqueId()));
        Location aloc = playerAddress.getLocation("address");

        if (loc.equals(aloc)) {
            File addressfile = new File(NopalShop.plugin().getDataFolder(), "playerAddress/" + p.getUniqueId() + ".yml");

            if (addressfile.exists()) {
                addressfile.delete();
                Configurations.addresses.remove(Bukkit.getOfflinePlayer(p.getUniqueId()));
                PDC.unsetdata(p, "address");
            }
        }
    }

}
