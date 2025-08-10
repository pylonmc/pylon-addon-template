package com.balugaq.constructionwand.utils;

import com.balugaq.constructionwand.api.enums.Interaction;
import com.destroystokyo.paper.MaterialTags;
import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
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
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
public class WandUtil {
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

    public static void placeBlocks(@NotNull Plugin plugin, @NotNull PlayerInteractEvent event, boolean disabled, int limitBlocks, boolean blockStrict, boolean opOnly) {
        placeBlocks(plugin, null, event.getPlayer(), disabled, limitBlocks, blockStrict, opOnly);
    }

    public static void placeBlocks(@NotNull Plugin plugin, @Nullable EquipmentSlot hand, @NotNull Player player, boolean disabled, int limitBlocks, boolean blockStrict, boolean opOnly) {
        if (hand != EquipmentSlot.HAND) {
            return;
        }

        if (opOnly && !player.isOp()) {
            return;
        }

        if (player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        if (disabled) {
            return;
        }

        Block lookingAtBlock = player.getTargetBlockExact(6, FluidCollisionMode.NEVER);
        if (lookingAtBlock == null || lookingAtBlock.getType() == Material.AIR) {
            return;
        }

        Material material = lookingAtBlock.getType();
        if (isMaterialDisabledToBuild(material)) {
            return;
        }

        int playerHas = 0;
        if (player.getGameMode() == GameMode.CREATIVE) {
            playerHas = 4096;
        } else {
            ItemStack target = new ItemStack(material, 1);
            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    continue;
                }

                if (itemStack.isSimilar(target)) {
                    int count = itemStack.getAmount();
                    playerHas += count;
                }

                if (playerHas >= limitBlocks) {
                    break;
                }
            }
        }

        if (playerHas <= 0) {
            return;
        }

        BlockFace originalFacing = player.getTargetBlockFace(6, FluidCollisionMode.NEVER);
        if (originalFacing == null) {
            return;
        }

        BlockFace lookingFacing = getBlockFaceAsCartesian(originalFacing);

        ItemStack itemInHand = new ItemStack(material, 1);
        ItemStack item = player.getInventory().getItemInMainHand();
        Set<Location> buildingLocations = WandUtil.getBuildingLocations(player, Math.min(limitBlocks, playerHas), WandUtil.getAxis(item), blockStrict);

        int consumed = 0;

        Set<Block> blocks = new HashSet<>();
        for (Location location : buildingLocations) {
            Block block = location.getBlock();
            if (block.getType() == Material.AIR || block.getType() == Material.WATER || block.getType() == Material.LAVA) {
                BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent(
                        block,
                        block.getState(),
                        block.getRelative(lookingFacing.getOppositeFace()),
                        itemInHand,
                        player,
                        PermissionUtil.hasPermission(player, location, Interaction.PLACE_BLOCK),
                        EquipmentSlot.HAND
                );
                Bukkit.getPluginManager().callEvent(blockPlaceEvent);
                if (!blockPlaceEvent.isCancelled()) {
                    blocks.add(block);
                    consumed += 1;
                }
            }
        }

        // I don't know why, but it must be run later, or it will create PlayerInteractEvent AGAIN!
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Block block : blocks) {
                if (block == null) {
                    continue;
                }

                if (isMaterialStateCopyableToBuild(material)) {
                    WorldUtils.copyBlockState(lookingAtBlock.getState(), block);
                } else {
                    block.setType(material);
                }
                block.getState().update(true, true);
            }
        }, 1);

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (consumed > 0) {
            player.getInventory().removeItem(new ItemStack(material, consumed));
        }
    }

    public static void breakBlocks(@NotNull Plugin plugin, @NotNull PlayerInteractEvent event, boolean disabled, int limitBlocks, boolean blockStrict, boolean opOnly) {
        breakBlocks(plugin, event.getHand(), event.getPlayer(), disabled, limitBlocks, blockStrict, opOnly);
    }

    public static void breakBlocks(@NotNull Plugin plugin, @Nullable EquipmentSlot hand, @NotNull Player player, boolean disabled, int limitBlocks, boolean blockStrict, boolean opOnly) {
        if (hand != EquipmentSlot.HAND) {
            return;
        }

        if (opOnly && !player.isOp()) {
            return;
        }

        if (player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        if (disabled) {
            return;
        }

        Block lookingAtBlock = player.getTargetBlockExact(6, FluidCollisionMode.NEVER);
        if (lookingAtBlock == null || lookingAtBlock.getType() == Material.AIR) {
            return;
        }

        Material material = lookingAtBlock.getType();
        if (isMaterialDisabledToBreak(material)) {
            return;
        }

        BlockFace originalFacing = player.getTargetBlockFace(6, FluidCollisionMode.NEVER);
        if (originalFacing == null) {
            return;
        }

        Location lookingLocation = lookingAtBlock.getLocation();
        BlockFace lookingFacing = getBlockFaceAsCartesian(originalFacing);
        ItemStack item = player.getInventory().getItemInMainHand();

        Set<Location> rawLocations = WandUtil.getRawLocations(lookingAtBlock, lookingFacing, limitBlocks, getAxis(item), blockStrict, true);

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
        List<Location> sortedLocations = locations.stream().sorted(Comparator.comparingDouble(distances::get)).toList();
        Set<Location> result = new HashSet<>();
        AtomicInteger count = new AtomicInteger(0);
        sortedLocations.forEach(location -> {
            if (count.incrementAndGet() > limitBlocks) {
                return;
            }
            result.add(location);
        });

        Set<BlockBreakEvent> locationsToBreak = new HashSet<>();
        for (Location location : result) {
            if (!PermissionUtil.hasPermission(player, location, Interaction.BREAK_BLOCK)) {
                continue;
            }

            BlockBreakEvent e2 = new BlockBreakEvent(location.getBlock(), player);
            Bukkit.getPluginManager().callEvent(e2);
            if (!e2.isCancelled()) {
                locationsToBreak.add(e2);
            }
        }

        // I don't know why, but it must be run later, or it will create PlayerInteractEvent AGAIN!
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (BlockBreakEvent e2 : locationsToBreak) {
                if (e2.isCancelled()) {
                    continue;
                }

                Block block = e2.getBlock();
                if (block == null) {
                    continue;
                }

                if (e2.isDropItems()) {
                    block.breakNaturally();
                } else {
                    block.setType(Material.AIR);
                }

                BlockState state = block.getState();
                state.update(true, true);
            }
        }, 1);
    }

    public static boolean isMaterialDisabledToBreak(@NotNull Material material) {
        if (
                material.isAir()
                        || !material.isBlock()

                        || material == Material.END_PORTAL_FRAME
                        || material == Material.BEDROCK
                        || material == Material.COMMAND_BLOCK
                        || material == Material.CHAIN_COMMAND_BLOCK
                        || material == Material.REPEATING_COMMAND_BLOCK
                        || material == Material.STRUCTURE_VOID
                        || material == Material.STRUCTURE_BLOCK
                        || material == Material.JIGSAW
                        || material == Material.BARRIER
                        || material == Material.LIGHT
                        || material == Material.SPAWNER
                        || material == Material.TRIAL_SPAWNER
                        || material.getHardness() < 0
        ) {
            return true;
        }

        return false;
    }

    @Nullable
    public static Axis getAxis(@NotNull ItemStack item) {
        String axis = PersistentUtil.getOrDefault(item, PersistentDataType.STRING, KeyUtil.AXIS, null);
        if (axis == null) {
            return null;
        }

        try {
            return Axis.valueOf(axis);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    public static void setAxis(@NotNull ItemStack item, @Nullable Axis axis) {
        if (axis == null) {
            PersistentUtil.set(item, PersistentDataType.STRING, KeyUtil.AXIS, "null");
        } else {
            PersistentUtil.set(item, PersistentDataType.STRING, KeyUtil.AXIS, axis.name());
        }
    }

    @NotNull
    private static BlockFace getBlockFaceAsCartesian(@NotNull BlockFace originalFacing) {
        // Seems here's a bug, but it works fine...
        BlockFace lookingFacing = originalFacing.getOppositeFace();
        if (!originalFacing.isCartesian()) {
            switch (originalFacing) {
                case NORTH_EAST, NORTH_WEST, NORTH_NORTH_EAST, NORTH_NORTH_WEST -> lookingFacing = BlockFace.NORTH;
                case SOUTH_EAST, SOUTH_WEST, SOUTH_SOUTH_EAST, SOUTH_SOUTH_WEST -> lookingFacing = BlockFace.SOUTH;
                case EAST_NORTH_EAST, EAST_SOUTH_EAST -> lookingFacing = BlockFace.EAST;
                case WEST_NORTH_WEST, WEST_SOUTH_WEST -> lookingFacing = BlockFace.WEST;
                default -> {
                }
            }
        }
        return lookingFacing;
    }

    public static boolean isMaterialStateCopyableToBuild(@NotNull Material material) {
        return // Items that be allowed to copy state
                MaterialTags.FENCE_GATES.isTagged(material)
                        || material.name().endsWith("_SLAB")
                        || material.name().endsWith("_STAIRS")
                        || material.name().endsWith("_TRAPDOOR")
                        || material.name().endsWith("_HEAD")
                        || material.name().endsWith("_LOG")
                        || material == Material.END_ROD
                        || material.name().endsWith("LIGHTNING_ROD")
                        || material.name().endsWith("CHAIN")
                        || material.name().endsWith("_BARS")
                        || material == Material.DAYLIGHT_DETECTOR
                        || material == Material.ENDER_CHEST
                        || material == Material.NOTE_BLOCK
                        || material == Material.REDSTONE_ORE
                        || material == Material.DEEPSLATE_REDSTONE_ORE
                        || material.name().endsWith("_WALL");
    }

    public static boolean isMaterialDisabledToBuild(@NotNull Material material) {
        if (// Items that can store items
                MaterialTags.SHULKER_BOXES.isTagged(material)
                        || (material.name().endsWith("CHEST") && material != Material.ENDER_CHEST)
                        || material == Material.BARREL
                        || material == Material.LECTERN
                        || material == Material.DISPENSER
                        || material == Material.DROPPER
                        || material == Material.HOPPER
                        || material == Material.VAULT
                        || material.name().endsWith("_SHELF")
                        || material == Material.SUSPICIOUS_SAND
                        || material == Material.SUSPICIOUS_GRAVEL

                        // Items that will take two blocks
                        || MaterialTags.BEDS.isTagged(material)
                        || MaterialTags.DOORS.isTagged(material)
                        || material == Material.TALL_GRASS
                        || material == Material.LARGE_FERN
                        || material == Material.TALL_SEAGRASS
                        || material == Material.SUNFLOWER
                        || material == Material.LILAC
                        || material == Material.ROSE_BUSH
                        || material == Material.PEONY
                        || material == Material.PITCHER_PLANT
                        || material == Material.PISTON_HEAD
                        || material == Material.PISTON
                        || material == Material.STICKY_PISTON

                        // Items that can place much same block in a location
                        || material.name().endsWith("CANDLE")
                        || material == Material.SEA_PICKLE
                        || material == Material.TURTLE_EGG
                        || material == Material.FROGSPAWN

                        // Items that can't be placed in a location
                        || material.isAir()
                        || !material.isBlock()

                        // Items that is invalid / no permission
                        || material == Material.END_PORTAL_FRAME
                        || material == Material.BEDROCK
                        || material == Material.COMMAND_BLOCK
                        || material == Material.CHAIN_COMMAND_BLOCK
                        || material == Material.REPEATING_COMMAND_BLOCK
                        || material == Material.STRUCTURE_VOID
                        || material == Material.STRUCTURE_BLOCK
                        || material == Material.JIGSAW
                        || material == Material.BARRIER
                        || material == Material.LIGHT
                        || material == Material.SPAWNER
                        || material == Material.TRIAL_SPAWNER
                        || material == Material.CHORUS_FLOWER
                        || material == Material.NETHER_WART
                        || material == Material.CAVE_VINES
                        || material == Material.CAVE_VINES_PLANT
                        || material == Material.FROSTED_ICE
                        || material == Material.WATER_CAULDRON
                        || material == Material.LAVA_CAULDRON
                        || material == Material.POWDER_SNOW_CAULDRON
                        || material.name().startsWith("POTTED_")
                        || material == Material.FIRE
                        || material == Material.SOUL_FIRE
                        || material == Material.END_PORTAL
                        || material == Material.END_GATEWAY
                        || material == Material.NETHER_PORTAL
                        || material == Material.BUBBLE_COLUMN
                        || material == Material.POWDER_SNOW
                        || material == Material.MUSHROOM_STEM

                        // Items that has inventory
                        || material == Material.CRAFTING_TABLE
                        || material == Material.STONECUTTER
                        || material == Material.CARTOGRAPHY_TABLE
                        || material == Material.SMITHING_TABLE
                        || material == Material.GRINDSTONE
                        || material == Material.LOOM
                        || material == Material.FURNACE
                        || material == Material.SMOKER
                        || material == Material.BLAST_FURNACE
                        || material == Material.CAMPFIRE
                        || material == Material.SOUL_CAMPFIRE
                        || material == Material.ANVIL
                        || material == Material.CHIPPED_ANVIL
                        || material == Material.DAMAGED_ANVIL
                        || material == Material.COMPOSTER
                        || material == Material.JUKEBOX
                        || material == Material.ENCHANTING_TABLE
                        || material == Material.BREWING_STAND
                        || material == Material.CAULDRON
                        || material == Material.BEACON
                        || material == Material.BEE_NEST
                        || material == Material.BEEHIVE
                        || material == Material.FLOWER_POT
                        || material == Material.DECORATED_POT
                        || material == Material.CHISELED_BOOKSHELF
                        || MaterialTags.SIGNS.isTagged(material)
                        || material == Material.CRAFTER

                        // Items that have different types
                        || material == Material.PLAYER_HEAD
                        || material == Material.PLAYER_WALL_HEAD
                        || material.name().endsWith("CAKE")
                        | material.name().endsWith("_BUTTON")
                        || material == Material.TRIPWIRE
                        || material == Material.CREAKING_HEART

                        // Needs side block
                        || material == Material.POINTED_DRIPSTONE
                        || material.name().endsWith("_BANNER")
                        || material == Material.LEVER
                        || material.name().endsWith("TORCH")
                        || material.name().endsWith("LANTERN")
                        || material == Material.LADDER
                        || material == Material.REPEATER
                        || material == Material.COMPARATOR
                        || material == Material.VINE
                        || material == Material.GLOW_LICHEN
                        || material == Material.SCULK_VEIN
                        || material == Material.BELL
                        || material == Material.TRIPWIRE_HOOK
                        || material.name().endsWith("_RAIL")
                        || material.name().endsWith("_CORAL")
                        || material.name().endsWith("_CORAL_FAN")
                        || material.name().endsWith("_CARPET")
                        || material == Material.HANGING_ROOTS
                        || material == Material.REDSTONE_WIRE
                        || material == Material.BIG_DRIPLEAF_STEM
                        || material == Material.CHORUS_PLANT
                        || material == Material.DRAGON_EGG
                        || material == Material.SNOW
                        || material.name().endsWith("_PRESSURE_PLATE")
                        || material == Material.SMALL_AMETHYST_BUD
                        || material == Material.MEDIUM_AMETHYST_BUD
                        || material == Material.LARGE_AMETHYST_BUD
                        || material == Material.AMETHYST_CLUSTER
                        || material.name().endsWith("_SAPLING")
                        || material == Material.AZALEA
                        || material == Material.FLOWERING_AZALEA
                        || material == Material.BROWN_MUSHROOM
                        || material == Material.RED_MUSHROOM
                        || material == Material.CRIMSON_FUNGUS
                        || material == Material.WARPED_FUNGUS
                        || material == Material.SHORT_GRASS
                        || material == Material.FERN
                        || material == Material.DEAD_BUSH
                        || material == Material.DANDELION
                        || material == Material.POPPY
                        || material == Material.BLUE_ORCHID
                        || material == Material.ALLIUM
                        || material == Material.AZURE_BLUET
                        || material == Material.RED_TULIP
                        || material == Material.ORANGE_TULIP
                        || material == Material.WHITE_TULIP
                        || material == Material.PINK_TULIP
                        || material == Material.OXEYE_DAISY
                        || material == Material.CORNFLOWER
                        || material == Material.LILY_OF_THE_VALLEY
                        || material == Material.TORCHFLOWER
                        || material == Material.WITHER_ROSE
                        || material == Material.PINK_PETALS
                        || material == Material.SPORE_BLOSSOM
                        || material == Material.BAMBOO
                        || material == Material.SUGAR_CANE
                        || material == Material.CACTUS
                        || material == Material.CRIMSON_ROOTS
                        || material == Material.WARPED_ROOTS
                        || material == Material.NETHER_SPROUTS
                        || material == Material.WEEPING_VINES
                        || material == Material.TWISTING_VINES
                        || material == Material.WEEPING_VINES_PLANT
                        || material == Material.TWISTING_VINES_PLANT
                        || material == Material.COCOA
                        || material == Material.SWEET_BERRY_BUSH
                        || material == Material.TORCHFLOWER_CROP
                        || material == Material.WHEAT
                        || material == Material.MELON_STEM
                        || material == Material.PUMPKIN_STEM
                        || material == Material.POTATOES
                        || material == Material.CARROTS
                        || material == Material.BEETROOTS
                        || material == Material.KELP
                        || material == Material.KELP_PLANT
                        || material == Material.SEAGRASS
                        || material == Material.LILY_PAD
                        || material == Material.OPEN_EYEBLOSSOM
                        || material == Material.CLOSED_EYEBLOSSOM
                        || material == Material.PALE_HANGING_MOSS
                        || material == Material.MANGROVE_PROPAGULE
                        || material == materialValueOf("WILDFLOWERS")
                        || material == materialValueOf("LEAF_LITTER")
                        || material.name().endsWith("_WALL_FAN")
                        || material == Material.RESIN_CLUMP
        ) {
            return true;
        }

        return false;
    }

    @NotNull
    private static Material materialValueOf(@NotNull String name) {
        try {
            return Material.valueOf(name);
        } catch (IllegalArgumentException | NullPointerException e) {
            return Material.AIR;
        }
    }
}
