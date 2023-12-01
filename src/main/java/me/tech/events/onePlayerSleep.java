package me.tech.events;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.scheduler.BukkitTask;

import me.tech.App;

public class onePlayerSleep implements Listener{
    private Map<UUID, BukkitTask> sleeping = new HashMap<>();
    @EventHandler
    public void onPlayerSleep(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().equals(Bukkit.getWorld("world"))) return;
        if (player.getWorld().getTime() > 13000 || player.getWorld().isThundering()) {
            //player.sendMessage(Long.toString(player.getWorld().getTime()));
            sleeping.put(player.getUniqueId(), Bukkit.getScheduler().runTaskLater(App.getPlugin(App.class), new Runnable() {
                @Override
                public void run() {
                    Bukkit.getWorld("world").setTime(0);
                    sleeping.remove(player.getUniqueId());
                }
            }, 100L));
        }        
    }
    @EventHandler
    public void onPlayerLeaveBed(PlayerBedLeaveEvent event) {
        Player player = event.getPlayer();
        if (sleeping.containsKey(player.getUniqueId())) {
            sleeping.get(player.getUniqueId()).cancel();
            sleeping.remove(player.getUniqueId());
        }
    }
}
