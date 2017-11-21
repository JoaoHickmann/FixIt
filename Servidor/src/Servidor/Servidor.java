package Servidor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {

    private static Conexao conexao;
    private static MulticastSocket s;
    private static InetAddress group;
    private static Email email;

    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
        conexao = new Conexao();
        System.out.println("Conexão com o banco de dados estabelecida");
        ServerSocket servidor = new ServerSocket(12345);
        System.out.println("Servidor iniciado em na porta: " + servidor.getLocalPort());

        group = InetAddress.getByName("228.5.6.7");
        s = new MulticastSocket(6789);
        //ele pega por padrao o virtualbox no meu pc
        //s.setInterface(InetAddress.getByName("192.168.0.200"));
        //-------------------
        s.joinGroup(group);

        email = new Email();

        while (true) {
            Socket cliente = servidor.accept();
            System.out.println("Nova conexão com o cliente: " + cliente.getInetAddress().getHostAddress());

            new TrataCliente(cliente).start();
        }
    }

    public static int AtualizaTabela(String sql) {
        try {
            return conexao.getStatement().executeUpdate(sql);
        } catch (SQLException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    public static ResultSet ExecutaSelect(String sql) {
        try {
            return conexao.getStatement().executeQuery(sql);
        } catch (SQLException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static void NotificacaoDesktop(String msg) {
        DatagramPacket dp = new DatagramPacket(msg.getBytes(), msg.length(), group, 6789);
        try {
            s.send(dp);
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Email getEmail() {
        return email;
    }
}
