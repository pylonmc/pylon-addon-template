package com.balugaq.constructionwand.implementation;

import com.balugaq.constructionwand.core.managers.ConfigManager;
import com.balugaq.constructionwand.core.managers.DisplayManager;
import com.balugaq.constructionwand.core.managers.ListenerManager;
import com.balugaq.constructionwand.core.managers.WandSetup;
import com.balugaq.constructionwand.utils.Debug;
import io.github.pylonmc.pylon.core.addon.PylonAddon;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Set;

@SuppressWarnings({"unused"})
public class ConstructionWandPlugin extends JavaPlugin implements PylonAddon {
    @Getter
    private static ConstructionWandPlugin instance;
    private @Getter ConfigManager configManager;
    private @Getter DisplayManager displayManager;
    private @Getter ListenerManager listenerManager;
    private @Getter WandSetup wandSetup;
    @Getter
    private String username;
    @Getter
    private String repo;
    @Getter
    private String branch;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        registerWithPylon();

        this.username = "balugaq";
        this.repo = "construction-wand";
        this.branch = "master";

        Debug.log("Loading config");
        configManager = new ConfigManager(this);
        configManager.setup();

        Debug.log("Loading display manager");
        displayManager = new DisplayManager(this);
        displayManager.setup();

        Debug.log("Loading listener manager");
        listenerManager = new ListenerManager(this);
        listenerManager.setup();

        Debug.log("Registering wands");
        wandSetup = new WandSetup();
        wandSetup.setup();

        Debug.log("ConstructionWand Done!");
    }

    public void reload() {
        onDisable();
        onEnable();
    }

    @Override
    public void onDisable() {
        wandSetup.shutdown();
        displayManager.shutdown();
        listenerManager.shutdown();
        Debug.log("Disabled BuildingWand!");
    }

    @Override
    @NotNull
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    public String getBugTrackerURL() {
        return MessageFormat.format("https://github.com/{0}/{1}/issues", username, repo);
    }

    @Override
    public @NotNull Set<Locale> getLanguages() {
        return Set.of(
                Locale.ENGLISH,
                Locale.of("enws"),
                Locale.SIMPLIFIED_CHINESE,
                Locale.TRADITIONAL_CHINESE,
                Locale.KOREA,
                Locale.FRANCE,
                Locale.JAPAN,
                Locale.CANADA,
                Locale.CANADA_FRENCH,
                Locale.GERMANY,
                Locale.ITALIAN,
                Locale.of("ru")
        );
    }

    @Override
    public @NotNull Material getMaterial() {
        return Material.BLAZE_ROD;
    }
}