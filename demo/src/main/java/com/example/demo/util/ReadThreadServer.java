package com.example.demo.util;

import com.example.demo.HelloApplication;
import javafx.application.Platform;

import java.util.HashMap;

public class ReadThreadServer implements Runnable {
    private Thread thr;
    private HashMap<Integer, SocketWrapper> clientMap;
    private SocketWrapper socketWrapper;
    private int clientId;
    private Server server;

    public ReadThreadServer(Server server,HashMap<Integer, SocketWrapper> clientMap, SocketWrapper socketWrapper, int clientId) {
        this.server = server;
        this.clientMap = clientMap;
        this.socketWrapper = socketWrapper;
        this.clientId = clientId;  // Use the explicit client ID
        this.thr = new Thread(this);
        thr.start();
    }


    public void run() {
        try {
            while (true) {
                Object obj = socketWrapper.read();
                if(obj == null){
                    // Connection closed
                    notifyOpponentLeft();
                    break;
                }

                if (obj instanceof String cmd) {
                    String[] parts = cmd.split(" ");
                    if(parts[0].equals("HIT") && parts.length == 3){
                        int playerId = Integer.parseInt(parts[1]);
                        int damage = Integer.parseInt(parts[2]);

                        synchronized (server){
                            if(playerId == 1){
                                server.gameState.player1Health = Math.max(0,server.gameState.player1Health - damage);
                                server.gameState.currentPlayer = 2;
                            }else if(playerId == 2){
                                server.gameState.player2Health = Math.max(0,server.gameState.player2Health - damage);
                                server.gameState.currentPlayer = 1;
                            }
                            server.broadcastGameState();
                        }
                    }

                    int toClientId = (clientId == 1) ? 2 : 1;
                    SocketWrapper otherClient = clientMap.get(toClientId);
                    if(otherClient != null) {
                        otherClient.write(cmd);
                    } else {
                        System.out.println("Other client not connected yet.");
                    }
                } else {
                    System.out.println("Unknown object type received: " + obj.getClass());
                }
            }

        } catch (Exception e) {
            System.out.println("Client " + clientId + " disconnected with exception: " + e);

            notifyOpponentLeft();
            clientMap.remove(clientId);
        }
    }


    private void notifyOpponentLeft() {
        System.out.println("===> notifyOpponentLeft() called");

        int toClientId = (clientId == 1) ? 2 : 1;
        SocketWrapper otherClient = clientMap.get(toClientId);

        synchronized (server) {
            GameState gs = new GameState(
                    server.gameState.player1Health,
                    server.gameState.player2Health,
                    server.gameState.currentPlayer
            );
            gs.opponentDisconnected = true;

            try {
                if (otherClient != null) {
                    otherClient.write(gs);
                    System.out.println("Notified opponent about disconnection.");
                } else {
                    System.out.println("No opponent to notify.");
                }
            } catch (Exception ex) {
                System.out.println("Failed to notify opponent: " + ex.getMessage());
            }
        }
    }


    private void handleOpponentLeft() {
        System.out.println("Connection lost or opponent left detected.");
        Platform.runLater(() -> {
            HelloApplication.setGameOver("Opponent left. Press E to exit");
        });
    }

}