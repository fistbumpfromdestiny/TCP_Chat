package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    static final private int port = 33333;

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(port);

           try{
                while(true) {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clientHandler.start();
                }
           } catch (IOException e) {
               e.printStackTrace();
        }
    }
}
