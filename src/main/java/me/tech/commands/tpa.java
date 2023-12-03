package me.tech.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

import me.tech.Util;

public class tpa implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length < 1) return false;
            if (args[0].equals(player.getName())) return true;
            if (!Util.validate(player)) return true;
            else if (args[0].equals("accept")) {
                TpaRequest request = TpaRequest.getRequest(player);
                if (request == null) {
                    player.sendMessage("You don't have any pending requests!");
                    return true;
                }
                request.accept();
                return true;
            } else if (args[0].equals("deny")) {
                TpaRequest request = TpaRequest.getRequest(player);
                if (request == null) {
                    player.sendMessage("You don't have any pending requests!");
                    return true;
                }
                request.deny();
                return true;
            }
            Player target = player.getServer().getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("The player cannot be found!");
                return true;
            }
            new TpaRequest(player, target);
            player.sendMessage("Your request has been sent to " + target.getName() + "!");
            return true;
        }
        return false;
    }
}