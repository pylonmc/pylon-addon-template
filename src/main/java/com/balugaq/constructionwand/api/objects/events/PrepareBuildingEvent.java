package com.balugaq.constructionwand.api.objects.events;

import com.balugaq.constructionwand.api.items.BuildingStaff;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@Getter
public class PrepareBuildingEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final BuildingStaff buildingStaff;
    private final Block lookingAtBlock;

    public PrepareBuildingEvent(@NotNull Player player, BuildingStaff buildingStaff, Block lookingAtBlock) {
        super(player);
        this.buildingStaff = buildingStaff;
        this.lookingAtBlock = lookingAtBlock;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
