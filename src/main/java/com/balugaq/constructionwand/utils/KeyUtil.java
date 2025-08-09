package com.balugaq.constructionwand.utils;

import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import lombok.experimental.UtilityClass;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@UtilityClass
public class KeyUtil {
    public static @NotNull NamespacedKey AXIS = newKey("axis");

    public static @NotNull NamespacedKey newKey(@NotNull String key) {
        return new NamespacedKey(ConstructionWandPlugin.getInstance(), key);
    }

    public static @NotNull NamespacedKey newKey(@NotNull String pluginName, @NotNull String key) {
        return new NamespacedKey(pluginName, key);
    }

    public static @NotNull NamespacedKey newKey(@NotNull Plugin plugin, @NotNull String key) {
        return new NamespacedKey(plugin, key);
    }
}
