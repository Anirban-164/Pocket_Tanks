package com.example.demo;
import com.example.demo.util.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

public class HelloApplication extends Application {

    public static double canvasWidth = 1080, canvasHeight = 600;
    public static double groundY = 510; //road
    public static double gravity = 0.2;
    public static double tankSpeed = 2;

    public static Image background = new Image("/images/bg/war3.2.jpg");
    public static Tank player1 = new Tank(100, "/images/tanks/124-1m.png", false);
    public static Tank player2 = new Tank(canvasWidth - 200, "/images/tanks/124-1.png", true);
    public static int currentPlayer = 1; // 1 or 2

    InputManager inputManager = new InputManager();
    GameEngine gameEngine;
    private static HelloApplication instance;
    private Stage stage;

    static boolean gameOver = false;
    static String winnerText = "";

    private GameMode gameMode;
    SocketWrapper socketWrapper;
    ReadThreadClient readThreadClient;
    WriteThreadClient writeThreadClient;

    public static void setGameOver(String message){
        gameOver = true;
        winnerText = message;
        Platform.runLater(() -> {
            HelloApplication.getInstance().loadMainMenu();
        });
    }


    public static HelloApplication getInstance() {
        return instance;
    }

    public Stage getStage() {
        return stage;
    }


    @Override
    public void start(Stage stage) {
        instance = this;
        this.stage = stage;
        loadMainMenu();

    }

    public void loadMainMenu(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/hello-view.fxml"));
            Parent menuRoot = loader.load();
            Scene menuScene = new Scene(menuRoot);
            stage.setScene(menuScene);
            stage.setTitle("Main Menu");
            stage.show();
            gameOver = false;
            winnerText = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void startGame(Stage stage, GameMode mode) {
        gameMode = mode;
        gameEngine = new GameEngine(player1, player2, inputManager, gameMode);
        stage.setTitle("Pocket Tanks");

        Group root = new Group();
        Canvas canvas = new Canvas(canvasWidth, canvasHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        scene.setOnKeyPressed(e -> {
            if(gameOver && e.getCode() == KeyCode.R){ //restart if pressed R
                gameEngine.restartGame();
            }
            else{
                inputManager.press(e.getCode());
            }
        });
        scene.setOnKeyReleased(e -> inputManager.release(e.getCode()));

        if(gameMode == GameMode.ONLINE){
            setupNetwork();
        }

        new AnimationTimer() {
            public void handle(long now) {
                gameEngine.update();
                gameEngine.draw(gc);
            }
        }.start();
    }

    void setupNetwork() {
        try {
            socketWrapper = new SocketWrapper("127.0.0.1", 44444);
            writeThreadClient = new WriteThreadClient(socketWrapper);
            readThreadClient = new ReadThreadClient(socketWrapper, gameEngine);

            gameEngine.writeThread = writeThreadClient;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}