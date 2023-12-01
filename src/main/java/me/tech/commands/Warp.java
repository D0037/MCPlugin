package me.tech.commands;

import java.util.*;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.tech.Util;

public class Warp {
    private static Map<String, Warp> warps = new HashMap<>();
    private Player owner = null;
    private Location position = null;
    private String name = null;
    Warp (Player player, Location loc, String name) {
        owner = player;
        position = loc;
        this.name = name;
        if (warps.containsKey(name)) return;
        warps.put(name, this);
        Util.save();
    }
    public Player getOwner() {
        return owner;
    }
    public void setOwner(Player player) {
        owner = player;
        warps.put(name, this);
    }
    public Location getLocation() {
        return position;
    }
    public void setLocation(Location loc) {
        position = loc;
        warps.put(name, this);
    }
    public void destroy() {
        warps.remove(name);
    }
    public String getName() {
        return this.name;
    }
    public static boolean isNameExists(String x) {
        if (warps.containsKey(x)) return true;
        return false;
    }
    public static Warp getWarpByName(String x) {
        return warps.get(x);
    }
    public static List<Warp> getAllWarps() {
        List<Warp> result = new ArrayList<>();
        for (Warp w : warps.values()) {
            result.add(w);
        }
        return result;
    }
    public static List<String> getAllWarpsName() {
        List<String> result = new ArrayList<>();
        for (String s: warps.keySet()) {
            result.add(s);
        }
        return result;
    }
    public static void setWarps(Map<String, Warp> x) {
        warps = x;
    }
    public static Map<String, Warp> getWarps() {
        return warps;
    }
}
