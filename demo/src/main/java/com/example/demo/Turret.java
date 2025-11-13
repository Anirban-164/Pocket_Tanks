package com.example.demo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Turret {
    double pivotX;
    double pivotY;
    double angleRad;
    double tipX;
    double tipY ;

    public Turret(){}

    public void draw(GraphicsContext gc){
        gc.setStroke(Color.GOLD);
        gc.setLineWidth(5);
        gc.strokeLine(pivotX, pivotY, tipX, tipY);
    }
}
