package io.github.pylonmc.myaddon;

import io.github.pylonmc.pylon.core.addon.PylonAddon;
import lombok.Getter;
import org.bukkit.Material;
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

    @Override
    public @NotNull JavaPlugin getJavaPlugin() {
        return instance;
    }

    @Override
    public @NotNull Set<@NotNull Locale> getLanguages() {
        return Set.of(Locale.ENGLISH);
    }

    @Override
    public @NotNull Material getMaterial() {
        return Material.DEAD_BUSH;
    }
}
