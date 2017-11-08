package Servidor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexao {
    private final String serverName = "localhost";
    private final String mydatabase = "trabalhofinal";
    private final String url ="jdbc:mysql://" + serverName + "/" + mydatabase;
    private final String username = "root";
    private final String password = "";
    private Connection conexao;
    private Statement stmt;
    
    Conexao() throws SQLException{
        conexao = (Connection) DriverManager.getConnection(url,username,password);
        stmt = conexao.createStatement();
    }

    public Statement getStatement() {
        return stmt;
    }
}
