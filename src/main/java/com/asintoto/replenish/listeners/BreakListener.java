package com.asintoto.replenish.listeners;

import com.asintoto.replenish.Replenish;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class BreakListener implements Listener {
    private final Replenish plugin;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.isCancelled()) return;

        if (!plugin.getConfig().getBoolean("enable")) return; // Replenish disabled

        Player player = e.getPlayer();

        boolean creative = plugin.getConfig().getBoolean("disable-while-in-creative");
        if(creative && player.getGameMode() == GameMode.CREATIVE) return; // Creative disabled and player is in creative

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        boolean isHoe = isHoe(itemInHand);
        boolean isEnable = plugin.getStatusManager().isEnabled(player);
        boolean replantingHoe = plugin.getConfig().getBoolean("replanting-hoe");

        if (!replantingHoe && !isEnable) return; // Hoe disabled and status disabled
        if (!isEnable && !isHoe) return; // Status disabled and item is not a hoe

        Block block = e.getBlock();
        Ageable ageable = (Ageable) block.getBlockData();
        int age = ageable.getAge();
        int maxAge = ageable.getMaximumAge();
        boolean sameAge = maxAge == age;

        if(plugin.getConfig().getBoolean("prevent-breaking-when-not-full-grown") && !sameAge) {
            e.setCancelled(true);
            return;
        }

        Material material = null;
        Material finalMaterial = block.getType();
        switch (finalMaterial) {
            case WHEAT -> material = Material.WHEAT_SEEDS;
            case CARROTS -> material = Material.CARROT;
            case POTATOES -> material = Material.POTATO;
            case BEETROOTS -> material = Material.BEETROOT_SEEDS;
            case NETHER_WART -> material = Material.NETHER_WART;
            case COCOA -> material = Material.COCOA_BEANS;
        }
        if (material == null)
            return;
        Collection<ItemStack> drops = block.getDrops(itemInHand, player);

        boolean replenish = false;
        for (ItemStack drop : drops) {
            if (drop.getType() == material) {
                drop.setAmount(drop.getAmount() - 1);
                replenish = true;
                if (drop.getAmount() <= 0)
                    drops.remove(drop);
                break;
            }
        }

        if (!replenish && consumeItem(player, 1, material)) replenish = true;

        if (replenish) {

            e.setDropItems(false);
            for (ItemStack drop : drops) {
                block.getWorld().dropItemNaturally(block.getLocation(), drop);
            }

            ageable.setAge(sameAge ? 0 : age);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                block.setType(finalMaterial, true);
                block.setBlockData(ageable);
            }, 3);
        }
    }

    public boolean consumeItem(Player player, int count, Material material) {
        Map<Integer, ? extends ItemStack> available = player.getInventory().all(material);

        int found = 0;
        for (ItemStack stack : available.values()) {
            found += stack.getAmount();
        }
        if (count > found) return false;

        for (Integer index : available.keySet()) {
            ItemStack stack = available.get(index);

            int removed = Math.min(count, stack.getAmount());
            count -= removed;

            if (stack.getAmount() == removed)
                player.getInventory().setItem(index, null);
            else
                stack.setAmount(stack.getAmount() - removed);

            if (count <= 0)
                break;
        }

        player.updateInventory();
        return true;
    }

    private boolean isHoe(ItemStack item) {
        if (item == null) return false;

        return (item.getType() == Material.WOODEN_HOE ||
                item.getType() == Material.STONE_HOE ||
                item.getType() == Material.IRON_HOE ||
                item.getType() == Material.GOLDEN_HOE ||
                item.getType() == Material.DIAMOND_HOE ||
                item.getType() == Material.NETHERITE_HOE);
    }
}
