package me.tech;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.*;

class connServer implements Runnable {
    private int port;
    public connServer (int port) {
        this.port = port;
    }

    private void clientHandler (Socket clientSocket) {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            StringBuilder requestBuilder = new StringBuilder();

            inputLine = in.readLine(); //) != null) {
            requestBuilder.append(inputLine);


            JSONObject request = new JSONObject(requestBuilder.toString());
            String method = request.getString("method");
            //String user = request.getString("username");
            //String message = request.getString("message");

            JSONObject response = new JSONObject();

            if (method.equals("send")) {
                String user = request.getString("username");
                String message = request.getString("message");
                Util.sendToPlayers(user, message);
            } else if (method.equals("online")) {
                response.put("online", Util.getOnlinePlayers());
            }

            response.put("status", "ok");
            out.println(response.toString());

            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Java RPC Server is listening on port " + Integer.toString(port));

            while (App.exit != true) {
                Socket clientSocket = serverSocket.accept();
                clientHandler(clientSocket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
