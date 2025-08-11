package com.balugaq.constructionwand.api.items;

import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import com.balugaq.constructionwand.utils.Debug;
import com.balugaq.constructionwand.utils.WandUtil;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import lombok.Getter;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class BuildingWand extends PylonItem implements Wand {
    private final int limitBlocks = getOrThrow("limit-blocks", Integer.class);
    private final boolean blockStrict = getOrThrow("block-strict", Boolean.class);
    private final boolean opOnly = getOrThrow("op-only", Boolean.class);
    private final long cooldown = getOrThrow("cooldown", Integer.class);

    public BuildingWand(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onUsedToClickBlock(@NotNull PlayerInteractEvent event) {
        WandUtil.placeBlocks(ConstructionWandPlugin.getInstance(), event, isDisabled(), limitBlocks, blockStrict, opOnly);
    }

    @Override
    public @NotNull List<PylonArgument> getPlaceholders() {
        return List.of(
                PylonArgument.of("range", getLimitBlocks())
        );
    }
}
