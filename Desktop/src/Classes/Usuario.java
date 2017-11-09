package Classes;

import java.io.Serializable;

public class Usuario implements Serializable{
    private static final long serialVersionUID = 1004L;
    private int ID_Usuario;
    private String Nome;
    private String Email;
    private String Senha;
    private boolean Administrador;
    
    public Usuario(String Nome, String Email, String Senha, boolean isAdministrador){
        this.Nome = Nome;
        this.Email = Email;
        this.Senha = Senha;
        this.Administrador = isAdministrador;
    }
    
    public Usuario(int ID_Usuario, String Nome, String Email, String Senha, boolean isAdministrador){
        this.ID_Usuario = ID_Usuario;
        this.Nome = Nome;
        this.Email = Email;
        this.Senha = Senha;
        this.Administrador = isAdministrador;
    }
    
    public Usuario(String Email, String Senha) {
        this.Email = Email;
        this.Senha = Senha;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String Nome) {
        this.Nome = Nome;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getSenha() {
        return Senha;
    }

    public void setSenha(String Senha) {
        this.Senha = Senha;
    }

    public boolean isAdministrador() {
        return Administrador;
    }

    public void setAdministrador(boolean isAdministrador) {
        this.Administrador = isAdministrador;
    }

    public int getID_Usuario() {
        return ID_Usuario;
    }

    public void setID_Usuario(int ID_Usuario) {
        this.ID_Usuario = ID_Usuario;
    }
}