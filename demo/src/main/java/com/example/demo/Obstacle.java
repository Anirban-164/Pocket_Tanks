package com.example.demo;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;


import java.awt.*;

import static com.example.demo.HelloApplication.*;

public class Obstacle {
    double width = 75;
    double height = 30;
    double x, y;
    double vy = 8;
    Image image = new Image("C:/Users/LENOVO/OneDrive - BUET/Project/image/obstacles/drone.png");

    Obstacle(){
        x = (canvasWidth-width)/2;
        y = (canvasHeight-height)/2;
    }

    public void update(){
        y += vy;
        if(y<=90 || y+height>=groundY) vy = -vy;
    }

    public void draw(GraphicsContext gc) {
        gc.drawImage(image, x, y, width, height);

    }

    public boolean checkCollision(double px, double py){
        if((px>=x && px<=(x+width)) && (py>=y && py<=(y+height))){
            y = 91;
            vy = (vy<0)? -vy : vy;
            if(vy<30) vy+=3;
            return true;
        }
        return false;
    }


}
