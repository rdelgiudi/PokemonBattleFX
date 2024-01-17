package com.delgiudice.pokemonbattlefx.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class SyncThread extends Thread{
    protected DataInputStream inputStream;
    protected DataOutputStream outputStream;

    public SyncThread(DataInputStream inputStream, DataOutputStream outputStream) {
        super();
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }
}
