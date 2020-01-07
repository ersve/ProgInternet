package com.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class ServerGui extends JFrame {
    Server serverLogic;
    JTextArea taMessages = null;
    int port = 0;


    public ServerGui( int serverPort ){
        port = serverPort;
        //create area for messages
        taMessages = new JTextArea();
        taMessages.setRows(10);
        taMessages.setColumns(50);
        taMessages.setEditable(false);

        JScrollPane spMessages = new JScrollPane(taMessages, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(spMessages,"Center");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(
                new WindowAdapter() {

                    @Override
                    public void windowClosing(WindowEvent e) {
                        serverLogic.stop();


                    }
                }
        );

        setSize(500,300);
        setVisible(true);
        pack();

        serverLogic = new Server(port,this);

    }

    // Called from the ServerLogic
    public void display(String message){
        taMessages.append(message + "\n");
    }

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 2000;
        ServerGui s = new ServerGui(port);
    }
}
