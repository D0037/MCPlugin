package me.tech.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import me.tech.App;

public class home implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String Label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (App.isOP.containsKey(player.getUniqueId().toString()) || App.unregistered.contains(player.getUniqueId().toString())) return true;
            try {
                player.teleport(player.getBedSpawnLocation());
            } catch (Exception e) {
                player.sendMessage("You don't have a bed!");
            }
            return true;
        }
        return false;
    }
    
}
