package com.balugaq.constructionwand.core.managers;

import com.balugaq.constructionwand.api.interfaces.IManager;
import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import com.balugaq.constructionwand.utils.Debug;
import io.github.pylonmc.pylon.core.config.Settings;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

@Getter
public class ConfigManager implements IManager {
    public static final Map<NamespacedKey, Map<String, ?>> CACHE = new HashMap<>();
    private final JavaPlugin plugin;
    private boolean AUTO_UPDATE;
    private boolean DISPLAY_PROJECTION;
    private boolean DEBUG;

    public ConfigManager(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        AUTO_UPDATE = plugin.getConfig().getBoolean("auto-update");
        DISPLAY_PROJECTION = plugin.getConfig().getBoolean("display-projection");
        DEBUG = plugin.getConfig().getBoolean("debug");
    }

    public static <T> @NotNull T getSettingOrThrow(NamespacedKey nk, String key, Class<T> clazz) {
        Map<String, ?> cache = CACHE.get(nk);
        if (cache == null) {
            T value = Settings.get(nk).getOrThrow(key, clazz);
            CACHE.put(nk, Map.of(key, value));
            return value;
        } else {
            Object value = cache.get(key);
            if (value == null) {
                value = Settings.get(nk).getOrThrow(key, clazz);
                CACHE.put(nk, Map.of(key, value));
                return clazz.cast(value);
            } else {
                return clazz.cast(value);
            }
        }
    }

    public static boolean autoUpdate() {
        return ConstructionWandPlugin.getInstance().getConfigManager().AUTO_UPDATE;
    }

    public static boolean displayProjection() {
        return ConstructionWandPlugin.getInstance().getConfigManager().DISPLAY_PROJECTION;
    }

    public static boolean debug() {
        return ConstructionWandPlugin.getInstance().getConfigManager().DEBUG;
    }

    @Override
    public void setup() {
        loadConfig();
    }

    @Override
    public void shutdown() {

    }

    public void loadConfig() {
        setupDefaultConfig();
    }

    private void setupDefaultConfig() {
        final InputStream inputStream = plugin.getResource("config.yml");
        final File existingFile = new File(plugin.getDataFolder(), "config.yml");

        if (inputStream == null) {
            return;
        }

        final Reader reader = new InputStreamReader(inputStream);
        final FileConfiguration resourceConfig = YamlConfiguration.loadConfiguration(reader);
        final FileConfiguration existingConfig = YamlConfiguration.loadConfiguration(existingFile);

        for (String key : resourceConfig.getKeys(false)) {
            checkKey(existingConfig, resourceConfig, key);
        }

        try {
            existingConfig.save(existingFile);
        } catch (IOException e) {
            Debug.log(e);
        }
    }

    @ParametersAreNonnullByDefault
    private void checkKey(FileConfiguration existingConfig, FileConfiguration resourceConfig, String key) {
        final Object currentValue = existingConfig.get(key);
        final Object newValue = resourceConfig.get(key);
        if (newValue instanceof ConfigurationSection section) {
            for (String sectionKey : section.getKeys(false)) {
                checkKey(existingConfig, resourceConfig, key + "." + sectionKey);
            }
        } else if (currentValue == null) {
            existingConfig.set(key, newValue);
        }
    }
}
