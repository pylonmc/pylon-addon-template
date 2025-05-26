package io.github.pylonmc.template;

import io.github.pylonmc.pylon.core.addon.PylonAddon;
import lombok.Getter;
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
}
