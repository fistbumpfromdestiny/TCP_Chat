package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class Client extends Thread implements ActionListener {

    // GUI variables
    final private JButton button;
    final private JTextField textField;
    protected JTextArea textArea = new JTextArea(60, 30);


    private final String userName;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Socket socket;

    public Client(Socket socket) {

        userName = JOptionPane.showInputDialog(null, "Ange nick: ");
        if(userName == null || userName.length() == 0) {
            System.exit(0);
        }

        // Setup for chat application's GUI
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(200, 450));

        frame.add(panel);

        button = new JButton("Koppla ner.");
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(button);
        button.addActionListener(this);

        textArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(textArea);

        textField = new JTextField(30);
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(textField);
        textField.addActionListener(this);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();

        try {
            this.socket = socket;

            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            closeConnections(socket, oos, ois);
        }

    }

    public void receiveMessage() {
        new Thread(() -> {
            Object fromServer;
            try{
                oos.writeObject(userName);
                while((fromServer = ois.readObject()) != null){
                    if(fromServer instanceof ConnectionEstablisher) {

                       textArea.append("Uppkopplad mot servern.\n");
                    }
                    else if(fromServer instanceof String) {
                        textArea.append(fromServer +"\n");
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                closeConnections(socket, oos, ois);
            }

        }).start();
    }

    public void sendMessage(String toSend) {

        try {
              oos.writeObject(toSend);
            } catch (IOException ex) {
            closeConnections(socket, oos, ois);
        }
        }

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("127.0.0.1", 33333);
        Client client = new Client(socket);

        client.receiveMessage();
        //while(true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(actionEvent.getSource() == button) {
            closeConnections(socket, oos, ois);
        }
         if(actionEvent.getSource() == textField) {
            if(textField.getText().trim().length() != 0) {
                textArea.append("Du: "+textField.getText()+"\n");
                sendMessage(textField.getText());
                textField.setText("");
            }
        }
    }

    private void closeConnections(Socket socket, ObjectOutputStream oos, ObjectInputStream ois) {

        try {
            if(socket != null) {
                socket.close();
            }
            if(oos != null) {
                oos.close();
            }
            if(ois != null) {
                ois.close();
            }
            textArea.append("Du kopplade ner.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
