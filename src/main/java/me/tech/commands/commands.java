package me.tech.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import me.tech.Util;

public class commands {
    public static class warp implements CommandExecutor, TabCompleter {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
        @NotNull String Label, @NotNull String[] args) { 
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            if (!Util.validate(player)) return true;
            if (args.length < 1) return false;
            if (args[0].equals("create")) {
                if (args.length < 2) return false;
                if (args[1].equals("create") || args[1].equals("delete")) return true;
                if (Warp.isNameExists(args[1])) {
                    player.sendMessage("A warp is already exists eith this name! You will have to choose an other one!");
                    return true;
                }
                Warp warp = new Warp(player.getName(), player.getLocation(), args[1]);
                player.sendMessage("The warp has been successfully created with the name: " + warp.getName());
            } else if (args[0].equals("delete")) {
                if (args.length < 2) return false;
                if (!Warp.isNameExists(args[1])) {
                    player.sendMessage("No warp found with this name");
                    return true;
                }
                Warp warp = Warp.getWarpByName(args[1]);
                if (!warp.getOwner().equals(player.getName())) {
                    player.sendMessage("You can't delete someone else's warp!");
                    return true;
                }
                warp.destroy();
                player.sendMessage("The has been successfully deleted!");
            } else {
                if (!Warp.isNameExists(args[0])) {
                    player.sendMessage("No warp found with this name!");
                    return true;
                }
                player.teleport(Warp.getWarpByName(args[0]).getLocation());
            }
            return true;
        }
        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            final List<String> completions = new ArrayList<>();
            final List<String> warps = Warp.getAllWarpsName();
            if (args.length == 2) StringUtil.copyPartialMatches(args[1], warps, completions);
            if (args.length == 2 && args[1].equals("delete")) StringUtil.copyPartialMatches(args[1], warps, completions);
            warps.add("create");
            warps.add("delete");
            if (args.length == 1) StringUtil.copyPartialMatches(args[0], warps, completions);
            return completions;
        }
    }
}