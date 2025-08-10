package com.balugaq.constructionwand.core.managers;

import com.balugaq.constructionwand.api.interfaces.IManager;
import com.balugaq.constructionwand.api.items.BreakingWand;
import com.balugaq.constructionwand.api.items.BuildingWand;
import com.balugaq.constructionwand.utils.KeyUtil;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.pylonmc.pylon.core.content.guide.PylonGuide;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WandSetup implements IManager {
    public static SimpleStaticGuidePage MAIN;
    
    @Override
    public void setup() {
        MAIN = new SimpleStaticGuidePage(key("construction-wand"), Material.BLAZE_ROD);
        PylonGuide.getRootPage().addPage(MAIN);

        registerBuildingWand(
                key("building-wand-9"),
                9,
                false,
                false,
                Material.STONE_SWORD,
                "  I",
                " S ",
                "S  "
        );
        registerBuildingWand(
                key("building-wand-64"),
                64,
                false,
                false,
                Material.IRON_SWORD,
                "  G",
                " S ",
                "S  "
        );
        registerBuildingWand(
                key("building-wand-4096"),
                4096,
                false,
                true,
                Material.DIAMOND_SWORD,
                (String[]) null
        );

        registerBuildingWand(
                key("building-wand-block-strict-9"),
                9,
                true,
                false,
                Material.STONE_SWORD,
                "I",
                " S ",
                "  S"
        );

        registerBuildingWand(
                key("building-wand-block-strict-64"),
                64,
                true,
                false,
                Material.IRON_SWORD,
                "G",
                " S ",
                "  S"
        );

        registerBuildingWand(
                key("building-wand-block-strict-4096"),
                4096,
                true,
                true,
                Material.DIAMOND_SWORD,
                (String[]) null
        );

        registerBreakingWand(
                key("breaking-wand-9"),
                9,
                false,
                false,
                Material.GOLDEN_SWORD,
                "OOI",
                "OSO",
                "SOO"
        );

        registerBreakingWand(
                key("breaking-wand-64"),
                64,
                false,
                false,
                Material.GOLDEN_SWORD,
                "OOG",
                "OSO",
                "SOO"
        );

        registerBreakingWand(
                key("breaking-wand-4096"),
                4096,
                false,
                true,
                Material.GOLDEN_SWORD,
                (String[]) null
        );
    }

    @NotNull
    public static NamespacedKey key(@NotNull String key) {
        return KeyUtil.newKey(key);
    }

    @Override
    public void shutdown() {
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
                .shape(recipe)
                .setIngredient('S', Material.STICK)
                .setIngredient('I', Material.IRON_INGOT)
                .setIngredient('G', Material.GOLD_INGOT)
                .setIngredient('O', Material.OBSIDIAN);
        shapedRecipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(shapedRecipe);
    }
}
