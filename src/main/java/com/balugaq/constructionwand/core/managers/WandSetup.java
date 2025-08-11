package com.balugaq.constructionwand.core.managers;

import com.balugaq.constructionwand.api.interfaces.IManager;
import com.balugaq.constructionwand.api.items.BreakingWand;
import com.balugaq.constructionwand.api.items.BuildingWand;
import com.balugaq.constructionwand.implementation.WandConfig;
import com.balugaq.constructionwand.utils.KeyUtil;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.pylonmc.pylon.core.content.guide.PylonGuide;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class WandSetup implements IManager {
    public static SimpleStaticGuidePage MAIN;

    @NotNull
    public static NamespacedKey key(@NotNull String key) {
        return KeyUtil.newKey(key);
    }

    @CanIgnoreReturnValue
    @NotNull
    public static BuildingWand registerBuildingWand(
            @NotNull NamespacedKey key,
            int limitBlocks,
            boolean blockStrict,
            boolean opOnly,
            @NotNull Material material,
            @Nullable String... recipe) {
        ItemStack item = ItemStackBuilder.pylonItem(material, key).build();
        if (recipe != null) {
            registerRecipe(key, item, recipe);
        }
        PylonItem.register(BuildingWand.class, item);
        return new BuildingWand(
                item,
                limitBlocks,
                blockStrict,
                opOnly
        );
    }

    @CanIgnoreReturnValue
    @NotNull
    public static BreakingWand registerBreakingWand(
            @NotNull NamespacedKey key,
            int limitBlocks,
            boolean blockStrict,
            boolean opOnly,
            @NotNull Material material,
            @Nullable String... recipe) {
        ItemStack item = ItemStackBuilder.pylonItem(material, key).build();
        if (recipe != null) {
            registerRecipe(key, item, recipe);
        }
        PylonItem.register(BreakingWand.class, item);
        return new BreakingWand(
                item,
                limitBlocks,
                blockStrict,
                opOnly
        );
    }

    public static void registerRecipe(
            @NotNull NamespacedKey key,
            @NotNull ItemStack item,
            @NotNull String... recipe) {
        ShapedRecipe shapedRecipe = new ShapedRecipe(key, item)
                .shape(recipe);
        Map<Character, ItemStack> m = shapedRecipe.getIngredientMap();
        if (m.containsKey('S')) shapedRecipe.setIngredient('S', Material.STICK);
        if (m.containsKey('I')) shapedRecipe.setIngredient('I', Material.IRON_INGOT);
        if (m.containsKey('G')) shapedRecipe.setIngredient('G', Material.GOLD_INGOT);
        if (m.containsKey('O')) shapedRecipe.setIngredient('O', Material.OBSIDIAN);

        shapedRecipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(shapedRecipe);
    }

    @Override
    public void setup() {
        MAIN = new SimpleStaticGuidePage(key("construction-wand"), Material.BLAZE_ROD);
        PylonGuide.getRootPage().addPage(MAIN);

        registerBuildingWand(
                key("building-wand-1"),
                WandConfig.LIMIT_BLOCKS_BUILDING_WAND_1,
                false,
                false,
                Material.STONE_SWORD,
                "  I",
                " S ",
                "S  "
        );
        registerBuildingWand(
                key("building-wand-2"),
                WandConfig.LIMIT_BLOCKS_BUILDING_WAND_2,
                false,
                false,
                Material.IRON_SWORD,
                "  G",
                " S ",
                "S  "
        );
        registerBuildingWand(
                key("building-wand-3"),
                WandConfig.LIMIT_BLOCKS_BUILDING_WAND_3,
                false,
                true,
                Material.DIAMOND_SWORD,
                (String[]) null
        );

        registerBuildingWand(
                key("building-wand-block-strict-1"),
                WandConfig.LIMIT_BLOCKS_BUILDING_WAND_BLOCK_STRICT_1,
                true,
                false,
                Material.STONE_SWORD,
                "I",
                " S ",
                "  S"
        );

        registerBuildingWand(
                key("building-wand-block-strict-2"),
                WandConfig.LIMIT_BLOCKS_BUILDING_WAND_BLOCK_STRICT_2,
                true,
                false,
                Material.IRON_SWORD,
                "G",
                " S ",
                "  S"
        );

        registerBuildingWand(
                key("building-wand-block-strict-3"),
                WandConfig.LIMIT_BLOCKS_BUILDING_WAND_BLOCK_STRICT_3,
                true,
                true,
                Material.DIAMOND_SWORD,
                (String[]) null
        );

        registerBreakingWand(
                key("breaking-wand-1"),
                WandConfig.LIMIT_BLOCKS_BREAKING_WAND_1,
                false,
                false,
                Material.GOLDEN_SWORD,
                "OOI",
                "OSO",
                "SOO"
        );

        registerBreakingWand(
                key("breaking-wand-2"),
                WandConfig.LIMIT_BLOCKS_BREAKING_WAND_2,
                false,
                false,
                Material.GOLDEN_SWORD,
                "OOG",
                "OSO",
                "SOO"
        );

        registerBreakingWand(
                key("breaking-wand-3"),
                WandConfig.LIMIT_BLOCKS_BREAKING_WAND_3,
                false,
                true,
                Material.GOLDEN_SWORD,
                (String[]) null
        );
    }

    @Override
    public void shutdown() {
    }
}
