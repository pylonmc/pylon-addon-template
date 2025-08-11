package com.balugaq.constructionwand.api.items;

import com.balugaq.constructionwand.core.managers.ConfigManager;
import io.github.pylonmc.pylon.core.item.base.PylonBlockInteractor;
import org.bukkit.Keyed;
import org.jetbrains.annotations.NotNull;

public interface Wand extends Keyed, PylonBlockInteractor {
    boolean isBlockStrict();

    boolean isOpOnly();

    int getLimitBlocks();

    default <T> T getOrThrow(@NotNull String key, @NotNull Class<T> clazz) {
        return ConfigManager.getSettingOrThrow(getKey(), key, clazz);
    }
}
