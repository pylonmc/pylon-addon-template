package com.balugaq.constructionwand.api.items;

import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.base.PylonInteractor;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.registry.RegistryHandler;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface Wand extends Keyed, PylonInteractor, RegistryHandler {
    Map<NamespacedKey, Consumer<Wand>> handlers = new HashMap<>();

    default boolean isBlockStrict() {
        return true;
    }

    default boolean isOpOnly() {
        return false;
    }

    default @NotNull List<PylonArgument> getPlaceholders() {
        return List.of(
                PylonArgument.of("range", getLimitBlocks())
        );
    }

    int getLimitBlocks();

    void setBlockStrict(boolean blockStrict);
    void setOpOnly(boolean opOnly);
    void setLimitBlocks(int limitBlocks);

    default void onLoad(Wand instance, @NotNull NamespacedKey key) {
        handlers.getOrDefault(key, (wand) -> {}).accept(instance);
    }

    static void setOnLoad(@NotNull NamespacedKey key, @NotNull Consumer<Wand> consumer) {
        handlers.put(key, consumer);
    }

    @Override
    default void onRegister(@NotNull PylonRegistry<?> registry) {
        onLoad(this, getKey());
    }
}
