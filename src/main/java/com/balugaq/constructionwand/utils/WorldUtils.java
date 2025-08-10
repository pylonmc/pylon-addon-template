package com.balugaq.constructionwand.utils;

import com.balugaq.constructionwand.api.collections.Pair;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.function.Consumer;

@ApiStatus.Experimental
@SuppressWarnings("unused")
public class WorldUtils {
    protected static @Nullable Class<?> craftBlockStateClass;
    protected static @Nullable Field interfaceBlockDataField;
    protected static @Nullable Field blockPositionField;
    protected static @Nullable Field worldField;
    protected static @Nullable Field weakWorldField;
    protected static boolean success = false;

    static {
        try {
            World sampleWorld = Bukkit.getWorlds().get(0);
            BlockState blockstate = sampleWorld.getBlockAt(0, 0, 0).getState();
            Pair<Field, Class<?>> result = ReflectionUtil.getDeclaredFieldsRecursively(blockstate.getClass(), "data");
            interfaceBlockDataField = result.getFirst();
            interfaceBlockDataField.setAccessible(true);
            craftBlockStateClass = result.getSecond();
            blockPositionField = ReflectionUtil.getDeclaredFieldsRecursively(craftBlockStateClass, "position").getFirst();
            blockPositionField.setAccessible(true);
            worldField = ReflectionUtil.getDeclaredFieldsRecursively(craftBlockStateClass, "world").getFirst();
            worldField.setAccessible(true);
            weakWorldField = ReflectionUtil.getDeclaredFieldsRecursively(craftBlockStateClass, "weakWorld").getFirst();
            weakWorldField.setAccessible(true);
            success = true;
        } catch (NullPointerException ignored) {

        }
    }

    @CanIgnoreReturnValue
    public static boolean copyBlockState(@Nonnull BlockState fromBlockState, @Nonnull Block toBlock) {
        if (!success) {
            return false;
        }

        BlockState toState = toBlock.getState();
        if (!craftBlockStateClass.isInstance(toState) || !craftBlockStateClass.isInstance(fromBlockState)) {
            return false;
        }

        try {
            blockPositionField.set(fromBlockState, blockPositionField.get(toState));
            worldField.set(fromBlockState, worldField.get(toState));
            weakWorldField.set(fromBlockState, weakWorldField.get(toState));
            fromBlockState.update(true, false);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static @NotNull String locationToString(@Nonnull Location l) {
        if (l == null) {
            return "Unknown Location";
        }
        if (l.getWorld() == null) {
            return "Unknown Location";
        }
        return l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
    }

    public static long locationRange(@Nonnull Location pos1, @Nonnull Location pos2) {
        if (pos1 == null || pos2 == null) {
            return 0;
        }

        final int downX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        final int upX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        final int downY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        final int upY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        final int downZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        final int upZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
        return (long) (Math.abs(upX - downX) + 1) * (Math.abs(upY - downY) + 1) * (Math.abs(upZ - downZ) + 1);
    }

    public static void doWorldEdit(@Nonnull Location pos1, @Nonnull Location pos2, @Nonnull Consumer<Location> consumer) {
        if (pos1 == null || pos2 == null) {
            return;
        }
        final int downX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        final int upX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        final int downY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        final int upY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        final int downZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        final int upZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        for (int x = downX; x <= upX; x++) {
            for (int y = downY; y <= upY; y++) {
                for (int z = downZ; z <= upZ; z++) {
                    consumer.accept(new Location(pos1.getWorld(), x, y, z));
                }
            }
        }
    }

    public static long getRange(@Nonnull Location pos1, @Nonnull Location pos2) {
        if (pos1 == null || pos2 == null) {
            return 0;
        }
        final int downX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        final int upX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        final int downY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        final int upY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        final int downZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        final int upZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
        return (long) (Math.abs(upX - downX) + 1) * (Math.abs(upY - downY) + 1) * (Math.abs(upZ - downZ) + 1);
    }
}