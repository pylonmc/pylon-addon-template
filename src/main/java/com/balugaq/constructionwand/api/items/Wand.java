package com.balugaq.constructionwand.api.items;

import com.balugaq.constructionwand.core.managers.ConfigManager;
import io.github.pylonmc.pylon.core.item.base.PylonBlockInteractor;
import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public interface Wand extends Keyed, PylonBlockInteractor {
    Map<UUID, Long> COOLDOWNS = new ConcurrentHashMap<>();

    boolean isBlockStrict();

    boolean isOpOnly();

    int getLimitBlocks();

    long getCooldown();

    default <T> T getOrThrow(@NotNull String key, @NotNull Class<T> clazz) {
        return ConfigManager.getSettingOrThrow(getKey(), key, clazz);
    }

    default boolean isCooldowning(@NotNull Player player) {
        UUID key = player.getUniqueId();
        Long lastUse = COOLDOWNS.get(key);
        if (lastUse == null) {
            COOLDOWNS.put(key, System.currentTimeMillis());
            return false;
        } else {
            if (lastUse + getCooldown() > System.currentTimeMillis()) {
                return true;
            } else {
                COOLDOWNS.put(key, System.currentTimeMillis());
                return false;
            }
        }
    }
}
