package com.delgiudice.pokemonbattlefx.network;

import com.delgiudice.pokemonbattlefx.BattleApplication;
import com.delgiudice.pokemonbattlefx.teambuilder.TeamBuilderController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

// Handles connecting to the server
public class ClientThread extends NetworkThread{
    String host;

    public ClientThread(String host, int port, TeamBuilderController teamBuilderController) {
        super(port ,teamBuilderController);
        this.host = host;
    }

    @Override
    public void run() {
        BattleApplication.threadList.add(this);
        System.out.println("[INFO] Starting client thread...");
        System.out.println("[INFO] Connecting to: " + host + ":" + port);
        try {
            clientSocket = new Socket(host, port);
            clientSocket.setKeepAlive(true);
            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            System.out.println("[INFO] Connection successful: " + clientSocket.getInetAddress());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        teamBuilderController.setInputStream(dataInputStream);
        teamBuilderController.setOutputStream(dataOutputStream);
        try {
            teamBuilderController.sendBattleInfoClient();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // If thread receives signal to close connection, the loop is broken and all opened objects are closed
        waitForConnectionClose();

        System.out.println("[INFO] Closing streams and sockets...");

        try {
            closeObjects();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
