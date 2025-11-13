package com.example.demo.util;
import java.io.Serializable;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    public int player1Health;
    public int player2Health;
    public int currentPlayer;
    public boolean opponentDisconnected = false;

    public GameState(int p1Health, int p2Health, int currentPlayer){
        this.player1Health = p1Health;
        this.player2Health = p2Health;
        this.currentPlayer = currentPlayer;
    }
}
