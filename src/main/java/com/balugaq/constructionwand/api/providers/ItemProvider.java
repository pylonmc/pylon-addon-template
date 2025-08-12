package com.balugaq.constructionwand.api.providers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public interface ItemProvider {
    List<ItemProvider> PROVIDERS = new CopyOnWriteArrayList<>();
    int INF = 4096;

    /**
     * The plugin that this item provider is from
     * @return The plugin
     */
    @NotNull Plugin getPlugin();

    /**
     * Counts the amount of items the player has
     * @param player The player
     * @param material The item material, item must be a pure vanilla item.
     * @param requireAmount The max amount to consume
     * @return The amount of items the player has
     */
    @Range(from = 0, to = Integer.MAX_VALUE)
    int getAmount(
            @NotNull Player player,
            @NotNull Material material,
            @Range(from = 1, to = Integer.MAX_VALUE) int requireAmount
    );

    /**
     * Consume items when player uses filling wand / building wand
     * @param player The player
     * @param material The item material, item must be a pure vanilla item.
     * @param amount The amount to consume
     * @return The amount of items consumed
     */
    @Range(from = 0, to = Integer.MAX_VALUE)
    int consumeItem(
            @NotNull Player player,
            @NotNull Material material,
            @Range(from = 1, to = Integer.MAX_VALUE) int amount
    );

    /**
     * Register an item provider, be used when player uses filling wand / building wand
     * @param provider The item provider
     */
    static void registerProvider(@NotNull ItemProvider provider) {
        PROVIDERS.add(provider);
    }

    /**
     * Counts the amount of items the player has
     * @param player The player
     * @param material The item material, item must be a pure vanilla item.
     * @param requireAmount The max amount to consume
     * @return The amount of items the player has
     */
    static @Range(from = 0, to = Integer.MAX_VALUE) int getItemAmount(@NotNull Player player, @NotNull Material material, @Range(from = 1, to = Integer.MAX_VALUE) int requireAmount) {
        int total = 0;
        for (ItemProvider provider : PROVIDERS) {
            int got = provider.getAmount(player, material, requireAmount);
            requireAmount -= got;
            total += got;
            if (requireAmount <= 0) {
                break;
            }
        }
        return total;
    }

    /**
     * Consume items when player uses filling wand / building wand
     * @param player The player
     * @param material The item material, item must be a pure vanilla item.
     * @param amount The amount to consume
     * @return The amount of items consumed
     */
    static @Range(from = 0, to = Integer.MAX_VALUE) int consumeItems(@NotNull Player player, @NotNull Material material, @Range(from = 1, to = Integer.MAX_VALUE) int amount) {
        int total = 0;
        for (ItemProvider provider : PROVIDERS) {
            int consumed = provider.consumeItem(player, material, amount);
            amount -= consumed;
            total += consumed;
            if (amount <= 0) {
                break;
            }
        }
        return total;
    }
}
