package com.delgiudice.pokemonbattlefx.network;

import com.delgiudice.pokemonbattlefx.teambuilder.TeamBuilderController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

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

    public void closeConnection() {
        connectionOpen = false;
    }

    public NetworkThread(int port, TeamBuilderController teamBuilderController) {
        super();
        this.teamBuilderController = teamBuilderController;
        this.port = port;
    }
}
