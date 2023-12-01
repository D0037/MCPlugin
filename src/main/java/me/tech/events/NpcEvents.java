package me.tech.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import me.tech.App;
import me.tech.Npc;

public class NpcEvents implements Listener {
    @EventHandler
    public void onNpcInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().isCustomNameVisible() && App.npcName.contains(event.getRightClicked().getCustomName())) {
            Player player = event.getPlayer();
            for (Npc npc : App.npcs) {
                if (npc.getName().equals(event.getRightClicked().getCustomName())) {
                    event.setCancelled(true);
                    npc.showInventory(player);
                    break;
                }
            }
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (App.npcName.contains(event.getWhoClicked().getOpenInventory().getTitle())) {
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            for (Npc npc : App.npcs) {
                if (npc.getName().equals(event.getWhoClicked().getOpenInventory().getTitle())) {
                    npc.execute(event.getCurrentItem(), player);
                    break;
                }
            }
        }
    }
}