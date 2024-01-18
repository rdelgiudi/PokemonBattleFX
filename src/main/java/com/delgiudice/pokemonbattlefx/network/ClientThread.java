package com.delgiudice.pokemonbattlefx.network;

import com.delgiudice.pokemonbattlefx.BattleApplication;
import com.delgiudice.pokemonbattlefx.teambuilder.TeamBuilderController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientThread extends NetworkThread{
    String host;

    public ClientThread(String host, int port, TeamBuilderController teamBuilderController) {
        super(port ,teamBuilderController);
        this.host = host;
    }

    @Override
    public void run() {
        BattleApplication.threadList.add(this);
        int port = 1234;
        System.out.println("Starting client thread...");
        System.out.println("Connecting to: " + host + ":" + port);
        try {
            clientSocket = new Socket(host, port);
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
            teamBuilderController.sendBattleInfoClient();
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
