package com.balugaq.constructionwand.core.managers;

import com.balugaq.constructionwand.api.interfaces.IManager;
import com.balugaq.constructionwand.api.items.BreakingWand;
import com.balugaq.constructionwand.api.items.BuildingWand;
import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import com.balugaq.constructionwand.utils.Keys;
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

public class WandSetup implements IManager {
    public static SimpleStaticGuidePage MAIN;
    private final ConstructionWandPlugin plugin;

    public WandSetup(@NotNull ConstructionWandPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void setup() {
        MAIN = new SimpleStaticGuidePage(Keys.MAIN_GROUP, Material.BLAZE_ROD);
        PylonGuide.getRootPage().addPage(MAIN);

        
    }

    @Override
    public void shutdown() {
    }

    @NotNull
    public static BuildingWand registerBuildingWand(
            @NotNull NamespacedKey key,
            int limitBlocks,
            boolean blockStrict,
            boolean opOnly,
            @NotNull Material material,
            @NotNull String @NotNull ... recipe) {
        ItemStack item = ItemStackBuilder.pylonItem(material, key).build();
        registerRecipe(key, item, recipe);
        return new BuildingWand(
                item,
                limitBlocks,
                blockStrict,
                opOnly
        );
    }

    @NotNull
    public static BreakingWand registerBreakingWand(
            @NotNull NamespacedKey key,
            int limitBlocks,
            boolean blockStrict,
            boolean opOnly,
            @NotNull Material material,
            @NotNull String @NotNull ... recipe) {
        ItemStack item = ItemStackBuilder.pylonItem(material, key).build();
        registerRecipe(key, item, recipe);
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
            @NotNull String @NotNull ... recipe) {
        ShapedRecipe shapedRecipe = new ShapedRecipe(key, item)
                .shape(recipe)
                .setIngredient('S', Material.STICK)
                .setIngredient('I', Material.IRON_INGOT);
        shapedRecipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(shapedRecipe);
    }
}
