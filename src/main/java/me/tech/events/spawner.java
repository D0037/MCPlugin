package me.tech.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import me.tech.App;
import me.tech.Util;

public class spawner implements Listener {
    private Plugin plugin = App.getPlugin(App.class);
    @EventHandler
    public void onSpawnerBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location location = block.getLocation();
        if (event.getBlock().getType().equals(Material.SPAWNER)) {
            if (!player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
                return;
            }
            event.setCancelled(true);
            CreatureSpawner spawner = (CreatureSpawner) block.getState();
            EntityType type = spawner.getSpawnedType();
            block.setType(Material.AIR);
            ItemStack itemStack = new ItemStack(Material.SPAWNER);
            ItemMeta meta = itemStack.getItemMeta();
            String mobType = new String();
            int count = spawner.getSpawnCount() / 4;
            switch (type) {
                case ZOMBIE:
                    mobType = "Zombie";
                    break;
                case SKELETON:
                    mobType = "Skeleton";
                    break;
                case CAVE_SPIDER:
                    mobType = "Cave spider";
                    break;
                case SPIDER:
                    mobType = "Spider";
                    break;
                case SILVERFISH:
                    mobType = "Silverfish";
                    break;
                case BLAZE:
                    mobType = "Blaze";
                    break;
                case MAGMA_CUBE:
                    mobType = "Magma cube";
                    break;
                default:
                    return;
            }
            meta.setDisplayName(mobType + " spawner");
            itemStack.setItemMeta(meta);
            itemStack = Util.signItem(itemStack, mobType + " spawner");
            itemStack.setAmount(count);
            Item droppedItem = (Item) plugin.getServer().getWorld(location.getWorld().getName()).spawnEntity(location, EntityType.DROPPED_ITEM);
            droppedItem.setItemStack(itemStack);
        }
    }
    
    @EventHandler
    public void onSpawnerPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType().equals(Material.SPAWNER)) {
            Player player = event.getPlayer();
            ItemStack spawnerItem = player.getInventory().getItemInMainHand();
            ItemMeta meta = spawnerItem.getItemMeta();
            String res = meta.getPersistentDataContainer().get(
                new NamespacedKey(App.getPlugin(App.class), "sign"),
                PersistentDataType.STRING
            );
            if (res == null) return;
            block.setType(Material.AIR);
            EntityType type = EntityType.PIG;
            switch (Util.getSign(spawnerItem)) {
                case "Zombie spawner":
                    type = EntityType.ZOMBIE;
                    break;
                case "Skeleton spawner":
                    type = EntityType.SKELETON;
                    break;
                case "Cave spider spawner":
                    type = EntityType.CAVE_SPIDER;
                    break;
                case "Spider spawner":
                    type = EntityType.SPIDER;
                    break;
                case "Silverfish spawner":
                    type = EntityType.SILVERFISH;
                    break;
                case "Blaze spawner":
                    type = EntityType.BLAZE;
                    break;
                case "Magma cube spawner":
                    type = EntityType.MAGMA_CUBE;
                    break;
                default:
                    break;
            }
            if (player.getTargetBlock(4).getType().equals(Material.SPAWNER)) {
                Block targetBlock = player.getTargetBlock(4);
                CreatureSpawner spawner = (CreatureSpawner) targetBlock.getState();
                if (spawner.getSpawnedType().equals(type)) {
                    CreatureSpawner targetSpawner = (CreatureSpawner) targetBlock.getState();
                    targetSpawner.setSpawnCount(targetSpawner.getSpawnCount() + 4);
                    targetSpawner.update();
                    return;
                }
            }
            block.setType(Material.SPAWNER);
            CreatureSpawner spawner = (CreatureSpawner) block.getState();
            spawner.setSpawnedType(type);
            spawner.update();
        }
    }
}