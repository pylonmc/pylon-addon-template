package com.balugaq.constructionwand.api.items;

import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Wand {
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
}
