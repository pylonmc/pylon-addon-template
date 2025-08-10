package com.balugaq.constructionwand.core.listeners;

import com.balugaq.constructionwand.api.enums.Interaction;
import com.balugaq.constructionwand.api.events.PrepareBreakingEvent;
import com.balugaq.constructionwand.api.items.BreakingWand;
import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import com.balugaq.constructionwand.implementation.WandConfig;
import com.balugaq.constructionwand.utils.Debug;
import com.balugaq.constructionwand.utils.PermissionUtil;
import com.balugaq.constructionwand.utils.WandUtil;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.displaymodellib.models.components.ModelCuboid;
import org.metamechanists.displaymodellib.sefilib.entity.display.DisplayGroup;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PrepareBreakingListener implements Listener {
    private static final ModelCuboid border = new ModelCuboid()
            .material(Material.RED_STAINED_GLASS)
            .size(0.9F, 0.9F, 0.9F);

    @EventHandler
    public void onPrepareBreaking(@NotNull PrepareBreakingEvent event) {
        if (!WandConfig.DISPLAY_PROJECTION) {
            return;
        }

        Player player = event.getPlayer();
        Debug.debug("Preparing breaking blocks...");
        BreakingWand breakingWand = event.getBreakingWand();
        if (breakingWand.isOpOnly() && !player.isOp()) {
            return;
        }
        showBreakingBlocksFor(player, event.getLookingAtBlock(), breakingWand.getLimitBlocks(), breakingWand);
    }

    private void showBreakingBlocksFor(@NotNull Player player, @NotNull Block lookingAtBlock, int limitBlocks, @NotNull BreakingWand breakingWand) {
        if (!player.isOp() && !PermissionUtil.hasPermission(player, lookingAtBlock, Interaction.BREAK_BLOCK)) {
            return;
        }

        BlockFace originalFacing = player.getTargetBlockFace(6, FluidCollisionMode.NEVER);
        if (originalFacing == null) {
            return;
        }

        Material material = lookingAtBlock.getType();
        if (WandUtil.isMaterialDisabledToBreak(material)) {
            return;
        }

        Location lookingLocation = lookingAtBlock.getLocation();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        BlockFace lookingFacing = WandUtil.getLookingFacing(originalFacing);

        Set<Location> rawLocations = WandUtil.getRawLocations(
                lookingAtBlock,
                lookingFacing,
                limitBlocks,
                WandUtil.getAxis(itemInMainHand),
                breakingWand.isBlockStrict(),
                true
        );

        World world = lookingLocation.getWorld();
        Map<Location, Double> distances = new HashMap<>();
        for (Location location : rawLocations) {
            if (world.getWorldBorder().isInside(location)) {
                double distance = location.distance(lookingLocation);
                distances.put(location, distance);
            }
        }

        // sort by shortest distance
        Set<Location> locations = new HashSet<>(distances.keySet());
        List<Location> sortedLocations = locations
                .stream()
                .sorted(Comparator.comparingDouble(distances::get))
                .limit(limitBlocks)
                .toList();

        Vector vector = lookingFacing.getOppositeFace().getDirection().multiply(0.6).add(new Vector(0.5F, 0.5F, 0.5F));
        DisplayGroup displayGroup = new DisplayGroup(player.getLocation(), 0.0F, 0.0F);
        for (Location location : sortedLocations) {
            String ls = location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
            Location displayLocation = location.clone().add(vector);
            displayGroup.addDisplay("b" + ls, border.build(displayLocation));
        }

        UUID uuid = player.getUniqueId();

        ConstructionWandPlugin.getInstance().getDisplayManager().registerDisplayGroup(uuid, displayGroup);
    }
}
