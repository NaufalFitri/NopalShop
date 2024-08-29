package org.Nopal.nopalShop;

import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Objects;

public final class NopalShop extends JavaPlugin {

    FileConfiguration config = getConfig();

    public static Economy econ = null;
    public static Permission perms = null;
    public static Chat chat = null;

    @Override
    public void onEnable() {

        new Configurations(this, getConfig());

        String prefix = (String) config.get("Prefix");

        getLogger().info("NopalShop Enabled");
        getServer().sendMessage(Component.text(prefix + "NopalShop Reloaded"));

        getServer().getPluginManager().registerEvents(new ShopGui(this), this);

        // Register Commands

        for (RegisteredServiceProvider<?> service : getServer().getServicesManager().getRegistrations(Economy.class)) {
            getLogger().info("Registered Economy Service: " + service.getProvider().getClass().getName());
        }
        
        Objects.requireNonNull(getCommand("nsreload")).setExecutor(new Commands(this));
        Objects.requireNonNull(getCommand("shop")).setExecutor(new Commands(this));

        if (!setupEconomy() ) {
            getLogger().severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        setupPermissions();
        setupChat();

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

    @Override
    public void onDisable() {
        getLogger().info("NopalShop Disabled");
    }

}
