package me.tech.troll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import me.tech.App;


public class Troll implements CommandExecutor, Listener, TabCompleter {
    public static Set<String> lagTrolled = new HashSet<>();
    private Plugin plugin = App.getPlugin(App.class);
    private final String[] subcommands = {"lag", "minizombie"};
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String Label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player senderPlayer = (Player) sender;
            if (!senderPlayer.isOp()) return true;
        }
        if (args[1] != null && plugin.getServer().getPlayer(args[0]) != null) {
            Player player = plugin.getServer().getPlayer(args[0]);
                switch (args[1]) {
                    case "lag":
                        Lag(player);
                        break;
                    case "minizombie":
                        babyZombie(player);
                        break;
                    default:
                        return false;
                }
            return true;
        }
        return false;
    }
    private void Lag (Player player) {
        if (lagTrolled.contains(player.getName())) lagTrolled.remove(player.getName());
        lagTrolled.add(player.getName());
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            lagTrolled.remove(player.getName());
            }, 400L);
    }
    private void babyZombie(Player player) {
        for (int i = 0; i < 5; i++) {
            Zombie zombie = (Zombie) player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);
            zombie.setBaby();
        }
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (lagTrolled.contains(event.getPlayer().getName())) {
            Random random = new Random();
            boolean bool = random.nextBoolean();
            event.setCancelled(bool);
        }
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final List<String> completions = new ArrayList<>();
        final String[] players = new String[plugin.getServer().getOnlinePlayers().size()];
        int i = 0;
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            players[i] = player.getName();
        }
        if (args.length == 1) StringUtil.copyPartialMatches(args[0], Arrays.asList(players), completions);
        if (args.length == 2) StringUtil.copyPartialMatches(args[1], Arrays.asList(subcommands), completions);
        return completions;
    }
}