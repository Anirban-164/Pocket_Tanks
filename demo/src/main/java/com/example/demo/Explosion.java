package com.example.demo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class Explosion {
    private double x, y;
    private int currentFrame = 0;
    private int frameDelay = 5; // ticks between frames
    private int delayCounter = 0;
    private boolean finished = false;

    private static ArrayList<Image> frames = new ArrayList<>();

    public Explosion(double x, double y) {
        this.x = x;
        this.y = y;

        if (frames.isEmpty()) {
            for (int i = 0; i < 6; i++) { // change 8 to the number of frames you have
                frames.add(new Image("/images/explosion (1)/" + i + ".png"));
            }
        }
    }

    public void update() {
        if (finished) return;

        delayCounter++;
        if (delayCounter >= frameDelay) {
            delayCounter = 0;
            currentFrame++;
            if (currentFrame >= frames.size()) {
                finished = true;
            }
        }
    }

    public void draw(GraphicsContext gc) {
        if (!finished && currentFrame < frames.size()) {
            Image frame = frames.get(currentFrame);
            gc.drawImage(frame, x - frame.getWidth() / 2, y - frame.getHeight() / 2, 60, 85);
        }
    }

    public boolean isFinished() {
        return finished;
    }
}
