package com.example.demo.util;

import com.example.demo.GameEngine;
import javafx.application.Platform;

import java.io.IOException;

public class ReadThreadClient implements Runnable {
    private Thread thr;
    private SocketWrapper socketWrapper;
    private GameEngine engine;

    public ReadThreadClient(SocketWrapper socketWrapper,GameEngine engine) {
        this.socketWrapper = socketWrapper;
        this.engine = engine;
        this.thr = new Thread(this);
        thr.start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object o = socketWrapper.read();
                if (o instanceof Integer) {
                    engine.setClientPlayerId((int) o);  // Store the player ID
                }
                else if (o instanceof String) {
                    String cmd = (String) o;
                    System.out.println("Received command: " + cmd);
                    engine.getInputManager().enqueueNetworkCommand(cmd);
                }
                else if(o instanceof GameState){
                    GameState gs = (GameState) o;
                    Platform.runLater(()->{
                        engine.setGameState(gs);
                        if (gs.opponentDisconnected) {
                            engine.setOpponentDisconnected();
                        }
                    });
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
