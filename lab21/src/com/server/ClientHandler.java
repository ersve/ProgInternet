package com.server;

import java.io.*;
import java.net.Socket;


public class ClientHandler extends Thread {
    private Socket client = null;
    private Server server = null;
    private int ID = -1;
    private String IP = null;
    private BufferedReader reader = null;
    private PrintWriter writer = null;



    public ClientHandler(Server s, Socket socket) {
        server = s;
        client = socket;
        ID = client.getPort();
        IP = client.getInetAddress().getHostName();
    }

    public void send(String message) {
        try {
            writer.println(message);
            writer.flush();
        } catch (Exception e) {
            System.out.println(ID + " ERROR sending: " + e.getMessage());
            server.removeClient(ID);

        }
    }

    public int getID() {
        return this.ID;
    }

    public String getIP() {
        return this.IP;
    }

    public void open() throws IOException {
        reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        writer = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), "ISO-8859-1"), true);
    }

    public void close() throws IOException {
        if (reader != null) reader.close();
        if (writer != null) writer.close();
        if (client != null) client.close();
    }


    @Override
    public void run() {
        System.out.println("Serving client " + ID + ".");
        String message = null;
        String print = "Correct termination of connection.";
        try {
            while ((message = reader.readLine()) != null) {
                //Do stuff with the message
                server.handleClient(ID, message);
            }
            close();
        } catch (IOException e) {
            print = "Incorrect termination of connection.";
        }

        System.out.println(ID + ":" + print);
        server.removeClient(ID);
    }
}






