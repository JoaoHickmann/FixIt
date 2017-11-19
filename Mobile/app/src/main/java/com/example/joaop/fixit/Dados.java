package com.example.joaop.fixit;

import android.app.Application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

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

    public Object obterLista(String operacao) {
        try {
            out.writeObject(operacao);
            return in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Object realizarOperacao(String operacao, Object obj) {
        try {
            out.writeObject(operacao);
            in.readObject();
            out.reset();
            out.writeObject(obj);
            return in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}