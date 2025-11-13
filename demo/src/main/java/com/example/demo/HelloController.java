package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import static java.lang.System.exit;

public class HelloController {
    public Button start;
    @FXML
    Button startButton;
    @FXML
    Button exitButton;

    @FXML
    public void startOffline(){
        try {
            //play offline
            HelloApplication app = HelloApplication.getInstance();
            Stage stage = app.getStage();
            app.startGame(stage, GameMode.OFFLINE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void startOnline() {
        try{
            HelloApplication app = HelloApplication.getInstance();
            Stage stage = app.getStage();
            app.startGame(stage, GameMode.ONLINE);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void exitGame(){
        try{
            exit(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




}