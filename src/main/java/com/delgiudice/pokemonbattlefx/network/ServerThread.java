package com.delgiudice.pokemonbattlefx.network;

import com.delgiudice.pokemonbattlefx.teambuilder.TeamBuilderController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread{

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private final TeamBuilderController teamBuilderController;

    boolean connectionOpen = true;

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    public ServerThread(ServerSocket serverSocket, TeamBuilderController teamBuilderController) {
        super();
        this.serverSocket = serverSocket;
        this.teamBuilderController = teamBuilderController;
    }

    public void closeConnection() {
        connectionOpen = false;
    }

    @Override
    public void run() {
        System.out.println("Starting server...");
        try {
            clientSocket = serverSocket.accept();
            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            System.out.println("Connection successful: " + clientSocket.getInetAddress());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            teamBuilderController.readBattleInfo(dataInputStream, dataOutputStream);
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
