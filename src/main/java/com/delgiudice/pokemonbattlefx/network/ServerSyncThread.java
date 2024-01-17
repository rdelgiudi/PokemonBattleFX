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
            if (inputStream.readUTF().equals("OK")) {
                outputStream.writeUTF("OK");
                outputStream.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Platform.runLater(runLater);
    }
}
