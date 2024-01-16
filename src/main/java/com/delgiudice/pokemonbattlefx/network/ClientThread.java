package com.delgiudice.pokemonbattlefx.network;

import com.delgiudice.pokemonbattlefx.teambuilder.TeamBuilderController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientThread extends Thread{
    private Socket clientSocket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private final TeamBuilderController teamBuilderController;
    String host;

    boolean connectionOpen = true;

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    public ClientThread(String host, TeamBuilderController teamBuilderController) {
        super();
        this.host = host;
        this.teamBuilderController = teamBuilderController;
    }

    public void closeConnection() {
        connectionOpen = false;
    }

    @Override
    public void run() {
        int port = 1234;
        System.out.println("Starting client thread...");
        System.out.println("Connecting to: " + host + ":" + port);
        try {
            clientSocket = new Socket(host, port);
            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            System.out.println("Connection successful: " + clientSocket.getInetAddress());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            teamBuilderController.sendBattleInfo(dataInputStream, dataOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (connectionOpen) {
            try {
                Thread.sleep(100);
                if (!clientSocket.isConnected())
                    break;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            dataInputStream.close();
            dataOutputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
