package com.balugaq.constructionwand.core.managers;

import com.balugaq.constructionwand.api.events.PrepareBreakingEvent;
import com.balugaq.constructionwand.api.events.PrepareBuildingEvent;
import com.balugaq.constructionwand.api.interfaces.IManager;
import com.balugaq.constructionwand.api.items.BreakingWand;
import com.balugaq.constructionwand.api.items.BuildingWand;
import com.balugaq.constructionwand.utils.WandUtil;
import dev.sefiraat.sefilib.entity.display.DisplayGroup;
import io.github.pylonmc.pylon.core.item.PylonItem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@Getter
public class DisplayManager implements IManager {
    private final Map<UUID, BlockFace> lookingFaces = new HashMap<>();
    private final Map<UUID, Location> lookingAt = new HashMap<>();
    private final Map<UUID, DisplayGroup> displays = new HashMap<>();
    private final JavaPlugin plugin;
    private boolean running = true;

    public DisplayManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setup() {
        startShowBlockTask();
    }

    @Override
    public void shutdown() {
        stopShowBlockTask();
    }

    public void stopShowBlockTask() {
        running = false;
        for (UUID uuid : new HashSet<>(displays.keySet())) {
            killDisplays(uuid);
        }
    }

    public void killDisplays(UUID uuid) {
        DisplayGroup group = displays.get(uuid);
        if (group != null) {
            group.remove();
        }
        displays.remove(uuid);
        lookingAt.remove(uuid);
        lookingFaces.remove(uuid);
    }

    public void startShowBlockTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!running) {
                return;
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getGameMode() == GameMode.SPECTATOR) {
                    return;
                }
                UUID uuid = player.getUniqueId();
                Block block = player.getTargetBlockExact(6, FluidCollisionMode.NEVER);
                if (block == null || block.getType().isAir()) {
                    lookingAt.remove(uuid);
                    killDisplays(uuid);
                    continue;
                }

                BlockFace originalFacing = player.getTargetBlockFace(6, FluidCollisionMode.NEVER);
                if (originalFacing == null) {
                    lookingAt.remove(uuid);
                    lookingFaces.remove(uuid);
                    killDisplays(uuid);
                    continue;
                }

                Location location = block.getLocation();
                if (!lookingAt.containsKey(uuid) || !lookingAt.get(uuid).equals(location) || !lookingFaces.containsKey(uuid) || !lookingFaces.get(uuid).equals(originalFacing)) {
                    killDisplays(uuid);
                    lookingAt.put(uuid, location);
                    lookingFaces.put(uuid, originalFacing);

                    PylonItem wandLike = PylonItem.fromStack(player.getInventory().getItemInMainHand());
                    if (wandLike instanceof BuildingWand buildingWand) {
                        if (buildingWand.isDisabled()) {
                            continue;
                        }

                        if (WandUtil.isMaterialDisabledToBuild(block.getType())) {
                            continue;
                        }

                        PrepareBuildingEvent event = new PrepareBuildingEvent(player, buildingWand, block);
                        Bukkit.getPluginManager().callEvent(event);
                    }

                    if (wandLike instanceof BreakingWand breakingWand) {
                        if (breakingWand.isDisabled()) {
                            continue;
                        }

                        if (WandUtil.isMaterialDisabledToBreak(block.getType())) {
                            continue;
                        }

                        PrepareBreakingEvent event = new PrepareBreakingEvent(player, breakingWand, block);
                        Bukkit.getPluginManager().callEvent(event);
                    }
                }
            }

        }, 2, 1);
    }

    public void registerDisplayGroup(UUID uuid, DisplayGroup group) {
        displays.put(uuid, group);
    }
}
