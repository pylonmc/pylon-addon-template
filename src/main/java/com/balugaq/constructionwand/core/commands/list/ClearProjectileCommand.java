package com.balugaq.constructionwand.core.commands.list;

import com.balugaq.constructionwand.core.commands.SubCommand;
import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ClearProjectileCommand extends SubCommand {
    public static final String IDENTIFIER = "clearProjectile";

    public ClearProjectileCommand(ConstructionWandPlugin plugin) {
        super(plugin);
    }

    @Override
    public @NotNull String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ConstructionWandPlugin plugin = getPlugin();
        Set<UUID> projectiles = new HashSet<>(plugin.getDisplayManager().getDisplays().keySet());
        for (UUID uuid : projectiles) {
            plugin.getDisplayManager().killDisplays(uuid);
        }

        if (sender instanceof Player player) {
            player.getWorld().getEntities().forEach(entity -> {
                if (entity instanceof Display display) {
                    List<MetadataValue> metadata = display.getMetadata(ConstructionWandPlugin.getInstance().getName());
                    if (!metadata.isEmpty()) {
                        if (metadata.getFirst().asBoolean()) {
                            display.remove();
                        }
                    }
                }
            });
        }

        sender.sendMessage("All projectiles have been cleared.");

        return true;
    }
}
