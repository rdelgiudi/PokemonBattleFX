package com.delgiudice.pokemonbattlefx.network;

import com.delgiudice.pokemonbattlefx.teambuilder.TeamBuilderController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Base class of threads that handle network connection during multiplayer games.
 */
abstract public class NetworkThread extends Thread{

    protected Socket clientSocket;
    protected DataOutputStream dataOutputStream;
    protected DataInputStream dataInputStream;
    protected final TeamBuilderController teamBuilderController;
    protected boolean connectionOpen = true;
    protected int port;

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    public NetworkThread(int port, TeamBuilderController teamBuilderController) {
        super();
        this.teamBuilderController = teamBuilderController;
        this.port = port;
    }

    public void closeConnection() {
        connectionOpen = false;
    }

    protected void closeObjects() throws IOException {
        dataInputStream.close();
        dataOutputStream.close();
        clientSocket.close();
    }

    protected void waitForConnectionClose() {
        while (connectionOpen) {
            try {
                sleep(500);
            } catch (InterruptedException e) {
                break;
            }
            if (clientSocket.isClosed()) {
                System.out.println("[ERROR] Unexpected connection closure!");
                break;
            }
        }
    }
}
