package com.balugaq.constructionwand.core.managers;

import com.balugaq.constructionwand.api.interfaces.IManager;
import com.balugaq.constructionwand.api.items.BreakingStaff;
import com.balugaq.constructionwand.api.items.BuildingStaff;
import com.balugaq.constructionwand.api.objects.events.PrepareBreakingEvent;
import com.balugaq.constructionwand.api.objects.events.PrepareBuildingEvent;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.metamechanists.displaymodellib.sefilib.entity.display.DisplayGroup;

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

                    SlimefunItem staffLike = SlimefunItem.getByItem(player.getInventory().getItemInMainHand());
                    if (staffLike instanceof BuildingStaff buildingStaff) {
                        if (buildingStaff.isDisabledIn(block.getWorld())) {
                            continue;
                        }

                        if (buildingStaff.isDisabledMaterial(block.getType())) {
                            continue;
                        }

                        PrepareBuildingEvent event = new PrepareBuildingEvent(player, buildingStaff, block);
                        Bukkit.getPluginManager().callEvent(event);
                    }

                    if (staffLike instanceof BreakingStaff breakingStaff) {
                        if (breakingStaff.isDisabledIn(block.getWorld())) {
                            continue;
                        }

                        if (breakingStaff.isDisabledMaterial(block.getType())) {
                            continue;
                        }

                        PrepareBreakingEvent event = new PrepareBreakingEvent(player, breakingStaff, block);
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
