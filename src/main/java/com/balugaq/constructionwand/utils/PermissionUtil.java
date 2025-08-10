package com.balugaq.constructionwand.utils;

import com.balugaq.constructionwand.api.enums.Interaction;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class PermissionUtil {
    public static boolean hasPermission(@NotNull Player player, @NotNull Block block, @NotNull Interaction interaction) {
        return hasPermission(player, block.getLocation(), interaction);
    }

    // todo: add permission system
    public static boolean hasPermission(@NotNull Player player, @NotNull Location location, @NotNull Interaction interaction) {
        return player.isOp();
    }
}
