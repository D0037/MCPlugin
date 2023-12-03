package me.tech.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import me.tech.App;

public class invsee implements CommandExecutor, Listener {
    private Plugin plugin = App.getPlugin(App.class);
    private static Map<UUID, UUID> players = new HashMap<>();
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
    @NotNull String Label, @NotNull String[] args) { 
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.isOp()) {
                player.sendMessage("You don't have permission to do that!");
            }
            if (args.length == 0) return false;
            if (player.getName().equals(args[0])) return true;
            Player target = plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("There is no online player with this name!");
                return true;
            }
            ItemStack[] inventoryItems = target.getInventory().getContents();
            Inventory inventory = plugin.getServer().createInventory(null, 45, target.getName());
            inventory.setContents(inventoryItems);
            player.openInventory(inventory);
            players.put(player.getUniqueId(), target.getUniqueId());
            return true;
        }
        return false;
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (players.containsKey(player.getUniqueId())) {
            Player target = plugin.getServer().getPlayer(players.remove(player.getUniqueId()));
            Inventory inventory = event.getInventory();
            ItemStack[] items = player.getInventory().getContents();
            for (int i = 0; i < 41; i++) {
                items[i] = (inventory.getContents())[i];
            }
            target.getInventory().setContents(items);
        }
    }
}
