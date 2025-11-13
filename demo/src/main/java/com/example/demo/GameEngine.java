package com.example.demo;

import com.example.demo.util.GameState;
import com.example.demo.util.WriteThreadClient;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import static com.example.demo.HelloApplication.*;
import static com.example.demo.Tank.MAX_HEALTH;

public class GameEngine {
    Tank player1, player2;
    Projectile projectile;
    Obstacle obstacle;
    InputManager input;
    GameMode mode;
    private int clientPlayerId;
    private GameState gameState;
    WriteThreadClient writeThread;


    private static ArrayList<Explosion> explosions = new ArrayList<>();

    public GameEngine(Tank player1, Tank player2, InputManager input, GameMode mode) {
        this.player1 = player1;
        this.player2 = player2;
        this.input = input;
        projectile = new Projectile(this);
        obstacle = new Obstacle();
        this.mode = mode;
    }

    public void setGameState(GameState gameState){
        this.gameState = gameState;
    }
    public void setOpponentDisconnected(){
        gameOver = true;
        winnerText = "Opponent left. Press E to exit";
    }

    public void setClientPlayerId(int id) {
        this.clientPlayerId = id;
    }

    public void addExplosion(Explosion e){
        explosions.add(e);
    }

    public void update() {
        if(gameOver) return;

        Tank myTank, opponentTank;

        if(mode == GameMode.ONLINE){
            myTank = (clientPlayerId == 1) ? player1 : player2;
            opponentTank = (clientPlayerId == 1) ? player2 : player1;
        }
        else{
            myTank = (currentPlayer == 1) ? player1 : player2;
            opponentTank = (currentPlayer == 1) ? player2 : player1;
        }

        if(!projectile.active) {
            if(mode == GameMode.OFFLINE) {
                if(currentPlayer == 1) {
                    if(input.isPressed(KeyCode.A)) myTank.x -= tankSpeed;
                    if(input.isPressed(KeyCode.D)) myTank.x += tankSpeed;
                    if(input.isPressed(KeyCode.S)) myTank.angle = Math.max(0, myTank.angle - 1);
                    if(input.isPressed(KeyCode.W)) myTank.angle = Math.min(180, myTank.angle + 1);
                    if(input.isJustPressed(KeyCode.P)) myTank.shotSpeed += 0.25;
                    if(input.isJustPressed(KeyCode.L)) myTank.shotSpeed -= 0.25;
                    if(input.isPressed(KeyCode.F)){
                        projectile.launchFrom(myTank, opponentTank);
                        myTank.shotSpeed = 8;
                    }
                }

                else{
                    if(input.isPressed(KeyCode.LEFT)) myTank.x -= tankSpeed;
                    if(input.isPressed(KeyCode.RIGHT)) myTank.x += tankSpeed;
                    if(input.isPressed(KeyCode.DOWN)) myTank.angle = Math.max(0, myTank.angle - 1);
                    if(input.isPressed(KeyCode.UP)) myTank.angle = Math.min(180, myTank.angle + 1);
                    if(input.isJustPressed(KeyCode.NUMPAD8)) myTank.shotSpeed += 0.25;
                    if(input.isJustPressed(KeyCode.NUMPAD2)) myTank.shotSpeed -= 0.25;
                    if(input.isPressed(KeyCode.NUMPAD5)) {
                        projectile.launchFrom(myTank, opponentTank);
                        myTank.shotSpeed = 8;
                    }
                }
            }

            else if(mode == GameMode.ONLINE) {
                if(clientPlayerId == currentPlayer) { //send input if it's my turn

                    if(input.isPressed(KeyCode.A)) {
                        myTank.x -= tankSpeed;
                        sendCommand("LEFT");
                    }
                    if(input.isPressed(KeyCode.D)) {
                        myTank.x += tankSpeed;
                        sendCommand("RIGHT");
                    }
                    if(input.isPressed(KeyCode.S)) {
                        myTank.angle = Math.max(0, myTank.angle - 1);
                        sendCommand("ANGLE_DOWN");
                    }
                    if(input.isPressed(KeyCode.W)) {
                        myTank.angle = Math.min(180, myTank.angle + 1);
                        sendCommand("ANGLE_UP");
                    }
                    if(input.isPressed(KeyCode.F)) {
                        projectile.launchFrom(myTank, opponentTank);
                        sendCommand("FIRE");
                        myTank.shotSpeed = 8;
                    }
                    if(input.isJustPressed(KeyCode.P)){
                        myTank.shotSpeed += 0.25;
                        sendCommand("POWER_UP");
                    }
                    if(input.isJustPressed(KeyCode.L)){
                        myTank.shotSpeed -= 0.25;
                        sendCommand("POWER_DOWN");
                    }

                    writeThread.sendCommand("DRONE," + obstacle.x + "," + obstacle.y + "," + obstacle.vy);

                }

                //apply received commands to opponent tank
                while (input.hasPendingCommands()) {
                    String cmd = input.getNextNetworkCommand();
                    processOpponentCommand(cmd, opponentTank, myTank);
                }
            }

            //tank bounds
            player1.x = Math.max(0, Math.min(canvasWidth/2 - player1.width - obstacle.width/2, player1.x));
            player2.x = Math.max(canvasWidth/2 + obstacle.width/2, Math.min(canvasWidth - player2.width, player2.x));
        }

        obstacle.update();
        projectile.update(obstacle);

        explosions.removeIf(e -> {
            e.update();
            return e.isFinished();
        });
        input.update();
    }

    public InputManager getInputManager() {
        return input;
    }

    public void sendCommand(String cmd) {
        if(writeThread != null) {
            writeThread.sendCommand(cmd);
        }
    }

    public void processOpponentCommand(String cmd, Tank opponent, Tank me) {
        switch (cmd) {
            case "LEFT" -> opponent.x -= tankSpeed;
            case "RIGHT" -> opponent.x += tankSpeed;
            case "ANGLE_DOWN" -> opponent.angle = Math.max(0, opponent.angle - 1);
            case "ANGLE_UP" -> opponent.angle = Math.min(180, opponent.angle + 1);
            case "POWER_UP" -> opponent.shotSpeed += 0.25;
            case "POWER_DOWN" -> opponent.shotSpeed -= 0.25;
            case "FIRE" -> {
                if(!projectile.active) {
                    projectile.launchFrom(opponent, me);
                    opponent.shotSpeed = 8;
                }
            }
        }

        if(cmd.startsWith("DRONE")) {
            String[] parts = cmd.split(",");
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double vy = Double.parseDouble(parts[3]);

            obstacle.x = x;
            obstacle.y = y;
            obstacle.vy = vy;
        }

    }

    private void drawWaitingPeriod(GraphicsContext gc){
        gc.fillText("Waiting for Player 2 to Join the Match.........",canvasWidth/2-150,50);
    }

    private void drawScoreBoardOffline(GraphicsContext gc, int p1Health, int p2Health, int turn, double shotSpeed1, double shotSpeed2) {
        gc.fillText("Player 1 Health: " + p1Health,10,25);
        gc.fillText("Player 1 Score: " + (MAX_HEALTH-p2Health),10,50);


        gc.fillText("Player 2 Health: " + p2Health,canvasWidth-200,25);
        gc.fillText("Player 2 Score: " + (MAX_HEALTH-p1Health),canvasWidth-200,50);

        if(turn == 1){
            gc.fillText("<< Your Turn",canvasWidth/2 - 40,50);
            gc.fillText("ShotSpeed: " + shotSpeed1,10,75);
        }
        else if(turn == 2){
            gc.fillText("Your Turn >>",canvasWidth/2 - 40,50);
            gc.fillText("shotSpeed: " + shotSpeed2, canvasWidth-200,75);
        }
    }

    private void drawScoreBoardOnline(GraphicsContext gc, int p1Health, int p2Health, int currentPlayerId, int turn, double shotSpeed1, double shotSpeed2){
        gc.fillText("Player 1 Health: " + p1Health,10,25);
        gc.fillText("Player 1 Score: " + (MAX_HEALTH - p2Health),10,50);

        gc.fillText("Player 2 Health: " + p2Health,(canvasWidth-200),25);
        gc.fillText("Player 2 Score: " + (MAX_HEALTH - p1Health),canvasWidth-200,50);

        if(currentPlayerId == turn){
            if(currentPlayer == 1){
                gc.fillText("Your Turn" ,canvasWidth/2-40,50);
                gc.fillText("Shot Power: " + shotSpeed1,10,75);
            }
            else{
                gc.fillText("Your Turn" ,canvasWidth/2-40,50);
                gc.fillText("Shot Power: " + shotSpeed2,canvasWidth-200,75);
            }
        }

        else{
            gc.fillText("Opponent's Turn" ,canvasWidth/2-50,50);
        }
    }

    public void draw(GraphicsContext gc) {
        gc.clearRect(0, 0, canvasWidth, canvasHeight);
        gc.drawImage(background, 0, 0, canvasWidth, canvasHeight);

        player1.draw(gc);
        player2.draw(gc);
        obstacle.draw(gc);
        projectile.draw(gc);

        for(Explosion e: explosions){
            e.draw(gc);
        }

        gc.setFill(Color.rgb(0, 0, 0, 0.6));
        gc.fillRect(0, 0, canvasWidth, 90);

        //scoreboard
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 18));

        if(mode == GameMode.ONLINE){
            if(gameState == null){
                drawWaitingPeriod(gc);
            }
            else{
                drawScoreBoardOnline(gc, player1.health, player2.health, clientPlayerId, currentPlayer,player1.shotSpeed,player2.shotSpeed);
            }
        }
        else if(mode == GameMode.OFFLINE){
            drawScoreBoardOffline(gc, player1.health, player2.health, currentPlayer, player1.shotSpeed, player2.shotSpeed);
        }

        if(gameOver){
            gc.setFill(Color.rgb(0, 0, 0, 0.6));
            gc.fillRect(0, 0, canvasWidth, canvasHeight);

            gc.setFill(Color.WHITE);
            gc.setFont(new Font("Arial", 36));
            gc.fillText(winnerText, canvasWidth / 2 - 150, canvasHeight / 2);

            gc.setFont(new Font("Arial", 20));
            gc.fillText("Press R to restart", canvasWidth / 2 - 100, canvasHeight / 2 + 50);
        }
    }

    public static void switchPlayer() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }

    public void restartGame() {
        player1.health = MAX_HEALTH;
        player2.health = MAX_HEALTH;
        projectile.active = false;

        obstacle.y = 91;
        obstacle.vy = 8;
        gameOver = false;
        winnerText = "";
        currentPlayer = 1;

        // reposition tanks
        player1.x = 100;
        player2.x = canvasWidth - 200;
    }
}
