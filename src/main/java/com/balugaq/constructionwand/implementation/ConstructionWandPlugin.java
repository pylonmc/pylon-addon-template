package com.balugaq.constructionwand.implementation;

import com.balugaq.constructionwand.core.managers.CommandManager;
import com.balugaq.constructionwand.core.managers.ConfigManager;
import com.balugaq.constructionwand.core.managers.DisplayManager;
import com.balugaq.constructionwand.core.managers.ListenerManager;
import com.balugaq.constructionwand.core.managers.StaffSetup;
import com.balugaq.constructionwand.utils.Debug;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;

@SuppressWarnings({"unused"})
public class ConstructionWandPlugin extends JavaPlugin implements PylonAddon {
    private static ConstructionWandPlugin instance;
    private @Getter CommandManager commandManager;
    private @Getter ConfigManager configManager;
    private @Getter DisplayManager displayManager;
    private @Getter ListenerManager listenerManager;
    private @Getter StaffSetup staffSetup;
    private String username;
    private String repo;
    private String branch;

    public static ConstructionWandPlugin getInstance() {
        return instance;
    }

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

        Debug.log("Loading config manager");
        configManager = new ConfigManager(this);
        configManager.setup();

        Debug.log("Loading display manager");
        displayManager = new DisplayManager(this);
        displayManager.setup();

        Debug.log("Loading listener manager");
        listenerManager = new ListenerManager(this);
        listenerManager.setup();

        Debug.log("Loading command manager");
        commandManager = new CommandManager(this);
        commandManager.setup();

        Debug.log("Registering wands");
        staffSetup = new StaffSetup(this);
        staffSetup.setup();

        Debug.log("ConstructionWand Done!");
    }

    public void reload() {
        onDisable();
        onEnable();
    }

    @Override
    public void onDisable() {
        staffSetup.shutdown();
        displayManager.shutdown();
        listenerManager.shutdown();
        commandManager.shutdown();
        configManager.shutdown();
        Debug.log("Disabled BuildingStaff!");
    }

    @Override
    @NotNull
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Override
    public String getBugTrackerURL() {
        return MessageFormat.format("https://github.com/{0}/{1}/issues", username, repo);
    }
}