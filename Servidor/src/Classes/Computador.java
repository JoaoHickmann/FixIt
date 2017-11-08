package Classes;

import java.io.Serializable;

public class Computador implements Serializable {
    private static final long serialVersionUID = 1001L;
    private int ID;
    private int Numero;
    private int Sala;

    public Computador(int Numero, int Sala) {
        this.Numero = Numero;
        this.Sala = Sala;
    }
    
    public Computador(int ID, int Numero, int Sala) {
        this.ID = ID;
        this.Numero = Numero;
        this.Sala = Sala;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getNumero() {
        return Numero;
    }

    public void setNumero(int Numero) {
        this.Numero = Numero;
    }

    public int getSala() {
        return Sala;
    }

    public void setSala(int Sala) {
        this.Sala = Sala;
    }
}