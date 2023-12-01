package me.tech.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.tech.App;
import me.tech.dcConnect;


public class report implements CommandExecutor {
    private Plugin plugin = App.getPlugin(App.class);
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length < 2) return false;
            Player target = plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("The player cannot be found!");
                return true;
            }
            args[0] = "";
            String reason = String.join(" ", args);
            plugin.getLogger().info(reason);
            dcConnect.report(target.getName(), reason + " (this was reported by " + player.getName() + ")");
        }
        return false;
    }
}
