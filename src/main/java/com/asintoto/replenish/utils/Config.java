package com.asintoto.replenish.utils;

import com.asintoto.replenish.Replenish;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

@SuppressWarnings("all")
public class Config {
    @Getter
    private File file;
    @Getter
    private YamlConfiguration config;
    @Getter
    private String fileName;

    private final Replenish plugin;

    public Config(Replenish plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;

        file = new File(plugin.getDataFolder(), fileName);

        if(!file.exists()) {
            plugin.saveResource(fileName, false);
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    @SneakyThrows
    public void save() {
        config.save(file);
        reload();
    }
}
