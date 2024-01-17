package com.delgiudice.pokemonbattlefx.network;

import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerSyncThread extends SyncThread {
    Runnable runLater;

    public ServerSyncThread(DataInputStream inputStream, DataOutputStream outputStream, Runnable runLater) {
        super(inputStream, outputStream);
        this.runLater = runLater;
    }

    @Override
    public void run() {
        try {
            inputStream.readUTF();
            outputStream.writeUTF("OK");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Platform.runLater(runLater);
    }
}
