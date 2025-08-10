package com.balugaq.constructionwand.api.events;

import com.balugaq.constructionwand.api.items.BreakingWand;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@Getter
public class PrepareBreakingEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final @NotNull BreakingWand breakingWand;
    private final @NotNull Block lookingAtBlock;

    public PrepareBreakingEvent(@NotNull Player player, @NotNull BreakingWand breakingWand, @NotNull Block lookingAtBlock) {
        super(player);
        this.breakingWand = breakingWand;
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
