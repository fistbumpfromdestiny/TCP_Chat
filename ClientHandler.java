package com.company;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler extends Thread {

    static private final ArrayList<ClientHandler> clients = new ArrayList<>();

    private Socket clientSocket;

    private String userName;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    Protocol protocol;


    public ClientHandler(Socket clientSocket) throws IOException {
       try {
           this.clientSocket = clientSocket;
           ois = new ObjectInputStream(clientSocket.getInputStream());
           oos = new ObjectOutputStream(clientSocket.getOutputStream());
           userName = (String)ois.readObject();
           protocol = new Protocol();
           clients.add(this);

       } catch (IOException | ClassNotFoundException e) {
           closeConnections(clientSocket, oos, ois);
       }

    }

    public void sendMessage(Object chatMessage) throws IOException {
        if(chatMessage != null) {
        for(ClientHandler c: clients) {
            c.oos.writeObject(chatMessage);
        }
    }
}

    @Override
    public void run() {
        try {
            oos.writeObject(protocol.inputHandler(userName,""));
            sendMessage(protocol.inputHandler(userName, " har anslutit till chatten."));
        } catch (IOException e) {
            closeConnections(clientSocket, oos, ois);
        }

        Object fromClient;

        while(clientSocket.isConnected()) {

            try {
                fromClient = ois.readObject();
                sendMessage(protocol.inputHandler(userName, (String) fromClient));
            } catch (IOException | ClassNotFoundException e) {
                closeConnections(clientSocket, oos, ois);
                break;
            }
        }
    }


    public void removeClient() throws IOException {
        sendMessage(protocol.inputHandler(userName, " har kopplat ner."));
        clients.remove(this);

    }


    public void closeConnections(Socket clientSocket, ObjectOutputStream oos, ObjectInputStream ois) {
        try {
            if(clientSocket != null) {
                clientSocket.close();
            }
            if(oos != null) {
                oos.close();
            }
            if(ois != null) {
                ois.close();
            }
            removeClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
