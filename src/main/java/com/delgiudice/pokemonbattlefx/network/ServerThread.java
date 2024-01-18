package com.delgiudice.pokemonbattlefx.network;

import com.delgiudice.pokemonbattlefx.BattleApplication;
import com.delgiudice.pokemonbattlefx.teambuilder.TeamBuilderController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends NetworkThread{

    private ServerSocket serverSocket;

    public ServerThread(int port ,TeamBuilderController teamBuilderController) {
        super(port, teamBuilderController);
    }
    @Override
    public void run() {
        BattleApplication.threadList.add(this);
        System.out.println("Starting server...");
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            clientSocket.setKeepAlive(true);
            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            System.out.println("Connection successful: " + clientSocket.getInetAddress());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        teamBuilderController.setInputStream(dataInputStream);
        teamBuilderController.setOutputStream(dataOutputStream);
        try {
            teamBuilderController.readBattleInfoServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (connectionOpen) {
            try {
                Thread.sleep(50);
                if (clientSocket.isClosed()) {
                    System.out.println("Unexpected connection closure!");
                    break;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("Closing streams and sockets...");

        try {
            dataInputStream.close();
            dataOutputStream.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
