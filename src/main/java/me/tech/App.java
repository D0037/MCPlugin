package me.tech;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.tech.commands.*;
import me.tech.events.*;
import me.tech.troll.Troll;
import net.md_5.bungee.api.ChatColor;

public class App extends JavaPlugin implements Listener {
    private static Plugin plugin = App.getPlugin(App.class);
    public static Map<String, ItemStack[]> inventories = new HashMap<>();
    public static Map<String, Location> locations = new HashMap<>();
    public static Map<String, Boolean> isOP = new HashMap<>();
    public static Map<String, String> gameMode = new HashMap<>();
    public static Map<String, Double> health = new HashMap<>();
    public static Map<String, Integer> xp = new HashMap<>();
    public static Map<String, Integer> foodLevel = new HashMap<>();
    public static Map<String, Float> Exprience = new HashMap<>();
    public static Map<String, Integer> FireTicks = new HashMap<>();
    public static Map<String, Collection<PotionEffect>> PotionEffects = new HashMap<>();
    public static Set<String> unregistered = new HashSet<>();
    public static Set<String> AFKing = new HashSet<>();
    public static Map<String, BukkitTask> AFKCounter = new HashMap<>();
    private final FileConfiguration conf = this.getConfig();
    private static final int spawnx = plugin.getConfig().getInt("cordx");
    private static final int spawny = plugin.getConfig().getInt("cordy");
    private static final int spawnz = plugin.getConfig().getInt("cordz");
    public static Set<Npc> npcs = new HashSet<>();
    public static Set<String> npcName = new HashSet<>();
    public static boolean exit;

    @Override
    public void onEnable() {
        Util.createCustomConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new spawner(), this);
        getServer().getPluginManager().registerEvents(new NpcEvents(), this);
        getServer().getPluginManager().registerEvents(new onePlayerSleep(), this);
        getServer().getPluginManager().registerEvents(new Troll(), this);
        getServer().getPluginManager().registerEvents(new invsee(), this);

        getCommand("login").setExecutor(this);
        getCommand("register").setExecutor(new Register());
        getCommand("delete").setExecutor(new Delete());
        getCommand("change_password").setExecutor(new Change());
        getCommand("home").setExecutor(new home());
        getCommand("troll").setExecutor(new Troll());
        getCommand("troll").setTabCompleter(new Troll());
        getCommand("report").setExecutor(new report());
        getCommand("invsee").setExecutor(new invsee());
        Util.restore();

        exit = false;
        dcConnect.start_server();
    }
    @Override
    public void onDisable() {
        for (Npc npc : npcs) {
            npc.kill();
        }
        exit = true;
    }
    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (AFKing.contains(player.getUniqueId().toString())) {
            AFKing.remove(player.getUniqueId().toString());
        }
        if (AFKCounter.containsKey(player.getUniqueId().toString())) {
            AFKCounter.remove(player.getUniqueId().toString()).cancel();
        }
    }
    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        final Player player = event.getPlayer();
        if (isOP.containsKey(player.getUniqueId().toString()) || unregistered.contains(player.getUniqueId().toString())) {
            event.setCancelled(true);
        } else {
            dcConnect.sendToDcBot(player.getName(), event.message().toString());
        }
    }

    

    @Override
    public boolean onCommand (@NotNull CommandSender sender, @NotNull Command command, @NotNull String Label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            conf.set("pls", "inventories");
            String password = args[0];
            if (args.length < 1) {
                player.sendMessage("No password given!");
                return false;
            }
            if (!isOP.containsKey(player.getUniqueId().toString())) {
                player.sendMessage("You are already logged in!");
                return true;
            }
            if (Util.checkPassword(player.getName(), password)) {
                player.sendMessage("You are successfully logged in!");
                getLogger().info(player.getName() + " logged in!");
                ItemStack[] inventory = inventories.get(player.getUniqueId().toString());
                
                Location location = locations.remove(player.getUniqueId().toString());
                Boolean op = isOP.remove(player.getUniqueId().toString());
                player.getInventory().clear();
                if (inventory != null) {
                    player.getInventory().setContents(inventory);
                }

                player.teleport(location);
                player.setOp(op);
                player.setGameMode(GameMode.valueOf(gameMode.remove(player.getUniqueId().toString())));
                player.setHealth(health.remove(player.getUniqueId().toString()));
                player.setLevel(xp.remove(player.getUniqueId().toString()));
                player.setFoodLevel(foodLevel.remove(player.getUniqueId().toString()));
                player.setExp(Exprience.remove(player.getUniqueId().toString()));
                player.addPotionEffects(PotionEffects.get(player.getUniqueId().toString()));
                Util.save();
                return true;
            } else {
                player.sendMessage("Incorrect username or password! If you didn't registered yet you can do this now with /register <your_new_password>");
                return true;
            }
        }
        return false;
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        final Runnable task = new Runnable() {
            @Override
            public void run() {
                getLogger().info(player.getName() + "has started to afk!");
                AFKing.add(player.getUniqueId().toString());
            }
        };
        if (isOP.containsKey(player.getUniqueId().toString()) || unregistered.contains(player.getUniqueId().toString())) {
            event.setCancelled(true);
            return;
        }
        if (AFKing.contains(player.getUniqueId().toString())) {
            final Runnable removeTask = new Runnable() {
                @Override
                public void run() {
                    getLogger().info(player.getName() + " has stopped AFKing!");
                    AFKing.remove(player.getUniqueId().toString());    
                }
            };
            getServer().getScheduler().runTaskLater(this, removeTask, 400L);
        }
        if (!AFKCounter.containsKey(player.getUniqueId().toString())) {
            AFKCounter.put(player.getUniqueId().toString(), getServer().getScheduler().runTaskLater(this, task, 1200L));
        }
        AFKCounter.replace(player.getUniqueId().toString(), getServer().getScheduler().runTaskLater(this, task, 1200L)).cancel();
    }
    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (AFKing.contains(player.getUniqueId().toString())) {
            AFKing.remove(player.getUniqueId().toString());
        }
        if (AFKCounter.containsKey(player.getUniqueId().toString())) {
            AFKCounter.remove(player.getUniqueId().toString()).cancel();
        }
    }
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            Player player = (Player) event.getEntity();
            if  (!AFKing.contains(player.getUniqueId().toString())) return;
            player.kickPlayer("You get damage while you was afking.");
            if (event.getDamager().getType().equals(EntityType.PLAYER)) {
                event.getDamager().sendMessage("You damaged a player who was afking. Don't do that bro! (This incident was reported to the admins!)");
                dcConnect.report(event.getDamager().getName(), "Damaged " + player.getName() + "while he afked!");
            }
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (isOP.containsKey(player.getUniqueId().toString())) {
            return;
        }
        if (Util.isRegistered(player.getName())) {
            player.sendMessage("Hello " + ChatColor.GREEN + player.getName() + ChatColor.WHITE + "! You have to log in before you play, with " + ChatColor.BLUE + "/login your_password");
            if (isOP.containsKey(player.getUniqueId().toString())) {
                return;
            }
        } else {
            player.sendMessage("Hello " + ChatColor.GREEN + player.getName() + ChatColor.WHITE + "! You have to register before you play, with the " + ChatColor.BLUE + "/register your_password" + ChatColor.WHITE + " command!");
            if (!unregistered.contains(player.getUniqueId().toString())) {
                unregistered.add(player.getUniqueId().toString());
                player.setGameMode(GameMode.ADVENTURE);
                player.teleport(new Location(Bukkit.getWorld("world"), spawnx + 0.5, spawny, spawnz + 0.5, 0, 0));
                return;
            }
        }
        Integer levels = player.getLevel();
        ItemStack[] inventory = player.getInventory().getContents();
        inventories.put(player.getUniqueId().toString(), inventory);
        locations.put(player.getUniqueId().toString(), player.getLocation());
        isOP.put(player.getUniqueId().toString(), player.isOp());
        gameMode.put(player.getUniqueId().toString(), player.getGameMode().toString());
        health.put(player.getUniqueId().toString(), player.getHealth());
        xp.put(player.getUniqueId().toString(), levels);
        foodLevel.put(player.getUniqueId().toString(), player.getFoodLevel());
        Exprience.put(player.getUniqueId().toString(), player.getExp());
        FireTicks.put(player.getUniqueId().toString(), player.getFireTicks());
        PotionEffects.put(player.getUniqueId().toString(), player.getActivePotionEffects());
        player.setExp(0);
        player.setFireTicks(-20);
        player.setFoodLevel(20);
        player.setLevel(0);
        player.setHealth(20.0);
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();
        player.setOp(false);

        player.teleport(new Location(Bukkit.getWorld("world"), spawnx + 0.5, spawny, spawnz + 0.5, 0, 0));
        for (PotionEffect effect : PotionEffects.get(player.getUniqueId().toString())) {
            player.removePotionEffect(effect.getType());
        }
        Util.save();
    }
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (isOP.containsKey(player.getUniqueId().toString()) || unregistered.contains(player.getUniqueId().toString())) {
            Bukkit.getScheduler().runTaskLater(this, () -> player.teleport(new Location(Bukkit.getWorld("world"), spawnx + 0.5, spawny, spawnz + 0.5, 0, 0)), 1L);
        }
    }
    public class Register implements CommandExecutor{
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                String[] args) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length < 1) {
                    return false;
                }
                if (Util.register(player.getName(), args[0])) {
                    player.sendMessage("You are succesfully registered!");
                    unregistered.remove(player.getUniqueId().toString());
                    player.setGameMode(GameMode.SURVIVAL);
                } else {
                    player.sendMessage("The username is already taken!");
                }
            }
            return true;
        }
    }
    public class Change implements CommandExecutor {
        @Override
        public boolean onCommand (@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                String[] args) {
            if (args.length < 2) {
                return false;
            }
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (Util.change(player.getName(), args[0], args[1])) {
                    player.sendMessage("Your password has been successfully changed!");
                } else {
                    player.sendMessage("Invalid password!");
                }
                return true;
            }
            return false;
        }
    }
    public class Delete implements CommandExecutor {
        @Override
        public boolean onCommand (@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                String[] args) {
            if (args.length < 1) {
                return false;
            }
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (Util.delete(player.getName(), args[0])) {
                    player.kickPlayer("Your account has been successfully deleted!");
                } else {
                    player.sendMessage("Invalid password!");
                }
            }
            return false;
        }
    }
}