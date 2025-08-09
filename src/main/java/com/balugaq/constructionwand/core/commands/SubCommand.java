package com.balugaq.constructionwand.core.commands;

import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public abstract class SubCommand implements TabExecutor {
    private final ConstructionWandPlugin plugin;

    public SubCommand(ConstructionWandPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract String getIdentifier();

    public abstract @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args);

    public abstract boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args);
}
