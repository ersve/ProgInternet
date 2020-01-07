package com.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class ServerListner extends Thread {
    private ChatLogic       clientInstance  = null;
    private Socket          socket          = null;
    private BufferedReader  reader          = null;
    public boolean          open            = false;


    public ServerListner(ChatLogic instance, Socket socket){
        this.clientInstance = instance;
        this.socket         = socket;
        open();
    }

    public void open(){
        try {
            reader = new BufferedReader(
                        new InputStreamReader(
                            socket.getInputStream(),"ISO-8859-1"));
            open = true;
        } catch (UnsupportedEncodingException e) {
            System.out.println("ERROR opening reader:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("ERROR opening reader:" + e.getMessage());
        }

    }
    public boolean isOpen(){
        return open;
    }

    public void close(){
        try {
            if(reader != null) {
                reader.close();
                open = false;
            }
        } catch (IOException e) {
            System.out.println("ERROR closing reader:" + e.getMessage());
        }
    }

    @Override
    public void run() {
        String message;

        try {
            while(((message = reader.readLine()) != null) && open){
                clientInstance.messageHandler(message);
            }
            if ( message == null){
                System.out.println("Server disconnected.");
            }
        } catch (IOException e) {
            System.out.println("ERROR Server:" + e.getMessage());


        } catch (NullPointerException e) {
            System.out.println("Input Stream terminated:" + e.getMessage());

        } finally {
            close();
            clientInstance.exit();

        }
    }
}
