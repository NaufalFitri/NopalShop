package org.Nopal.nopalShop.Gui;

import net.kyori.adventure.text.Component;
import org.Nopal.nopalShop.Data.PDC;
import org.Nopal.nopalShop.Data.TranslateColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NavigationBar extends PDC {

    public static void bar(int size, Inventory inv, int page, String next, String prev) {

        int startNav = size - 9;

        for (int i = startNav; i < size; i++) {
            ItemStack navBorder = navItem(Material.GRAY_STAINED_GLASS_PANE, "");

            inv.setItem(i, navBorder);
        }

        if (Objects.equals(next, "checkout")) {
            inv.setItem(size - 5, navItem(Material.PAPER, "&eCheck Out"));
        }

        inv.setItem(startNav, navItem(Material.PLAYER_HEAD, "&eYour name"));
        inv.setItem(size - 1, navItem(Material.BARRIER, "&cClose"));

        ItemStack nextPage = navItem(Material.ARROW, "&fNext Page");
        ItemStack prevPage = navItem(Material.ARROW, "&fPrevious Page");

        if (page > 1) {

            setdata(nextPage, "nextpage", next);
            setdata(prevPage, "prevpage", prev);

            if (!prev.contains(String.valueOf(0))) {
                inv.setItem(size - 6, prevPage);
            }

            if (!next.contains(String.valueOf(page + 1))) {
                inv.setItem(size - 4, nextPage);
            }

        }

    } // Initializing the bar in all the inventory

    public static ItemStack navItem(Material material, String displayName, String... lore) {

        ItemStack item = ItemStack.of(material);

        setdata(item, "nav", true);

        List<Component> componentLore = Arrays.stream(lore).map(Component::text).collect(Collectors.toList());

        ItemMeta navMeta = item.getItemMeta();
        navMeta.displayName(Component.text(TranslateColor.text('&', displayName)));
        navMeta.lore(componentLore);

        item.setItemMeta(navMeta);

        return item;


    } // Adjust the item before inputting inti the navigation bar

    public static void Nav(int size, Inventory inventory, int pages, String category, int page) {
        if (pages > 1) {
            String nextPage = category + (page + 1);
            String prevPage = category + (page - 1);

            if (page - 1 == 1) {
                prevPage = category;
            }

            NavigationBar.bar(size, inventory, pages, nextPage, prevPage);
        } else {
            NavigationBar.bar(size, inventory, pages, category, category);
        }
    } // The Navigation button for next page and prev page
}
