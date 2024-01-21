package com.delgiudice.pokemonbattlefx.network;

import com.delgiudice.pokemonbattlefx.BattleApplication;
import com.delgiudice.pokemonbattlefx.teambuilder.TeamBuilderController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Handles connecting to the client.
 */
public class ServerThread extends NetworkThread{

    private ServerSocket serverSocket;

    public ServerThread(int port ,TeamBuilderController teamBuilderController) {
        super(port, teamBuilderController);
    }
    @Override
    public void run() {
        BattleApplication.threadList.add(this);
        System.out.println("[INFO] Starting server...");
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            System.out.println("[INFO] Connection successful: " + clientSocket.getInetAddress());
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

        // If thread receives signal to close connection, the loop is broken and all opened objects are closed
        waitForConnectionClose();

        System.out.println("[INFO] Closing streams and sockets...");

        try {
            closeObjects();
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
