package com.asintoto.replenish.managers;

import com.asintoto.replenish.Replenish;
import com.asintoto.replenish.utils.PluginKeys;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

@RequiredArgsConstructor
public class StatusManager {
    private final Replenish plugin;

    public boolean isEnabled(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();

        return pdc.has(PluginKeys.REPLANTING);
    }

    public void enable(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();

        pdc.set(PluginKeys.REPLANTING, PersistentDataType.BOOLEAN, true);
    }

    public void disable(Player player) {
        if(!isEnabled(player)) return;

        PersistentDataContainer pdc = player.getPersistentDataContainer();

        pdc.remove(PluginKeys.REPLANTING);
    }
}
