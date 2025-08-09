package com.balugaq.constructionwand.api.objects.events;

import com.balugaq.constructionwand.api.items.BreakingStaff;
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
    private final BreakingStaff breakingStaff;
    private final Block lookingAtBlock;

    public PrepareBreakingEvent(@NotNull Player player, BreakingStaff breakingStaff, Block lookingAtBlock) {
        super(player);
        this.breakingStaff = breakingStaff;
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
