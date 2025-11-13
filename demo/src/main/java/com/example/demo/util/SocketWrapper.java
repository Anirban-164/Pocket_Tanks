package com.example.demo.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketWrapper {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public SocketWrapper(String host, int port) throws IOException { // Client
        this.socket = new Socket(host, port);
        this.oos = new ObjectOutputStream(socket.getOutputStream());
        this.ois = new ObjectInputStream(socket.getInputStream());
    }

    public SocketWrapper(Socket socket) throws IOException { // Server
        this.socket = socket;
        this.oos = new ObjectOutputStream(socket.getOutputStream());
        this.ois = new ObjectInputStream(socket.getInputStream());
    }

    public Object read() throws IOException, ClassNotFoundException {
        return ois.readObject();
    }

    public void write(Object o) throws IOException {
        oos.writeObject(o);
        oos.flush();
        oos.reset();
    }

    public void closeConnection() throws IOException {
        ois.close();
        oos.close();
        socket.close();
    }
}
