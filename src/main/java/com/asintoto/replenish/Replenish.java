package com.asintoto.replenish;

import com.asintoto.replenish.commands.MainCommand;
import com.asintoto.replenish.listeners.BreakListener;
import com.asintoto.replenish.managers.StatusManager;
import com.asintoto.replenish.utils.Config;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Replenish extends JavaPlugin {

    private Config messages;
    @Getter
    private StatusManager statusManager;

    @SuppressWarnings("all")
    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.messages = new Config(this, "messages.yml");

        this.statusManager = new StatusManager(this);

        getServer().getPluginManager().registerEvents(new BreakListener(this), this);

        MainCommand command = new MainCommand(this);
        getCommand("replenish").setExecutor(command);
        getCommand("replenish").setTabCompleter(command);

        getLogger().info("Replenish: Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Replenish: Disabled!");
    }

    public void reload() {
        this.reloadConfig();
        this.messages.reload();
    }

    public YamlConfiguration getMessages() {
        return this.messages.getConfig();
    }

    public static Replenish getInstance() {
        return getPlugin(Replenish.class);
    }
}
