package com.example.demo;

import javafx.scene.input.KeyCode;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class InputManager {
    private final Set<KeyCode> currentlyPressed = new HashSet<>();
    private final Set<KeyCode> previouslyPressed = new HashSet<>();
    private final Queue<String> commandQueue = new LinkedList<>();

    public void press(KeyCode code) {
        currentlyPressed.add(code);
    }

    public void release(KeyCode code) {
        currentlyPressed.remove(code);
    }

    public boolean isPressed(KeyCode code) {
        return currentlyPressed.contains(code);
    }

    public boolean isJustPressed(KeyCode code) {
        return currentlyPressed.contains(code) && !previouslyPressed.contains(code);
    }

    public void update() {
        previouslyPressed.clear();
        previouslyPressed.addAll(currentlyPressed);
    }

    public void enqueueNetworkCommand(String cmd) {
        synchronized (commandQueue) {
            commandQueue.offer(cmd);
        }
    }

    public String getNextNetworkCommand() {
        synchronized (commandQueue) {
            return commandQueue.poll();
        }
    }

    public boolean hasPendingCommands() {
        synchronized (commandQueue) {
            return !commandQueue.isEmpty();
        }
    }
}
