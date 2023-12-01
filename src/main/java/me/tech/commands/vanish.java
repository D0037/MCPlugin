package me.tech.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.md_5.bungee.api.ChatColor;

public class vanish implements CommandExecutor{
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.isOp()) {
                if (p.isInvisible()) {
                    p.setInvisible(false);
                    p.setInvulnerable(false);
                    p.sendMessage(ChatColor.RED + "Vanish is disabled!");
                } else {
                    p.setInvisible(true);
                    p.setInvulnerable(true);
                    p.sendMessage(ChatColor.GREEN + "Vanish is enabled!");
                }
            } else {
                p.sendMessage(ChatColor.RED + "You dont have the permission for this!");
            }
        }
        return true;
    }
}
