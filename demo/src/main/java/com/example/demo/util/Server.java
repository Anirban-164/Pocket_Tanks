package com.example.demo.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    int playerCount = 0;
    private ServerSocket serverSocket;
    public HashMap<Integer, SocketWrapper> clientMap;
    public GameState gameState = new GameState(100,100,1);

    public Server() {
        clientMap = new HashMap<>();
        try {
            serverSocket = new ServerSocket(44444);
            System.out.println("Server started. Waiting for players...");
            while (playerCount < 2) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("A client joined");
                serve(clientSocket);
            }
            broadcastGameState();
        } catch (Exception e) {
            System.out.println("Server starts:" + e);
        }
    }

    public void broadcastGameState(){
        for(SocketWrapper client : clientMap.values()){
            try{
                client.write(gameState);
            }catch(Exception e){
                System.out.println("Failed to send gameState: " + e);
            }
        }
    }

    public void serve(Socket clientSocket) throws IOException {
        SocketWrapper socketWrapper = new SocketWrapper(clientSocket);
        socketWrapper.write(++playerCount);

        clientMap.put(playerCount, socketWrapper);
        new ReadThreadServer(this,clientMap, socketWrapper, playerCount);
    }


    public static void main(String args[]) {
        new Server();
    }
}
