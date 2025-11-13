package com.example.demo;
import static com.example.demo.HelloApplication.*;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

public class Projectile {
    double x, y, vx, vy;
    boolean active = false;
    Tank owner;
    Tank opponent;
    GameEngine gameEngine;

    Projectile(GameEngine ge){
        gameEngine = ge;
    }


    void launchFrom(Tank shooter, Tank enemy) { //launching point
        this.owner = shooter;
        this.opponent = enemy;

        x = shooter.turret.pivotX + shooter.turretLength * Math.cos(shooter.turret.angleRad);
        y = shooter.turret.pivotY - shooter.turretLength * Math.sin(shooter.turret.angleRad);

        vx = shooter.shotSpeed * Math.cos(shooter.turret.angleRad); //velocity in x-axis
        vy = -shooter.shotSpeed * Math.sin(shooter.turret.angleRad);
        active = true;
    }

    public boolean selfHit(){
        //self-hit
        double dx1 = x - (owner.x + owner.width / 2);
        double dy1 = y - (groundY - owner.height / 2);
        double dist1 = Math.sqrt(dx1 * dx1 + dy1 * dy1);
        double explosionRadius = 75;

        if(dist1 <= explosionRadius) return true;
        return false;
    }

    void update(Obstacle obstacle) {
        if(!active) return;

        x += vx;
        y += vy;
        vy += gravity;

        if(obstacle.checkCollision(x, y)) {
            if(owner.health<=90) owner.health += 10;
            active = false;
            gameEngine.switchPlayer();
            gameEngine.addExplosion(new Explosion(x,y));
            return;
        }

        if(y >= groundY - 40) {
            double explosionRadius = 75;

            if(selfHit()){
                owner.health -= 25;
                System.out.println("Self-hit! Owner health: " + owner.health);
            }

            // Damage to opponent
            double dx2 = x - (opponent.x + opponent.width / 2);
            double dy2 = y - (groundY - opponent.height / 2);
            double dist2 = Math.sqrt(dx2 * dx2 + dy2 * dy2);

            if(dist2 <= explosionRadius) {
                opponent.health -= 25;
                System.out.println("Opponent hit! Opponent health: " + opponent.health);
            }

            gameEngine.addExplosion(new Explosion(x,y));

            if(owner.health <= 0) {
                HelloApplication.setGameOver("Player " + (HelloApplication.currentPlayer == 1 ? 1 : 2) + " loses!");
                return;
            }

            if(opponent.health <= 0) {
                HelloApplication.setGameOver("Player " + (HelloApplication.currentPlayer == 2 ? 2 : 1) + " wins!");
                return;
            }

            active = false;
            gameEngine.switchPlayer();
        }


        // Optional off-screen fail-safe
        if(x < 0 || x > canvasWidth || y > canvasHeight) {
            active = false;
            gameEngine.addExplosion(new Explosion(x,y));
            gameEngine.switchPlayer();
        }
    }


    void draw(GraphicsContext gc) {
        if(active) {
            gc.setFill(Color.RED);
            gc.fillOval(x - 5, y - 5, 10, 10);
        }
    }
}
