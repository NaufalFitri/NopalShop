package org.Nopal.nopalShop.Gui;

import net.kyori.adventure.text.Component;
import org.Nopal.nopalShop.Data.PDC;
import org.Nopal.nopalShop.Data.TranslateColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class NavigationBar extends PDC {

    public static void bar(int size, Inventory inv, int page, String next, String prev) {

        int startnav = size - 9;
        for (int i = startnav; i < size; i++) {
            inv.setItem(i, ItemStack.of(Material.GRAY_STAINED_GLASS_PANE));
        }

        inv.setItem(startnav, ItemStack.of(Material.PLAYER_HEAD));
        inv.setItem(size - 1, ItemStack.of(Material.BARRIER));

        ItemStack nextPage = ItemStack.of(Material.ARROW);
        ItemStack prevPage = ItemStack.of(Material.ARROW);

        if (page > 1) {

            setdata(nextPage, "nextpage", next);
            setdata(prevPage, "prevpage", prev);
            ItemMeta npMeta = nextPage.getItemMeta();
            ItemMeta ppMeta = prevPage.getItemMeta();

            npMeta.displayName(Component.text(TranslateColor.text('&',"&fNext Page")));
            nextPage.setItemMeta(npMeta);
            ppMeta.displayName(Component.text(TranslateColor.text('&',"&fPrevious Page")));
            prevPage.setItemMeta(ppMeta);

            inv.setItem(size - 4, nextPage);
            inv.setItem(size - 6, prevPage);

        }

    }

}
