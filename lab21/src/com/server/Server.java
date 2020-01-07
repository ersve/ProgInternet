package com.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server implements Runnable{
    private int MAX_CLIENTS = 50;
    private int connections = 0;
    public String exitString = "exit";
    private ClientHandler clients[] = new ClientHandler[MAX_CLIENTS];
    private ServerSocket server = null;
    private Thread thread = null;
    private ServerGui gui = null;


    public Server(int port, ServerGui serverGui) {
        gui = serverGui;
        try {
            System.out.println("Binding to port " + port + ", please wait  ...");
            gui.display("Binding to port " + port + ", please wait  ...\n");
            server = new ServerSocket(port);
            System.out.println("Server started: " + server);
            gui.display("Server started: " + server +"\n");
            start();

        } catch (IOException e) {
            System.out.println("Can not bind to port " + port + ": " + e.getMessage());
            gui.display("Can not bind to port " + port + ": " + e.getMessage());
        }
    }

    @Override
    public void run() {
        while ( thread != null) {
            try {
                System.out.println("Waiting for a client ...");
                gui.display("Waiting for a client ...");
                addClient(server.accept());
            } catch (Exception e) {
                System.out.println("Server accept error: " + e);
                gui.display("Server accept error: " + e.getMessage());
                stop();
            }
        }
        //Close down all clients
        for (int i = 0; i<connections;i++ ) {
            if( clients[i] != null) {
                try{
                clients[i].close();
                 } catch (IOException e){
                    //nothing to be done here
                }
            }

        }

    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if (thread != null) {
            thread = null;
        }

    }



    public synchronized void handleClient(int ID, String message) {

        if (message.equals(exitString)) {
            clients[findClient(ID)].send(exitString);

        } else {
            gui.display(clients[findClient(ID)].getIP()+":"+ID + ":" + message);
            for (int i = 0; i < connections; i++) {
                clients[i].send(clients[findClient(ID)].getIP()+":"+ID + ":" + message);
            }
        }


    }

    public synchronized void removeClient(int ID) {
        int position = findClient(ID);
        if (position > -1) {
            ClientHandler toRemove = clients[position];


            if (position < connections - 1) {
                for (int i = position + 1; i < connections; i++) {
                    clients[i - 1] = clients[i];
                }
            }
            connections--;
            try {
                toRemove.close();
            } catch (Exception e) {
                System.out.println("Error closing thread: " + e.getMessage());
                gui.display("Error closing thread: " + e.getMessage());
            }finally {
                System.out.println("Removed client " + toRemove.getIP()+":"+ ID +" at " + position);
                gui.display("Removed client " + toRemove.getIP()+":"+ ID +" at " + position +"\n");
            }
        }
    }

    public int findClient(int ID) {
        for (int i = 0; i < connections; i++) {
            if (clients[i].getID() == ID) {
                return i;
            }
        }
        return -1;
    }

    public void addClient(Socket client) {
        if (connections < MAX_CLIENTS) {
            System.out.println("Client accepted: " + client);
            gui.display("Client accepted: " + client +"\n");
            clients[connections] = new ClientHandler(this, client);
            try {
                clients[connections].open();
                clients[connections].start();
                connections++;
            } catch (Exception e) {
                System.out.println("Error opening thread: " + e);
            }
        } else {
            System.out.println("Client refused: maximum " + clients.length + " reached.");
            gui.display("Client refused: maximum " + clients.length + " reached." +"\n");
        }
        return;

    }


    public static void main(String[] args) {

        // Check if port number is assigned from input, else use port 2000
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 2000;
        // Start server
        //Server server = new Server(port);

    }
}





