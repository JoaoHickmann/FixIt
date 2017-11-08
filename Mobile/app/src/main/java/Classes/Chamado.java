package Classes;

import java.io.Serializable;

public class Chamado implements Serializable{
    private static final long serialVersionUID = 1000L;
    private int ID_Chamado;
    private int ID_Usuario;
    private String Nome_Usuario;
    private int ID_Administrador;
    private String Nome_Administrador;
    private String Data_Inicial;
    private String Data_Final;
    private int Computador;
    private int Sala;
    private int Tipo_Problema;
    private int ID_Problema;
    private String Problema;
    private String Observacao;
    private int Status;
    
    public Chamado (int ID_Chamado, String Nome_Usuario, String Data_Inicial, int Computador, int Sala, int Tipo_Problema, String Problema, String Observacao, int Status) {
        this.ID_Chamado = ID_Chamado;
        this.Nome_Usuario = Nome_Usuario;
        this.Data_Inicial = Data_Inicial;
        this.Computador = Computador;
        this.Sala = Sala;
        this.Tipo_Problema = Tipo_Problema;
        this.Problema = Problema;
        this.Observacao = Observacao;
        this.Status = Status;
    }

    public int getID_Chamado() {
        return ID_Chamado;
    }

    public String getNome_Usuario() {
        return Nome_Usuario;
    }

    public String getNome_Administrador() {
        return Nome_Administrador;
    }

    public void setNome_Administrador(String Nome_Administrador) {
        this.Nome_Administrador = Nome_Administrador;
    }

    public String getData_Inicial() {
        return Data_Inicial;
    }

    public void setData_Inicial(String Data_Inicial) {
        this.Data_Inicial = Data_Inicial;
    }

    public String getData_Final() {
        return Data_Final;
    }

    public void setData_Final(String Data_Final) {
        this.Data_Final = Data_Final;
    }

    public int getComputador() {
        return Computador;
    }

    public void setComputador(int Computador) {
        this.Computador = Computador;
    }

    public int getSala() {
        return Sala;
    }

    public void setSala(int Sala) {
        this.Sala = Sala;
    }

    public String getProblema() {
        return Problema;
    }

    public void setProblema(String Problema) {
        this.Problema = Problema;
    }

    public String getObservacao() {
        return Observacao;
    }

    public void setObservacao(String Observacao) {
        this.Observacao = Observacao;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        this.Status = status;
    }

    public int getID_Usuario() {
        return ID_Usuario;
    }

    public void setID_Usuario(int ID_Usuario) {
        this.ID_Usuario = ID_Usuario;
    }

    public int getID_Administrador() {
        return ID_Administrador;
    }

    public void setID_Administrador(int ID_Administrador) {
        this.ID_Administrador = ID_Administrador;
    }

    public void setID_Chamado(int ID_Chamada) {
        this.ID_Chamado = ID_Chamada;
    }

    public void setNome_Usuario(String Nome_Usuario) {
        this.Nome_Usuario = Nome_Usuario;
    }

    public int getTipo_Problema() {
        return Tipo_Problema;
    }

    public void setTipo_Problema(int Tipo_Problema) {
        this.Tipo_Problema = Tipo_Problema;
    }

    public int getID_Problema() {
        return ID_Problema;
    }

    public void setID_Problema(int ID_Problema) {
        this.ID_Problema = ID_Problema;
    }
}