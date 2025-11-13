package com.example.demo;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

import static com.example.demo.HelloApplication.groundY;

public class Tank {
    Image sprite;
    static final int MAX_HEALTH = 100;
    int health;
    double x;
    double angle = 45;
    final double width = 120;
    final double height = 60;
    final double turretLength = 50;
    Turret turret = new Turret();
    public double shotSpeed = 8.0;


    Tank(double x, String imagePath, boolean isMirror) {
        this.x = x;
        this.sprite = new Image("file:" + imagePath);
        if(isMirror) angle = 180-angle;
        health = 100;
    }

    void draw(GraphicsContext gc) {
        double y = groundY - height;
        gc.drawImage(sprite, x, y, width, height);

        turret.pivotX = x + width / 2;
        turret.pivotY = y;
        turret.angleRad = Math.toRadians(angle);
        turret.tipX = turret.pivotX + turretLength * Math.cos(turret.angleRad);
        turret.tipY = turret.pivotY - turretLength * Math.sin(turret.angleRad);

        turret.draw(gc);

        //health bar
        double barWidth = width;
        double healthRatio = (double) health / MAX_HEALTH;
        gc.setFill(Color.RED);
        gc.fillRect(x, groundY - height - 65, barWidth, 8); // background
        gc.setFill(Color.LIMEGREEN);
        gc.fillRect(x, groundY - height - 65, barWidth * healthRatio, 8);
    }
}
