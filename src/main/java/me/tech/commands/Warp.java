package me.tech.commands;

import java.util.*;

import org.bukkit.Location;

import me.tech.Util;

public class Warp {
    private static Map<String, Warp> warps = new HashMap<>();
    private String owner = null;
    private Location position = null;
    private String name = null;

    public Warp (String owner, Location loc, String name) {
        this.owner = owner;
        position = loc;
        this.name = name;

        if (warps.containsKey(name)) return;
        warps.put(name, this);
        Util.save();
    }

    public Warp (String owner, Location loc, String name, boolean save) {
        this.owner = owner;
        position = loc;
        this.name = name;

        if (warps.containsKey(name)) {
            System.out.println("WTF!?");
            return;
        }

        warps.put(name, this);
        if (save) Util.save();
    } 

    public String getOwner() {
        return owner;
    }

    public void setOwner(String newOwner) {
        owner = newOwner;
        warps.put(name, this);
        Util.save();
    }

    public Location getLocation() {
        return position;
    }

    public void setLocation(Location loc) {
        position = loc;
        warps.put(name, this);
        Util.save();
    }

    public void destroy() {
        warps.remove(name);
        Util.save();
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

    public static void addWarp(Warp warp) {
        warps.put(warp.getName(), warp);
    }

    public static Map<String, Warp> getWarpsMap() {
        return warps;
    }
}