package Servidor;

import Classes.Chamado;
import Classes.Computador;
import Classes.Problema;
import Classes.Sala;
import Classes.Usuario;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

public class TrataCliente extends Thread {

    private Socket cliente;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Usuario user;

    public TrataCliente(Socket cliente) throws IOException {
        this.cliente = cliente;
        this.out = new ObjectOutputStream(cliente.getOutputStream());
        this.in = new ObjectInputStream(cliente.getInputStream());
    }

    @Override
    public void run() {
        try {
            System.out.println("Trata cliente " + cliente.getInetAddress().getHostAddress() + " iniciado");

            while (true) {
                try {
                    String operacao = (String) in.readObject();
                    System.out.println("Operação: " + operacao);

                    String sql;
                    ResultSet rs;
                    Usuario usuario;
                    Chamado chamado;
                    Computador computador;
                    Problema problema;
                    Sala sala;

                    //<editor-fold defaultstate="collapsed" desc="Diversos">
                    if (operacao.equals("Login")) {
                        out.writeObject(1);
                        usuario = (Usuario) in.readObject();
                        sql = " SELECT id_usuario, nome, isAdministrador"
                                + " FROM usuarios"
                                + " WHERE UPPER(Email) = '" + usuario.getEmail().toUpperCase() + "'"
                                + "   AND Senha = '" + usuario.getSenha() + "'";
                        rs = Servidor.ExecutaSelect(sql);

                        if (rs.next()) {
                            usuario.setID_Usuario(rs.getInt(1));
                            usuario.setNome(rs.getString(2));
                            usuario.setAdministrador(rs.getInt(3) == 1 ? true : false);
                        } else {
                            usuario = null;
                        }

                        user = usuario;
                        out.writeObject(usuario);
                    } else if (operacao.equals("PossuiAdmin")) {
                        sql = " SELECT 1"
                                + " FROM usuarios"
                                + " WHERE isAdministrador = 1";

                        out.writeObject(Servidor.ExecutaSelect(sql).next() ? 1 : 0);
                    } else if (operacao.equals("MudarNome")) {
                        out.writeObject(1);
                        usuario = (Usuario) in.readObject();

                        sql = "UPDATE usuarios"
                                + " SET nome = '" + usuario.getNome() + "'"
                                + " WHERE id_usuario = " + usuario.getID_Usuario();

                        out.writeObject(Servidor.AtualizaTabela(sql));
                    } else if (operacao.equals("MudarSenha")) {
                        out.writeObject(1);
                        usuario = (Usuario) in.readObject();

                        sql = "UPDATE usuarios"
                                + " SET senha = '" + usuario.getSenha() + "'"
                                + " WHERE id_usuario = " + usuario.getID_Usuario();

                        out.writeObject(Servidor.AtualizaTabela(sql));
                    } else if (operacao.equals("AtenderChamado")) {
                        out.writeObject(1);
                        chamado = (Chamado) in.readObject();

                        sql = "UPDATE chamados"
                                + " SET id_usuario_administrador = " + chamado.getID_Administrador()
                                + "   , status = 2"
                                + " WHERE id_chamado = " + chamado.getID_Chamado();

                        out.writeObject(Servidor.AtualizaTabela(sql));
                    } else if (operacao.equals("FinalizarChamado")) {
                        out.writeObject(1);
                        chamado = (Chamado) in.readObject();

                        sql = "UPDATE chamados"
                                + " SET status = 3"
                                + "   , data_final = CURRENT_TIMESTAMP"
                                + " WHERE id_chamado = " + chamado.getID_Chamado();

                        out.writeObject(Servidor.AtualizaTabela(sql));
                    } else if (operacao.equals("EsqueceuSenha")) {
                        out.writeObject(1);
                        String email = (String) in.readObject();

                        sql = "SELECT senha"
                                + " FROM usuarios"
                                + " WHERE email = '" + email + "'";
                        rs = Servidor.ExecutaSelect(sql);

                        if (rs.next()) {
                            String senha = new Criptografia(email.charAt(0)).descriptografar(rs.getString(1));

                            new Thread(() -> {
                                try {
                                    Servidor.getEmail().enviar(email, "Recuperação de senha", "Sua senha é '" + senha + "'.");
                                } catch (MessagingException ex) {
                                    Logger.getLogger(TrataCliente.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }).start();
                        }

                        out.writeObject(1);
                    }  //</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Cadastro">
                    if (operacao.equals("CadastrarUsuario")) {
                        out.writeObject(1);
                        usuario = (Usuario) in.readObject();

                        sql = " INSERT INTO `usuarios`(`Nome`, `Email`, `Senha`, `isAdministrador`)"
                                + " VALUES ('" + usuario.getNome() + "','" + usuario.getEmail() + "','" + usuario.getSenha() + "'," + (usuario.isAdministrador() ? "1" : "0") + ")";

                        Servidor.AtualizaTabela(sql);

                        sql = " SELECT MAX(ID_Usuario) ID FROM usuarios";
                        rs = Servidor.ExecutaSelect(sql);
                        rs.next();

                        usuario.setID_Usuario(rs.getInt("ID"));

                        out.writeObject(usuario);
                    } else if (operacao.equals("AdicionarAdministrador")) {
                        out.writeObject(1);
                        usuario = (Usuario) in.readObject();

                        sql = "INSERT INTO usuarios(nome, email, senha, isAdministrador)"
                                + " VALUES ('" + usuario.getNome() + "', '" + usuario.getEmail() + "', '" + usuario.getSenha() + "', " + (usuario.isAdministrador() ? "1" : "0") + ")";

                        out.writeObject(Servidor.AtualizaTabela(sql));
                    } else  if (operacao.equals("AdicionarChamado")) {
                        out.writeObject(1);
                        chamado = (Chamado) in.readObject();

                        sql = "INSERT INTO chamados(id_usuario, id_computador, id_problema, observacao)"
                                + " VALUES (" + user.getID_Usuario() + ", " + chamado.getComputador() + ", " + chamado.getID_Problema() + ", " + chamado.getObservacao() +"'";

                        out.writeObject(Servidor.AtualizaTabela(sql));
                    } else if (operacao.equals("AdicionarProblema")) {
                        out.writeObject(1);
                        problema = (Problema) in.readObject();

                        sql = "INSERT INTO problemas(descricao, tipo)"
                                + " VALUES ('" + problema.getDescricao() + "', " + problema.getTipo() + ")";

                        out.writeObject(Servidor.AtualizaTabela(sql));
                    } else if (operacao.equals("AdicionarComputador")) {
                        out.writeObject(1);
                        computador = (Computador) in.readObject();

                        sql = "INSERT INTO computadores(numero, id_sala)"
                                + " VALUES ('" + computador.getNumero() + "', " + computador.getSala() + ")";

                        out.writeObject(Servidor.AtualizaTabela(sql));
                    } else if (operacao.equals("AdicionarSala")) {
                        out.writeObject(1);
                        sala = (Sala) in.readObject();

                        sql = "INSERT INTO salas(ID_Sala)"
                                + " VALUES (" + sala.getID() + ")";

                        out.writeObject(Servidor.AtualizaTabela(sql));

                        Servidor.NotificacaoDesktop("Teste;Sala");
                    } else //</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Lista">
                    if (operacao.equals("Administradores")) {
                        sql = " SELECT id_usuario, nome, email"
                                + " FROM usuarios"
                                + " WHERE isAdministrador = 1";
                        rs = Servidor.ExecutaSelect(sql);

                        LinkedList<Usuario> administradores = new LinkedList<>();

                        while (rs.next()) {
                            int ID = rs.getInt(1);
                            String nome = rs.getString(2);
                            String email = rs.getString(3);

                            Usuario p = new Usuario(ID, nome, email, "", true);

                            administradores.add(p);
                        }

                        out.writeObject(administradores);
                    } else if (operacao.equals("Salas")) {
                        sql = "SELECT id_sala"
                                + " FROM salas";

                        rs = Servidor.ExecutaSelect(sql);

                        LinkedList<Sala> salas = new LinkedList<>();

                        while (rs.next()) {
                            int ID = rs.getInt(1);

                            salas.add(new Sala(ID));
                        }

                        out.writeObject(salas);
                    } else if (operacao.equals("Chamados")) {
                        sql = " SELECT C.id_chamado, U.nome, C.data_inicial, C.data_final,"
                                + "    C.id_computador, CO.id_sala, P.tipo,"
                                + "    P.descricao, C.observacao, C.status, A.nome"
                                + " FROM chamados C"
                                + " INNER JOIN usuarios U ON U.id_usuario = C.id_usuario"
                                + " INNER JOIN computadores CO ON CO.id_computador = C.id_computador"
                                + " INNER JOIN problemas P ON P.id_problema = C.id_problema"
                                + " LEFT JOIN usuarios A ON A.id_usuario = C.id_usuario_administrador"
                                + " ORDER BY C.status, C.data_inicial DESC";
                        rs = Servidor.ExecutaSelect(sql);

                        LinkedList<Chamado> chamados = new LinkedList<>();

                        while (rs.next()) {
                            int ID = rs.getInt(1);
                            String nome = rs.getString(2);
                            String data_inicial = rs.getString(3);
                            String data_final = rs.getString(4);
                            int computadorr = rs.getInt(5);
                            int salaa = rs.getInt(6);
                            int tipo_problema = rs.getInt(7);
                            String problemaa = rs.getString(8);
                            String observacao = rs.getString(9);
                            int status = rs.getInt(10);
                            String adm = rs.getString(11);

                            chamados.add(new Chamado(ID, nome, data_inicial, computadorr,
                                    salaa, tipo_problema, problemaa, observacao, status));
                            chamados.getLast().setData_Final(data_final);
                            chamados.getLast().setNome_Administrador(adm);

                        }

                        out.writeObject(chamados);
                    } else if (operacao.equals("MeusChamados")) {
                        sql = " SELECT C.id_chamado, U.nome, C.data_inicial, C.data_final,"
                                + "    C.computador, CO.id_sala, P.tipo,"
                                + "    P.descricao, C.observacao, C.status, A.nome, C.id_computador"
                                + " FROM chamados C"
                                + " INNER JOIN usuarios U ON U.id_usuario = C.id_usuario"
                                + " INNER JOIN computadores CO ON CO.id_computador = C.id_computador"
                                + " INNER JOIN problemas P ON P.id_problema = C.id_problema"
                                + " LEFT JOIN usuarios A ON A.id_usuario = C.id_usuario_administrador"
                                + " WHERE C.id_usuario = " + user.getID_Usuario()
                                + " ORDER BY C.status, C.data_inicial DESC";
                        rs = Servidor.ExecutaSelect(sql);

                        LinkedList<Chamado> chamados = new LinkedList<>();

                        while (rs.next()) {
                            int ID = rs.getInt(1);
                            String nome = rs.getString(2);
                            String data_inicial = rs.getString(3);
                            String data_final = rs.getString(4);
                            int computadorr = rs.getInt(5);
                            int salaa = rs.getInt(6);
                            int tipo_problema = rs.getInt(7);
                            String problemaa = rs.getString(8);
                            String observacao = rs.getString(9);
                            int status = rs.getInt(10);
                            String adm = rs.getString(11);
                            int id_computador = rs.getInt(12);

                            chamados.add(new Chamado(ID, nome, data_inicial, computadorr,
                                    salaa, tipo_problema, problemaa, observacao, status));
                            chamados.getLast().setData_Final(data_final);
                            chamados.getLast().setNome_Administrador(adm);

                        }

                        out.writeObject(chamados);
                    } else if (operacao.equals("Problemas")) {
                        sql = "SELECT id_problema, descricao, tipo"
                                + " FROM problemas";

                        rs = Servidor.ExecutaSelect(sql);

                        LinkedList<Problema> problemas = new LinkedList<>();

                        while (rs.next()) {
                            int ID = rs.getInt(1);
                            String Descricao = rs.getString(2);
                            int Tipo = rs.getInt(3);

                            problemas.add(new Problema(ID, Descricao, Tipo));
                        }

                        out.writeObject(problemas);
                    } else if (operacao.equals("Computadores")) {
                        sql = "SELECT id_computador, numero, id_sala"
                                + " FROM computadores";

                        rs = Servidor.ExecutaSelect(sql);

                        LinkedList<Computador> computadores = new LinkedList<>();

                        while (rs.next()) {
                            int ID = rs.getInt(1);
                            int Numero = rs.getInt(2);
                            int Salaa = rs.getInt(3);

                            computadores.add(new Computador(ID, Numero, Salaa));
                        }

                        out.writeObject(computadores);
                    } else //</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Exclusao">
                    if (operacao.equals("ExcluirProblema")) {
                        out.writeObject(1);

                        problema = (Problema) in.readObject();

                        sql = "DELETE FROM problemas"
                                + " WHERE id_problema = " + problema.getID();

                        out.writeObject(Servidor.AtualizaTabela(sql));
                    } else if (operacao.equals("ExcluirAdministrador")) {
                        out.writeObject(1);

                        usuario = (Usuario) in.readObject();

                        sql = "DELETE FROM usuarios"
                                + " WHERE id_usuario = " + usuario.getID_Usuario();

                        out.writeObject(Servidor.AtualizaTabela(sql));
                    } else  if (operacao.equals("ExcluirChamado")) {
                        out.writeObject(1);

                        chamado = (Chamado) in.readObject();

                        sql = "DELETE FROM chamados"
                                + " WHERE id_chamado = " + chamado.getID_Chamado();

                        out.writeObject(Servidor.AtualizaTabela(sql));
                    } else if (operacao.equals("ExcluirSala")) {
                        out.writeObject(1);

                        sala = (Sala) in.readObject();

                        sql = "DELETE FROM salas"
                                + " WHERE id_sala = " + sala.getID();

                        out.writeObject(Servidor.AtualizaTabela(sql));
                    } else if (operacao.equals("ExcluirComputador")) {
                        out.writeObject(1);

                        computador = (Computador) in.readObject();

                        sql = "DELETE FROM computadores"
                                + " WHERE id_computador = " + computador.getID();

                        out.writeObject(Servidor.AtualizaTabela(sql));
                    } //</editor-fold>

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(TrataCliente.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(TrataCliente.class.getName()).log(Level.SEVERE, null, ex);
                    out.writeObject(ex.getErrorCode());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(TrataCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
