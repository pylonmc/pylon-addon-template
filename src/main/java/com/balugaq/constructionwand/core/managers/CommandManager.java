package com.balugaq.constructionwand.core.managers;

import com.balugaq.constructionwand.api.interfaces.IManager;
import com.balugaq.constructionwand.core.commands.SubCommand;
import com.balugaq.constructionwand.core.commands.list.ClearProjectileCommand;
import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CommandManager implements TabExecutor, IManager {
    private final ConstructionWandPlugin plugin;
    private final List<SubCommand> subCommands = new ArrayList<>();

    public CommandManager(@NotNull ConstructionWandPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setup() {
        subCommands.add(new ClearProjectileCommand(plugin));
        PluginCommand pluginCommand = plugin.getCommand("constructionwand");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(this);
        }
    }

    @Override
    public void shutdown() {
        PluginCommand pluginCommand = plugin.getCommand("constructionwand");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(null);
        }

        subCommands.clear();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.isOp()) {
            sender.sendMessage("You don't have permission to use this command.");
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage("Usage: /constructionwand <subcommand>");
            return false;
        }

        for (SubCommand subCommand : subCommands) {
            if (subCommand.getIdentifier().equalsIgnoreCase(args[0])) {
                if (subCommand.onCommand(sender, command, label, args)) {
                    return true;
                }
            }
        }

        sender.sendMessage("Subcommand not found.");
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.isOp()) {
            return null;
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 0) {
            return completions;
        } else if (args.length == 1) {
            for (SubCommand subCommand : subCommands) {
                completions.add(subCommand.getIdentifier());
            }
        } else {
            for (SubCommand subCommand : subCommands) {
                if (subCommand.getIdentifier().equalsIgnoreCase(args[0])) {
                    completions.addAll(subCommand.onTabComplete(sender, command, label, args));
                }
            }
        }

        return completions;
    }
}
