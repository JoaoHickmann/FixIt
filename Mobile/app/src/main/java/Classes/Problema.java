package Classes;

import java.io.Serializable;

public class Problema implements Serializable {
    private static final long serialVersionUID = 1002L;
    private int ID;
    private String Descricao;
    private int Tipo;
    
    public Problema (int ID, String Descricao, int Tipo) {
        this.ID = ID;
        this.Descricao = Descricao;
        this.Tipo = Tipo;
    }
    
    public Problema (String Descricao, int Tipo) {
        this.Descricao = Descricao;
        this.Tipo = Tipo;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String Descricao) {
        this.Descricao = Descricao;
    }

    public int getTipo() {
        return Tipo;
    }

    public void setTipo(int Tipo) {
        this.Tipo = Tipo;
    }
}