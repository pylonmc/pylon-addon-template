package com.balugaq.constructionwand.implementation;

import io.github.pylonmc.pylon.core.config.Config;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WandConfig {
    private static final Config config = new Config(ConstructionWandPlugin.getInstance(), "config.yml");

    public static final boolean AUTO_UPDATE = config.get("auto-update", Boolean.class, false);
    public static final boolean DISPLAY_PROJECTION = config.getOrThrow("display-projection", Boolean.class);
    public static final boolean DEBUG = config.getOrThrow("debug", Boolean.class);
    public static final int LIMIT_BLOCKS_BUILDING_WAND_1 = config.getOrThrow("limit-blocks.building-wand-1", Integer.class);
    public static final int LIMIT_BLOCKS_BUILDING_WAND_2 = config.getOrThrow("limit-blocks.building-wand-2", Integer.class);
    public static final int LIMIT_BLOCKS_BUILDING_WAND_3 = config.getOrThrow("limit-blocks.building-wand-3", Integer.class);
    public static final int LIMIT_BLOCKS_BUILDING_WAND_BLOCK_STRICT_1 = config.getOrThrow("limit-blocks.building-wand-block-strict-1", Integer.class);
    public static final int LIMIT_BLOCKS_BUILDING_WAND_BLOCK_STRICT_2 = config.getOrThrow("limit-blocks.building-wand-block-strict-2", Integer.class);
    public static final int LIMIT_BLOCKS_BUILDING_WAND_BLOCK_STRICT_3 = config.getOrThrow("limit-blocks.building-wand-block-strict-3", Integer.class);
    public static final int LIMIT_BLOCKS_BREAKING_WAND_1 = config.getOrThrow("limit-blocks.breaking-wand-1", Integer.class);
    public static final int LIMIT_BLOCKS_BREAKING_WAND_2 = config.getOrThrow("limit-blocks.breaking-wand-2", Integer.class);
    public static final int LIMIT_BLOCKS_BREAKING_WAND_3 = config.getOrThrow("limit-blocks.breaking-wand-3", Integer.class);
}
