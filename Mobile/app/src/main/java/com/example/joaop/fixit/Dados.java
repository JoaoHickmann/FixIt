package com.example.joaop.fixit;

import android.app.Application;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Classes.Usuario;

public class Dados extends Application {
    private Socket servidor;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Usuario user;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public Socket getServidor() {
        return servidor;
    }

    public void setServidor(Socket servidor) {
        this.servidor = servidor;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public void setIn(ObjectInputStream in) {
        this.in = in;
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }
}