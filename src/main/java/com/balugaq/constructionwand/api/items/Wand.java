package com.balugaq.constructionwand.api.items;

import com.balugaq.constructionwand.utils.Keys;
import com.balugaq.constructionwand.utils.PersistentUtil;
import org.bukkit.Axis;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Wand {
    @Nullable
    default Axis getAxis(@NotNull ItemStack item) {
        String axis = PersistentUtil.getOrDefault(item, PersistentDataType.STRING, Keys.AXIS, null);
        if (axis == null) {
            return null;
        }

        try {
            return Axis.valueOf(axis);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    default void setAxis(@NotNull ItemStack item, @Nullable Axis axis) {
        if (axis == null) {
            PersistentUtil.set(item, PersistentDataType.STRING, Keys.AXIS, "null");
        } else {
            PersistentUtil.set(item, PersistentDataType.STRING, Keys.AXIS, axis.name());
        }
    }

    default boolean isBlockStrict() {
        return true;
    }
}
