package com.client;



import java.io.*;
import java.net.*;

public class ChatLogic {
    //Default values
    private String          host;
    private Integer         port;
    private Socket          socket          = null;
    private ServerListner   clientListner   = null;
    private PrintWriter     writer          = null;
    private InetAddress     inetAddress     = null;
    private SocketAddress   socketAddress   = null;
    private Client gui             = null;


    public ChatLogic(String serverHost, Integer serverPort, Client chatgui){
        this.host = serverHost;
        this.port = serverPort;
        this.gui = chatgui;
        connect();


    }

    public void messageHandler(String message){
        if(message.equals("exit")){
            System.out.println("Shutting down client..");
            gui.dispose();
            disconnect();

        }else {
            gui.display(message);
        }
    }

    public void connect(){

        try
        {   //Create an object to store information about host
            inetAddress = InetAddress.getByName(host);
            socketAddress = new InetSocketAddress(inetAddress,port);

            //Start new socket
            socket = new Socket();

            //Try to connect to host
            socket.connect(socketAddress);

            if (socket.isConnected()){
                System.out.println("Connection accepted " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
                if (gui != null) {
                    gui.display("Connection accepted " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
                    gui.setTitle("Connected " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
                }
            }
            //Open writer data stream
            openWriter();
            if (!writer.checkError()) {
                //System.out.println("Output data stream open");


            }

            //Start the listner thread
            startListner();
            if (clientListner.isOpen()){
                //System.out.println("Input data stream is open");

            }
        } catch (UnknownHostException e) {
            System.out.println("Host unknown: " + e.getMessage());
            if (gui != null) gui.display("Host unknown: " + e.getMessage());
            exit();
        } catch (IOException e) {
            System.out.println("Unexpected exception: " + e.getMessage());
            if (gui != null) gui.display("Unexpected exception: " + e.getMessage());
            exit();
        }
    }

    public void disconnect(){
        //close the socket
        closeSocket();
        if(socket.isClosed()){
            //System.out.println("Client socket is closed");
            //if (gui != null) gui.display("Client socket is closed");
        }

        //Stop the listner thread
        stopListner();
        if (clientListner == null){
            //System.out.println("Input data stream is closed");

        }
        //Stop the writer
        closeWriter();
        if (writer == null) {
            //System.out.println("Output data stream is closed");

        }


    }

    public void closeSocket() {
            try{
                socket.close();
            } catch (IOException e) {
                System.out.println("ERROR closing socket: "+ e.getMessage());


            }
    }

    public void startListner(){
        if (clientListner == null){
            clientListner = new ServerListner(this , socket);
            clientListner.start();
        }
    }

    public void stopListner(){
        // Listner can be stopped by
        // interrupting the thread or closing the socket
        if (clientListner != null) {
            clientListner.open = false;
            clientListner.close();
            clientListner = null;
        }

    }

    public void openWriter(){
        try {
            writer = new PrintWriter(
                    new OutputStreamWriter(
                            socket.getOutputStream(), "ISO-8859-1"), true);

        } catch (UnsupportedEncodingException e) {
            System.out.println("Unsupported Encoding: "+ e.getMessage());



        } catch (IOException e) {
            System.out.println("Error opening writer: " + e.getMessage());


        }
    }

    public void closeWriter(){
        if (writer != null){
            writer.close();
           // writer = null;
        }
    }

    public void send(String message){
        writer.println(message);
    }

    public void exit(){
        gui.dispose();
        disconnect();
    }



}

