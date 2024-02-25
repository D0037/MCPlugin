package me.tech;

import org.bukkit.configuration.file.FileConfiguration;
import java.io.*;
import java.net.*;
import org.json.*;

public class dcConnect {
    private static final FileConfiguration conf = App.getPlugin(App.class).getConfig();
    private static String serverAddress = conf.getString("dc_server_address");
    private static int serverPort = conf.getInt("dc_server_port");
    public static Thread serverProcess;


    public static void sendToDcBot(String user, String message) {
        try (Socket socket = new Socket(serverAddress, serverPort)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("method", "send");
            jsonObject.put("user", user);
            jsonObject.put("message", message);
            out.println(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void report(String user, String reason) {
        try (Socket socket = new Socket(serverAddress, serverPort)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("method", "report");
            jsonObject.put("user", user);
            jsonObject.put("reason", reason);
            out.println(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void start_server() {
        serverProcess = new Thread(new connServer(conf.getInt("java_server_port")));
        serverProcess.start();
        
    }
}
