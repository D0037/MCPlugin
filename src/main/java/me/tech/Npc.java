package me.tech;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class Npc {
    public static ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        
        return item;
    }
    public interface NpcCommand {
        public void Command(Player player);
    }
    public static class NpcFields {
        public ItemStack item;
        public NpcCommand command;

        public NpcFields (ItemStack i, NpcCommand c) {
            item = i;
            command = c;
        }
    }
    private String name;
    private Location location;
    private NpcFields[] fields;
    private Villager villager;
    public Npc(String name, Location location, NpcFields... fields) {
        this.name = name;
        this.location = location;
        this.fields = fields;

        villager = (Villager) Bukkit.getServer().getWorld("world").spawnEntity(location, EntityType.VILLAGER);
        villager.setCustomName(name);
        villager.setCustomNameVisible(true);
        villager.setAI(false);
        villager.setSilent(true);
        villager.setCanPickupItems(false);
        villager.setInvulnerable(true);
        App.npcs.add(this);
        App.npcName.add(name);
    }

    public void showInventory(Player player) {
        int length = fields.length;
        if (length < 9) length = 9;
        else if (length < 18) length = 18;
        else if (length < 27) length = 27;
        else if (length < 36) length = 36;
        else if (length < 45) length = 45;
        Inventory inventory = Bukkit.createInventory(null, length, name);
        for (NpcFields field : fields) {
            inventory.addItem(field.item);
        }
        player.openInventory(inventory);
    }
    public void execute(ItemStack item, Player player) {
        for (NpcFields field : fields) {
            if (field.item.equals(item)) {
                field.command.Command(player);
                break;
            }
        }
    }
    public Location getLocation() {
        return location;
    }
    public String getName() {
        return name;
    }
    public void kill() {
        villager.setHealth(0);
    }
}