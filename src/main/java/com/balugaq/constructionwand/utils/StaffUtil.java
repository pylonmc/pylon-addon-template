package com.balugaq.constructionwand.utils;

import org.bukkit.Axis;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

@SuppressWarnings("unused")
public class StaffUtil {
    public static final Set<BlockFace> validFaces = new HashSet<>();

    static {
        validFaces.add(BlockFace.NORTH);
        validFaces.add(BlockFace.SOUTH);
        validFaces.add(BlockFace.EAST);
        validFaces.add(BlockFace.WEST);
        validFaces.add(BlockFace.UP);
        validFaces.add(BlockFace.DOWN);
    }


    public static @NotNull Set<Location> getBuildingLocations(@NotNull Player player, int limitBlocks, Axis onlyAxis, boolean blockStrict) {
        if (limitBlocks <= 0) {
            return new HashSet<>();
        }

        Block lookingBlock = player.getTargetBlockExact(6, FluidCollisionMode.NEVER);
        if (lookingBlock == null || lookingBlock.getType().isAir()) {
            return new HashSet<>();
        }

        BlockFace originalFacing = player.getTargetBlockFace(6, FluidCollisionMode.NEVER);
        if (originalFacing == null) {
            return new HashSet<>();
        }
        BlockFace lookingFacing = getLookingFacing(originalFacing);

        return getLocations(lookingBlock, lookingFacing, limitBlocks, onlyAxis, blockStrict);
    }

    public static @NotNull BlockFace getLookingFacing(@NotNull BlockFace originalFacing) {
        BlockFace lookingFacing = originalFacing.getOppositeFace();
        if (!originalFacing.isCartesian()) {
            switch (originalFacing) {
                case NORTH_EAST, NORTH_WEST, NORTH_NORTH_EAST, NORTH_NORTH_WEST -> lookingFacing = BlockFace.NORTH;
                case SOUTH_EAST, SOUTH_WEST, SOUTH_SOUTH_EAST, SOUTH_SOUTH_WEST -> lookingFacing = BlockFace.SOUTH;
                case EAST_NORTH_EAST, EAST_SOUTH_EAST -> lookingFacing = BlockFace.EAST;
                case WEST_NORTH_WEST, WEST_SOUTH_WEST -> lookingFacing = BlockFace.WEST;
            }
        }

        return lookingFacing;
    }

    public static @NotNull Set<Location> getLocations(@NotNull Block lookingBlock, @NotNull BlockFace lookingFacing, int limitBlocks, Axis onlyAxis, boolean blockStrict) {
        Set<Location> rawLocations = getRawLocations(lookingBlock, lookingFacing, limitBlocks, onlyAxis, blockStrict);
        Set<Location> outwardLocations = new HashSet<>();
        for (Location location : rawLocations) {
            Location outwardLocation = location.clone().add(lookingFacing.getOppositeFace().getDirection());
            Block outwardBlock = outwardLocation.getBlock();
            Material outwardType = outwardBlock.getType();
            if (outwardType == Material.AIR || outwardType == Material.WATER || outwardType == Material.LAVA) {
                outwardLocations.add(outwardLocation);
            }
        }
        Location lookingLocation = lookingBlock.getLocation();

        World world = lookingLocation.getWorld();
        Map<Location, Double> distances = new HashMap<>();
        for (Location location : outwardLocations) {
            if (world.getWorldBorder().isInside(location)) {
                double distance = location.distance(lookingLocation);
                distances.put(location, distance);
            }
        }

        // sort by shortest distance
        Set<Location> locations = new HashSet<>(distances.keySet());
        List<Location> sortedLocations = locations.stream().sorted(Comparator.comparingDouble(distances::get)).limit(limitBlocks).toList();

        return new HashSet<>(sortedLocations);
    }

    public static @NotNull Set<Location> getRawLocations(@NotNull Block lookingBlock, @NotNull BlockFace lookingFacing, int limitBlocks) {
        return getRawLocations(lookingBlock, lookingFacing, limitBlocks, null);
    }

    public static @NotNull Set<Location> getRawLocations(@NotNull Block lookingBlock, @NotNull BlockFace lookingFacing, int limitBlocks, Axis onlyAxis) {
        return getRawLocations(lookingBlock, lookingFacing, limitBlocks, onlyAxis, true);
    }

    public static @NotNull Set<Location> getRawLocations(@NotNull Block lookingBlock, @NotNull BlockFace lookingFacing, int limitBlocks, Axis onlyAxis, boolean blockStrict) {
        return getRawLocations(lookingBlock, lookingFacing, limitBlocks, onlyAxis, blockStrict, true);
    }

    public static @NotNull Set<Location> getRawLocations(@NotNull Block lookingBlock, @NotNull BlockFace lookingFacing, int limitBlocks, @Nullable Axis onlyAxis, boolean blockStrict, boolean checkOutward) {
        Set<Location> locations = new HashSet<>();
        Queue<Location> queue = new LinkedList<>();
        Location lookingLocation = lookingBlock.getLocation();
        queue.offer(lookingLocation);

        Set<BlockFace> faces = new HashSet<>(validFaces);
        faces.remove(lookingFacing);
        faces.remove(lookingFacing.getOppositeFace());
        if (onlyAxis != null) {
            switch (onlyAxis) {
                case X -> {
                    faces.remove(BlockFace.NORTH);
                    faces.remove(BlockFace.SOUTH);
                    faces.remove(BlockFace.UP);
                    faces.remove(BlockFace.DOWN);
                }

                case Y -> {
                    faces.remove(BlockFace.NORTH);
                    faces.remove(BlockFace.SOUTH);
                    faces.remove(BlockFace.EAST);
                    faces.remove(BlockFace.WEST);
                }

                case Z -> {
                    faces.remove(BlockFace.EAST);
                    faces.remove(BlockFace.WEST);
                    faces.remove(BlockFace.UP);
                    faces.remove(BlockFace.DOWN);
                }
            }
        }

        if (faces.isEmpty()) {
            return locations;
        }

        while (!queue.isEmpty() && limitBlocks > 0) {
            Block currentBlock = queue.poll().getBlock();
            Material type = currentBlock.getType();
            if (type.isAir()) {
                continue;
            }

            Location queuedLocation = currentBlock.getLocation();
            if (!locations.contains(queuedLocation)) {
                locations.add(queuedLocation);

                for (BlockFace face : faces) {
                    Block block = currentBlock.getRelative(face);
                    if (!blockStrict || block.getType() == type) {
                        Location location = block.getLocation();
                        if (!locations.contains(location)) {
                            if (checkOutward) {
                                Block outwardBlock = block.getRelative(lookingFacing.getOppositeFace());
                                Material outwardType = outwardBlock.getType();
                                if (outwardType != Material.AIR && outwardType != Material.WATER && outwardType != Material.LAVA) {
                                    continue;
                                }
                            }

                            Location blockLocation = block.getLocation();
                            if (manhattanDistance(lookingLocation, blockLocation) < limitBlocks) {
                                queue.offer(blockLocation);
                            }
                        }
                    }
                }
            }
        }

        return locations;
    }

    public static int manhattanDistance(@NotNull Location a, @NotNull Location b) {
        int dx = Math.abs(a.getBlockX() - b.getBlockX());
        int dy = Math.abs(a.getBlockY() - b.getBlockY());
        int dz = Math.abs(a.getBlockZ() - b.getBlockZ());
        return dx + dy + dz;
    }
}
