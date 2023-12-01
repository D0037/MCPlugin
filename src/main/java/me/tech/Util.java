package me.tech;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

import me.tech.commands.Warp;
import net.md_5.bungee.api.ChatColor;

public class Util {
   private static Plugin plugin = App.getPlugin(App.class);
   private static final String url = "jdbc:mysql://" + plugin.getConfig().getString("host") + "/" + plugin.getConfig().getString("db") + "?serverTime=UTC";
   private static final String dbuser = plugin.getConfig().getString("user");
   private static final String dbpassword = plugin.getConfig().getString("password");
   private static File savedDataFile;
   private static FileConfiguration savedData;
   public static void save() {
      savedData.set("inventories", App.inventories);
      savedData.set("locations", App.locations);
      savedData.set("isOP", App.isOP);
      savedData.set("gameMode", App.gameMode);
      savedData.set("health", App.health);
      savedData.set("xp", App.xp);
      savedData.set("foodLevel", App.foodLevel);
      savedData.set("Experience", App.Exprience);
      savedData.set("unregistered", App.unregistered.toArray());
      savedData.set("FireTicks", App.FireTicks);
      savedData.set("PotionEffects", App.PotionEffects);
      savedData.set("warps", Warp.getAllWarps());
      plugin.saveResource("data.yml", true);
      try {
         savedData.save(savedDataFile);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   public static void restore() {
      try {
         for (String key : savedData.getConfigurationSection("inventories").getKeys(false)) {
               Set<ItemStack> itemSet = new HashSet<>();
               for (Object object : savedData.getList("inventories." + key)) {
                  if (object == null) {
                     itemSet.add(null);
                  } else {
                     itemSet.add((ItemStack) object);
                  }
                  itemSet.add((ItemStack) object);
               }
               Object[] fuckingArray = itemSet.toArray();
               ItemStack[] items = new ItemStack[fuckingArray.length];
               int i = 0;
               for (Object object : fuckingArray) {
                  items[i] = (ItemStack) object;
                  i++;
               }
               App.inventories.put(key, items);
         }
         ItemStack item = new ItemStack(Material.DIAMOND, 5);
         Object obj = (Object) item;
         plugin.getLogger().info(((ItemStack) obj).getType().toString());
         for (String key : savedData.getConfigurationSection("locations").getKeys(false)) {
               App.locations.put(key, savedData.getLocation("locations." + key));
         }
         for (String key : savedData.getConfigurationSection("isOP").getKeys(false)) {
               App.isOP.put(key, savedData.getBoolean("isOP." + key));
         }
         for (String key : savedData.getConfigurationSection("gameMode").getKeys(false)) {
               App.gameMode.put(key, savedData.getString("gameMode." + key));
         }
         for (String key : savedData.getConfigurationSection("health").getKeys(false)) {
               App.health.put(key, savedData.getDouble("health." + key));
         }
         for (String key : savedData.getConfigurationSection("xp").getKeys(false)) {
               App.xp.put(key, savedData.getInt("xp." + key));
         }
         for (String key : savedData.getConfigurationSection("foodLevel").getKeys(false)) {
               App.foodLevel.put(key, savedData.getInt("foodLevel." + key));
         }
         for (String key : savedData.getConfigurationSection("Experience").getKeys(false)) {
               App.Exprience.put(key, ((float) savedData.getDouble("Experience." + key)));
         }
         for (Object value: savedData.getList("unregistered")) {
               App.unregistered.add(value.toString());
         }
         for (String key : savedData.getConfigurationSection("PotionEffects").getKeys(false)) {
               Set<PotionEffect> effects = new HashSet<>();
               for (Object object : savedData.getList("PotionEffects." + key)) {
                  effects.add((PotionEffect) object);
               }
               App.PotionEffects.put(key, (Collection<PotionEffect>) effects);
         }
         for (String key : savedData.getConfigurationSection("warps").getKeys(false)) {
               Warp.getWarps().put(key, (Warp) savedData.get("warps." + key));
         }
      } catch (Exception e) {
         plugin.getLogger().warning("If you are starting the plugin for the first time this is normal!");
         e.printStackTrace();
      }
   }
   public static void createCustomConfig() {
      savedDataFile = new File(plugin.getDataFolder(), "data.yml");
      if (!savedDataFile.exists()) {
         savedDataFile.getParentFile().mkdirs();
         plugin.saveResource("data.yml", false);
      }

      savedData = new YamlConfiguration();
      try {
         savedData.load(savedDataFile);
      } catch (IOException | InvalidConfigurationException e) {
         e.printStackTrace();
      }
   }

   public static boolean validate(Player player) {
      if (App.unregistered.contains(player.getUniqueId().toString()) || App.isOP.containsKey(player.getUniqueId().toString())) return false;
      return true;
   }
   public static Object[] getOnlinePlayers() {
      Set<String> online = new HashSet<>(); 
      for (Player player : plugin.getServer().getOnlinePlayers()) {
         online.add(player.getName());
      }
      return online.toArray();
   }
   public static ItemStack signItem(ItemStack item, String tag) {
      if (item == null || tag == null) return null;

      ItemMeta meta = item.getItemMeta();
      if (meta != null) {
         meta.getPersistentDataContainer().set(
               new NamespacedKey(App.getPlugin(App.class), "sign"),
               PersistentDataType.STRING,
               tag
         );
         item.setItemMeta(meta);
         return item;
      }
      return null;
   }

   public static boolean isSigned(ItemStack item, String tag) {
      if (item == null || tag == null) return false;

      ItemMeta meta = item.getItemMeta();
      String sign = meta.getPersistentDataContainer().get(
         new NamespacedKey(App.getPlugin(App.class), "sign"),
         PersistentDataType.STRING
      );
      if (sign == null) return false;
      if (sign.equals(tag)) return true;
      return false; 
   }

   public static String getSign(ItemStack item) {
      if (item == null) return null;

      ItemMeta meta = item.getItemMeta();
      if (meta != null)  {
         return meta.getPersistentDataContainer().get(
               new NamespacedKey(App.getPlugin(App.class), "sign"),
               PersistentDataType.STRING
         );
      }
      return null;
   }
   public static boolean isRegistered(String username) {
      try {
         String sql = "SELECT username FROM users WHERE username=?";
         Connection connection = DriverManager.getConnection(url, dbuser, dbpassword);
         PreparedStatement pstmt = connection.prepareStatement(sql);
         pstmt.setString(1, username);
         ResultSet rs = pstmt.executeQuery();
         return rs.next();
      } catch (SQLException e) {
         plugin.getLogger().warning(e.getStackTrace().toString());
         return false;
      }
   }
   public static void sendToPlayers(String user, String message) {
      String msg = ChatColor.BLUE + "[" + user + "] " + ChatColor.WHITE + message;
      for (Player player : Bukkit.getOnlinePlayers()) {
         player.sendMessage(msg);
      }
   }
   public static String hashString(String string) {
      StringBuilder hashedPassword = new StringBuilder();
      try {
         final MessageDigest digest = MessageDigest.getInstance("SHA-256");
         final byte[] hash = digest.digest(string.getBytes("UTF-8"));
         for (int i = 0; i < hash.length; i++) {
               final String hex = Integer.toHexString(0xff & hash[i]);
               if(hex.length() == 1) 
                  hashedPassword.append('0');
               hashedPassword.append(hex);
         }
      } catch (Exception e) {
         Bukkit.getLogger().severe(e.toString());
      }
      return hashedPassword.toString();
   } 
   public static boolean checkPassword (String username, String password) {
      try {
         Connection conn = DriverManager.getConnection(url, dbuser, dbpassword);
         PreparedStatement pstmt = conn.prepareStatement("select * from users where username=?");
         pstmt.setString(1, username);
         ResultSet rs = pstmt.executeQuery();
         if (rs.next() && rs.getString("password").equals(hashString(password))) {
               return true;
         }
      } catch (SQLException e) {
         Bukkit.getLogger().severe(e.toString());
      }
      return false;
   }
   public static boolean register(String username, String password) {
      String sql = "SELECT username FROM users WHERE username=?";
      if (isRegistered(username)) return false;
      sql = "INSERT INTO users VALUES (?,?)";
      try {
         Connection connection = DriverManager.getConnection(url, dbuser, dbpassword);
         PreparedStatement pstmt = connection.prepareStatement(sql);
         pstmt.setString(1, username);
         pstmt.setString(2, hashString(password));
         pstmt.execute();
         return true;
      } catch (SQLException e) {
         Bukkit.getLogger().severe(e.toString());
      }
      return false;
   }
   public static boolean change(String username, String oldPassword, String newPassword) {
      String sql = "SELECT username FROM users WHERE username=?";
      if (!isRegistered(username) || !checkPassword(username, oldPassword) || !validate(plugin.getServer().getPlayer(username))) return false;
      sql = "UPDATE users SET password=? WHERE username=?";
      try {
         Connection connection = DriverManager.getConnection(url, dbuser, dbpassword);
         PreparedStatement pstmt = connection.prepareStatement(sql);
         pstmt.setString(1, hashString(newPassword));
         pstmt.setString(2, username);
         pstmt.execute();
         return true;
      } catch (SQLException e) {
         Bukkit.getLogger().severe(e.toString());
      }
      return false;
   }
   public static boolean delete(String username, String password) {
      String sql = "SELECT username FROM users WHERE username=?";
      if (!isRegistered(username) || !checkPassword(username, password) || !validate(plugin.getServer().getPlayer(username))) return false;
      sql = "DELETE FROM users WHERE username=?";
      try {
         Connection connection = DriverManager.getConnection(url, dbuser, dbpassword);
         PreparedStatement pstmt = connection.prepareStatement(sql);
         pstmt.setString(1, username);
         pstmt.execute();
         return true;
      } catch (SQLException e) {
         Bukkit.getLogger().severe(e.toString());
      }
      return false;
   }
}