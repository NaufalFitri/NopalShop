package org.Nopal.nopalShop.Data;

import net.kyori.adventure.text.Component;
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
                    case Double d -> itemData.set(key, PersistentDataType.DOUBLE, d);
                    case null, default -> itemData.set(key, PersistentDataType.STRING, serialize(data));
                }
                item.setItemMeta(itemMeta);

            }
        } else if (what instanceof Player) {

            PersistentDataContainer playerData = ((Player) what).getPersistentDataContainer();
            switch (data) {
                case String s -> playerData.set(key, PersistentDataType.STRING, s);
                case Integer i -> playerData.set(key, PersistentDataType.INTEGER, i);
                case Boolean b -> playerData.set(key, PersistentDataType.BOOLEAN, b);
                case Double d -> playerData.set(key, PersistentDataType.DOUBLE, d);
                case null, default -> playerData.set(key, PersistentDataType.STRING, serialize(data));
            }

        }

    }

    public static void unsetdata(Object what, String whatkey) {
        NamespacedKey key = new NamespacedKey(NopalShop.plugin(), whatkey);

        if (what instanceof ItemStack item) {
            ItemMeta itemMeta = item.getItemMeta();

            if (!Objects.isNull(itemMeta)) {
                PersistentDataContainer itemData = itemMeta.getPersistentDataContainer();

                itemData.remove(key);
                item.setItemMeta(itemMeta);

            }
        } else if (what instanceof Player) {

            PersistentDataContainer playerData = ((Player) what).getPersistentDataContainer();
            playerData.remove(key);

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
                } else if (itemData.has(key, PersistentDataType.DOUBLE)) {
                    thedata = itemData.get(key, PersistentDataType.DOUBLE);
                }
            }

        } else if (what instanceof Player p) {
            PersistentDataContainer playerData = p.getPersistentDataContainer();

            if (playerData.has(key, PersistentDataType.STRING)) {
                thedata = playerData.get(key, PersistentDataType.STRING);

                if (deep) {
                    thedata = deserialize((String) thedata);
                }

            } else if (playerData.has(key, PersistentDataType.INTEGER)) {
                thedata = playerData.get(key, PersistentDataType.INTEGER);
            } else if (playerData.has(key, PersistentDataType.BOOLEAN)) {
                thedata = playerData.get(key, PersistentDataType.BOOLEAN);
            } else if (playerData.has(key, PersistentDataType.DOUBLE)) {
                thedata = playerData.get(key, PersistentDataType.DOUBLE);
            }

        }
        return thedata;
    }

    public static boolean hasData(Object what, String keyvalue) {

        NamespacedKey key = new NamespacedKey(NopalShop.plugin(), keyvalue);

        if (what instanceof ItemStack) {
            ItemMeta whatMeta = ((ItemStack) what).getItemMeta();
            PersistentDataContainer data = whatMeta.getPersistentDataContainer();
            return data.has(key);
        } else if (what instanceof Player) {
            PersistentDataContainer data = ((Player) what).getPersistentDataContainer();
            return data.has(key);
        }

        return false;
    }

    public static void CalcData(Object what, String whatkey, double num, String operation) {

        Object data = getdata(what, whatkey, false);
        switch (operation) {
            case "+" -> setdata(what, whatkey, (double) data + num);
            case "-" -> setdata(what, whatkey, (double) data - num);
            case "*" -> setdata(what, whatkey, (double) data * num);
            case "/" -> setdata(what, whatkey, (double) data / num);
        }

    }

}
