package com.server;

import java.io.*;
import java.net.Socket;


public class ClientHandler extends Thread {
    private Socket          client   = null;
    private Server          server   = null;
    private int             ID       = -1;
    private DataInputStream in       = null;
    private DataOutputStream out     = null;
    private volatile boolean shutdown = false;


    public ClientHandler(Server s, Socket socket) {
        server = s;
        client = socket;
        ID = client.getPort();
    }

    public void send(String message) {
        try{
            out.writeUTF(message);
            out.flush();
        }catch (Exception e){
            System.out.println(ID + " ERROR sending: " + e.getMessage());
            server.removeClient(ID);
            shutdown();
        }
    }

    public int getID() {
        return this.ID;
    }

    public void open() throws IOException {
        in = new DataInputStream(new
                BufferedInputStream(client.getInputStream()));
        out = new DataOutputStream(new
                BufferedOutputStream(client.getOutputStream()));
    }

    public void close() throws IOException {
        if (in != null)  in.close();
        if (out != null) out.close();
        if (client != null)  client.close();
    }
    public void shutdown(){
        this.shutdown = true;
    }

    @Override
    public void run() {
        System.out.println("Server Thread " + ID + " running.");
        while (!shutdown){
            try {
                server.handleClient(ID, in.readUTF());
            }catch (IOException e){
                System.out.println(ID + " ERROR reading: " + e.getMessage());
                server.removeClient(ID);
                shutdown();
            }
        }

    }

}

