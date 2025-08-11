package com.balugaq.constructionwand.api.items;

import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import com.balugaq.constructionwand.utils.WandUtil;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonInteractor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class BreakingWand extends PylonItem implements Wand, PylonInteractor {
    private final int limitBlocks;
    private final boolean blockStrict;
    private final boolean opOnly;

    public BreakingWand(@NotNull ItemStack stack, int limitBlocks, boolean blockStrict, boolean opOnly) {
        super(stack);
        this.limitBlocks = limitBlocks;
        this.blockStrict = blockStrict;
        this.opOnly = opOnly;
    }

    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        WandUtil.breakBlocks(ConstructionWandPlugin.getInstance(), event, isDisabled(), this.limitBlocks, this.blockStrict, this.opOnly);
    }

    @NotNull
    private Material materialValueOf(@NotNull String name) {
        try {
            return Material.valueOf(name);
        } catch (IllegalArgumentException | NullPointerException e) {
            return Material.AIR;
        }
    }
}
