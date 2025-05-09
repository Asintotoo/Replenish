package com.asintoto.replenish.utils;

import com.asintoto.replenish.Replenish;
import org.bukkit.NamespacedKey;

public final class PluginKeys {
    private static final Replenish plugin = Replenish.getInstance();

    public static final NamespacedKey REPLANTING = new NamespacedKey(plugin, "replanting");

    private PluginKeys() {}
}
