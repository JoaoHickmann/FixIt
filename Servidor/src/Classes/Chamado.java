package Classes;

import java.io.Serializable;
import java.util.Date;

public class Chamado implements Serializable{
    private static final long serialVersionUID = 1000L;
    private int id_chamado;
    private int id_usuario;
    private String nome_usuario;
    private int id_administrador;
    private String nome_administrador;
    private Date data_inicial;
    private Date data_final;
    private Computador computador;
    private Problema problema;
    private String observacao;
    private int status;
    
    public Chamado (int ID_Chamado, String Nome_Usuario, String Nome_Administrador, Date Data_Inicial, Date Data_Final, Computador computador, Problema problema, String observacao, int status) {
        this.id_chamado = ID_Chamado;
        this.nome_usuario = Nome_Usuario;
        this.nome_administrador = Nome_Administrador;
        this.data_inicial = Data_Inicial;
        this.data_final = Data_Final;
        this.computador = computador;
        this.problema = problema;
        this.observacao = observacao;
        this.status = status;
    }
    
    public Chamado (Computador computador, Problema problema, String observacao) {
        this.computador = computador;
        this.problema = problema;
        this.observacao = observacao;
    }

    public int getID_Chamado() {
        return id_chamado;
    }

    public void setID_Chamado(int ID_Chamado) {
        this.id_chamado = ID_Chamado;
    }

    public int getID_Usuario() {
        return id_usuario;
    }

    public void setID_Usuario(int ID_Usuario) {
        this.id_usuario = ID_Usuario;
    }

    public String getNome_Usuario() {
        return nome_usuario;
    }

    public void setNome_Usuario(String Nome_Usuario) {
        this.nome_usuario = Nome_Usuario;
    }

    public int getID_Administrador() {
        return id_administrador;
    }

    public void setID_Administrador(int ID_Administrador) {
        this.id_administrador = ID_Administrador;
    }

    public String getNome_Administrador() {
        return nome_administrador;
    }

    public void setNome_Administrador(String Nome_Administrador) {
        this.nome_administrador = Nome_Administrador;
    }

    public Date getData_Inicial() {
        return data_inicial;
    }

    public void setData_Inicial(Date Data_Inicial) {
        this.data_inicial = Data_Inicial;
    }

    public Date getData_Final() {
        return data_final;
    }

    public void setData_Final(Date Data_Final) {
        this.data_final = Data_Final;
    }

    public Computador getComputador() {
        return computador;
    }

    public void setComputador(Computador computador) {
        this.computador = computador;
    }

    public Problema getProblema() {
        return problema;
    }

    public void setProblema(Problema problema) {
        this.problema = problema;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String Observacao) {
        this.observacao = Observacao;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int Status) {
        this.status = Status;
    }

}