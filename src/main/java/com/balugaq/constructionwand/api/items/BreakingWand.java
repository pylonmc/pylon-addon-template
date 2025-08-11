package com.balugaq.constructionwand.api.items;

import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import com.balugaq.constructionwand.utils.WandUtil;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonInteractor;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.registry.RegistryHandler;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class BreakingWand extends PylonItem implements Wand, PylonInteractor, RegistryHandler {
    private int limitBlocks;
    private boolean blockStrict;
    private boolean opOnly;

    public BreakingWand(@NotNull ItemStack stack) {
        super(stack);
    }

    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        WandUtil.breakBlocks(ConstructionWandPlugin.getInstance(), event, isDisabled(), this.limitBlocks, this.blockStrict, this.opOnly);
    }

    @Override
    public void onRegister(@NotNull PylonRegistry<?> registry) {
        onLoad(this, getKey());
    }
}
