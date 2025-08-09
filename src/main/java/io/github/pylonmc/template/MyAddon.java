package io.github.pylonmc.template;

import io.github.pylonmc.pylon.base.BasePages;
import io.github.pylonmc.pylon.core.addon.PylonAddon;
import io.github.pylonmc.pylon.core.content.guide.PylonGuide;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Unbreakable;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Set;


public class MyAddon extends JavaPlugin implements PylonAddon {

    // The `instance` field stores the instance of the addon (there's only ever one)
    @Getter private static MyAddon instance;

    // Called when our plugin is enabled
    @Override
    public void onEnable() {
        instance = this;

        // Every Pylon addon must call this BEFORE doing anything Pylon-related
        registerWithPylon();

        NamespacedKey epicSwordKey = new NamespacedKey(this, "epic_sword");
        ItemStack epicSword = ItemStackBuilder.pylonItem(Material.DIAMOND_SWORD, epicSwordKey)
                .set(DataComponentTypes.UNBREAKABLE, Unbreakable.unbreakable())
                .build();
        PylonItem.register(PylonItem.class, epicSword);

        BasePages.COMBAT.addItem(epicSwordKey);
    }

    // Required by Pylon
    @Override
    public @NotNull JavaPlugin getJavaPlugin() {
        return instance;
    }

    // Required by Pylon
    @Override
    public @NotNull String getDisplayName() {
        return "My Addon";
    }

    // Required by Pylon - returns all the languages the addon supports
    @Override
    public @NotNull Set<@NotNull Locale> getLanguages() {
        return Set.of(Locale.ENGLISH);
    }

    @Override
    public @NotNull Material getMaterial() {
        return null;
    }
}
