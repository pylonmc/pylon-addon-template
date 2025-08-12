package com.balugaq.constructionwand.api.providers;

import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import com.destroystokyo.paper.MaterialTags;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemContainerContents;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class ShulkerBoxItemProvider implements ItemProvider {
    /**
     * The plugin that this item provider is from
     *
     * @return The plugin
     */
    @Override
    public @NotNull Plugin getPlugin() {
        return ConstructionWandPlugin.getInstance();
    }

    /**
     * Counts the amount of items the player has
     *
     * @param player   The player
     * @param material The item material, item must be a pure vanilla item.
     * @param requireAmount The max amount to consume
     * @return The amount of items the player has
     */
    @SuppressWarnings("UnstableApiUsage")
    @Override
    public int getAmount(@NotNull Player player, @NotNull Material material, int requireAmount) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return INF;
        }

        int existing = 0;
        ItemStack target = new ItemStack(material, 1);
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }

            if (!MaterialTags.SHULKER_BOXES.isTagged(itemStack.getType())) {
                continue;
            }

            ItemContainerContents contents = itemStack.getData(DataComponentTypes.CONTAINER);
            if (contents == null) {
                continue;
            }

            List<ItemStack> stacks = contents.contents();
            if (stacks == null || stacks.isEmpty()) {
                continue;
            }

            for (ItemStack stack : stacks) {
                if (stack.isSimilar(target)) {
                    existing += stack.getAmount();
                }

                if (existing >= requireAmount) {
                    // enough
                    break;
                }
            }

            if (existing >= requireAmount) {
                // enough
                break;
            }
        }

        return existing;
    }

    /**
     * Consume items when player uses filling wand / building wand
     *
     * @param player   The player
     * @param material The item material, item must be a pure vanilla item.
     * @param amount   The amount to consume
     * @return The amount of items consumed
     */
    @Override
    public int consumeItem(@NotNull Player player, @NotNull Material material, int amount) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return amount;
        }

        // In this case, amount = left;
        int scheduleConsume = amount;
        ItemStack target = new ItemStack(material, 1);
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }

            if (!MaterialTags.SHULKER_BOXES.isTagged(itemStack.getType())) {
                continue;
            }

            ItemContainerContents contents = itemStack.getData(DataComponentTypes.CONTAINER);
            if (contents == null) {
                continue;
            }

            List<ItemStack> stacks = contents.contents();
            if (stacks == null || stacks.isEmpty()) {
                continue;
            }

            for (ItemStack stack : stacks) {
                if (stack == null || stack.getType() == Material.AIR) {
                    continue;
                }

                if (stack.isSimilar(target)) {
                    int exist = stack.getAmount();
                    if (amount >= exist) {
                        stack.setAmount(0);
                        amount -= exist;
                    } else {
                        stack.setAmount(exist - amount);
                        amount = 0;
                    }

                    if (amount == 0) {
                        return scheduleConsume;
                    }
                }
            }
        }

        return scheduleConsume - amount;
    }
}
