package com.example.demo.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WriteThreadClient implements Runnable {
    private Thread thr;
    private SocketWrapper socketWrapper;
    private BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public WriteThreadClient(SocketWrapper socketWrapper) {
        this.socketWrapper = socketWrapper;
        this.thr = new Thread(this);
        thr.start();
    }

    public void sendCommand(String cmd) {
        queue.offer(cmd);
    }

    public void run() {
        try {
            while (true) {
                String cmd = queue.take();
                socketWrapper.write(cmd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

