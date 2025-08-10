package com.balugaq.constructionwand.api.events;

import com.balugaq.constructionwand.api.items.BuildingWand;
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
    private final @NotNull BuildingWand buildingWand;
    private final @NotNull Block lookingAtBlock;

    public PrepareBuildingEvent(@NotNull Player player, @NotNull BuildingWand buildingWand, @NotNull Block lookingAtBlock) {
        super(player);
        this.buildingWand = buildingWand;
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
