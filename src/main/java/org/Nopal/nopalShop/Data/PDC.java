package org.Nopal.nopalShop.Data;

import org.Nopal.nopalShop.NopalShop;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class PDC extends Storing {

    public static void setdata(Object what, String whatkey, Object data) {
        NamespacedKey key = new NamespacedKey(NopalShop.plugin(), whatkey);

        if (what instanceof ItemStack item) {
            ItemMeta itemMeta = item.getItemMeta();

            if (!Objects.isNull(itemMeta)) {
                PersistentDataContainer itemData = itemMeta.getPersistentDataContainer();

                switch (data) {
                    case String s -> itemData.set(key, PersistentDataType.STRING, s);
                    case Integer i -> itemData.set(key, PersistentDataType.INTEGER, i);
                    case Boolean b -> itemData.set(key, PersistentDataType.BOOLEAN, b);
                    case null, default -> itemData.set(key, PersistentDataType.STRING, serialize(data));
                }
                item.setItemMeta(itemMeta);

            }
        }

    }

    public static Object getdata(Object what, String whatkey, boolean deep) {
        NamespacedKey key = new NamespacedKey(NopalShop.plugin(), whatkey);
        Object thedata = null;
        
        if (what instanceof ItemStack item) {
            ItemMeta itemMeta = item.getItemMeta();
            if (!Objects.isNull(itemMeta)) {
                PersistentDataContainer itemData = itemMeta.getPersistentDataContainer();

                if (itemData.has(key, PersistentDataType.STRING)) {
                    thedata = itemData.get(key, PersistentDataType.STRING);

                    if (deep) {
                        thedata = deserialize((String) thedata);
                    }

                } else if (itemData.has(key, PersistentDataType.INTEGER)) {
                    thedata = itemData.get(key, PersistentDataType.INTEGER);
                } else if (itemData.has(key, PersistentDataType.BOOLEAN)) {
                    thedata = itemData.get(key, PersistentDataType.BOOLEAN);
                }
            }

        } else if (what instanceof Player p) {
            PersistentDataContainer playerData = p.getPersistentDataContainer();
            thedata = playerData.get(key, PersistentDataType.STRING);

        }
        return thedata;
    }

}
