package com.delgiudice.pokemonbattlefx.network;

import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientSyncThread extends SyncThread {
    Runnable runLater;

    public ClientSyncThread(DataInputStream inputStream, DataOutputStream outputStream, Runnable runLater) {
        super(inputStream, outputStream);
        this.runLater = runLater;
    }

    @Override
    public void run() {
        try {
            outputStream.writeUTF("OK");
            outputStream.flush();
            if (inputStream.readUTF().equals("OK"))
                Platform.runLater(runLater);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
