package com.balugaq.constructionwand.implementation.items;

import com.balugaq.constructionwand.api.items.BreakingStaff;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BreakingStaff4096 extends BreakingStaff {
    public BreakingStaff4096(@NotNull ItemGroup itemGroup, @NotNull SlimefunItemStack item, @NotNull RecipeType recipeType, ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe, 4096, true, true);
    }
}
