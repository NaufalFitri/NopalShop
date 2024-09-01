package org.Nopal.nopalShop;

import net.kyori.adventure.text.Component;
import org.Nopal.nopalShop.Gui.AddressGui;
import org.Nopal.nopalShop.Gui.CategoryGui;
import org.Nopal.nopalShop.Gui.PlayerCart;
import org.Nopal.nopalShop.Gui.ShopGui;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Objects;

public final class NopalShop extends JavaPlugin {

    FileConfiguration config = getConfig();
    private static NopalShop instance;

    public static Economy econ = null;
    public static Permission perms = null;
    public static Chat chat = null;
    public static String prefix;

    @Override
    public void onEnable() {

        prefix = config.getString("Prefix");

        instance = this;
        config = getConfig();

        setupConfigurations();
        registerEvents();
        registerCommands();
        setupDependencies();

        getLogger().info("NopalShop Enabled");
        getServer().sendMessage(Component.text(prefix + "NopalShop Reloaded"));

    }

    private void setupConfigurations() {
        new Configurations(this, config);
        saveDefaultConfig();

        Configurations.AddressFolder();

        Configurations.CategoryConfig(config);
        Configurations.get().options().copyDefaults(true);
        Configurations.save();
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new ShopGui(this), this);
        getServer().getPluginManager().registerEvents(new CategoryGui(), this);
        getServer().getPluginManager().registerEvents(new PlayerCart(), this);
        getServer().getPluginManager().registerEvents(new AddressGui(), this);
        CategoryGui.init();
        AddressGui.init();
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("nsreload")).setExecutor(new Commands(this));
        Objects.requireNonNull(getCommand("shop")).setExecutor(new Commands(this));
    }

    private void setupDependencies() {
        if (!setupEconomy()) {
            getLogger().severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
        } else {
            setupPermissions();
            setupChat();
        }
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp == null) {
            return false;
        }
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            return false;
        }
        perms = rsp.getProvider();
        return perms != null;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static Permission getPermissions() {
        return perms;
    }

    public static Chat getChat() {
        return chat;
    }

    public static Plugin plugin(){
        return instance;
    }

    @Override
    public void onDisable() {
        getLogger().info("NopalShop Disabled");
    }

}
