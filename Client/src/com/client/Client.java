package com.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Client extends JFrame implements ActionListener {

    private JTextArea   taMessages = null;
    private JTextField  taInput    = null;
    private ChatLogic   client     = null;
    private int         port       = 0;
    private String      host       = null;


    public Client(String host, int port){

        //create area for messages
        taMessages = new JTextArea();
        taMessages.setRows(10);
        taMessages.setColumns(50);
        taMessages.setEditable(false);

        JScrollPane spMessages = new JScrollPane(taMessages, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(spMessages,"Center");

        // Create area for input
        taInput  = new JTextField();
        taInput.setColumns(50);
        taInput.addActionListener(this);

        // Create panel and add content
        JPanel bp = new JPanel( new FlowLayout());
        bp.add(taInput);

        add(bp,"South");
        setSize(500,300);

        setVisible(true);
        pack();


        addWindowListener(
                new WindowAdapter() {

                    @Override
                    public void windowClosing(WindowEvent e) {
                        client.send("exit");

                    }
                }
        );

        client = new ChatLogic(host,port,this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //Enter button is pressed from taInput
        client.send(taInput.getText());
        taInput.setText("");
    }

    public static void main(String[] args) {
        // Check if hostname is specified by input, else use 127.0.0.1
        String hostName = args.length > 0 ? args[0] : "localhost";
        // Check if port is specified by input, else use 2000
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 2000;
        Client chatGui = new Client(hostName, port);
    }


    // Called from the ChatClient
    public void display(String message){
        taMessages.append(message + "\n");
    }


}
