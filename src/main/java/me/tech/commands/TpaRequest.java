package me.tech.commands;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import me.tech.App;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class TpaRequest {
    private static Map<UUID, TpaRequest> requests = new HashMap<>();
    private Player sender;
    private Player target;
    private BukkitTask task;
    private static Plugin plugin = App.getPlugin(App.class);
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            requests.remove(target.getUniqueId());
            TextComponent message = new TextComponent(sender.getName() + "'s teleport request has expired.");
            target.sendMessage(message.toLegacyText());
        }
    };
    TpaRequest(Player sender, Player target) {
        this.sender = sender;
        this.target = target;
        ComponentBuilder message = new ComponentBuilder(sender.getName() + " has requested to teleport to you.");
        TextComponent accept = new TextComponent(" [Accept] ");
        accept.setColor(ChatColor.GREEN);
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa accept"));
        message.append(accept);
        TextComponent deny = new TextComponent(" §c[Deny] ");
        deny.setColor(ChatColor.RED);
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa deny"));
        message.append(deny);
        message.append("§7(this request will expire in 30 seconds)");
        target.sendMessage(message.create());
        task = plugin.getServer().getScheduler().runTaskLater(plugin, runnable, 600L);
        requests.put(target.getUniqueId(), this);
    }
    public void accept() {
        sender.teleport(target);
        task.cancel();
        requests.remove(target.getUniqueId());
    }
    public void deny() {
        task.cancel();
        requests.remove(target.getUniqueId());
        sender.sendMessage("Your teleport request to $a" + target.getName() + "§fhas been denied.");
    }
    public static TpaRequest getRequest(Player player) {
        return requests.get(player.getUniqueId());
    }
}