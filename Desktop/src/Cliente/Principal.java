package Cliente;

import Classes.Usuario;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.swing.JOptionPane;
import org.controlsfx.control.Notifications;

public class Principal extends Application {

    private static Socket servidor;
    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    private static Usuario user;
    private static Stage stg;
    private static Class<?> classe;

    @Override
    public void start(Stage stage) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Conexão">
        stg = stage;
        classe = getClass();
        conectar();
        out.writeObject("PossuiAdmin");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="PossuiAdmin">
        try {
            Parent root;
            if ((int) in.readObject() == 1) {
                root = FXMLLoader.load(getClass().getResource("FXMLLogin.fxml"));
            } else {
                root = FXMLLoader.load(getClass().getResource("FXMLRegistrar.fxml"));
            }

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("cssSnackbar.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("FixIt");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/Imagens/ic_launcher_round.png")));
            stage.show();
        } catch (IOException ex) {
            JOptionPane.showConfirmDialog(null, "Tente novamente mais tarde.", "Não foi possível se conectar!", JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Thread Notificações">
        Thread t = new Thread(() -> {
            try {
                InetAddress group = InetAddress.getByName("228.5.6.7");
                MulticastSocket s = new MulticastSocket(6789);
                s.joinGroup(group);

                byte[] buf = new byte[1000];
                DatagramPacket recv = new DatagramPacket(buf, buf.length);

                while (true) {
                    s.receive(recv);

                    if (user != null) {
                        String msg = new String(recv.getData());

                        MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.BELL_RING);
                        icon.setSize("60");

                        Notifications notify = Notifications.create()
                                .title(msg.split(";")[0])
                                .text(msg.split(";")[1])
                                .graphic(icon)
                                .hideAfter(Duration.seconds(10))
                                .onAction((ActionEvent event) -> {
                                    try {
                                        Scene scene1 = new Scene(FXMLLoader.load(getClass().getResource("FXMLPrincipal.fxml")), stage.getScene().getWidth(), stage.getScene().getHeight());
                                        scene1.getStylesheets().add(getClass().getResource("cssSnackbar.css").toExternalForm());
                                        stage.setScene(scene1);
                                        stage.show();
                                        stage.setIconified(false);
                                        stage.toFront();
                                    } catch (IOException ex) {
                                        Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                });

                        Platform.runLater(() -> {
                            Toolkit.getDefaultToolkit().beep();

                            notify.show();
                        });
                    }
                }
            } catch (UnknownHostException ex) {
                Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        t.setDaemon(true);
        t.start();
        //</editor-fold>
    }

    public static void conectar() {
        try {
            servidor = new Socket();
            servidor.setSoTimeout(5000);
            servidor.connect(new InetSocketAddress("localhost", 12345), 5000);
            in = new ObjectInputStream(servidor.getInputStream());
            out = new ObjectOutputStream(servidor.getOutputStream());

            if (user != null) {
                Principal.realizarOperacao("Login", user);
                FXMLLoader loader = new FXMLLoader(classe.getResource("FXMLPrincipal.fxml"));
                Scene scene = new Scene(loader.load(), stg.getScene().getWidth(), stg.getScene().getHeight());
                scene.getStylesheets().add(classe.getResource("cssSnackbar.css").toExternalForm());
                stg.setScene(scene);
                stg.show();
                ((FXMLPrincipalController) loader.getController()).reconnect();
            }
        } catch (IOException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Erro");
            dialog.setHeaderText("Não foi possivel se conectar com o servidor.");
            dialog.setContentText("Deseja se conectar novamente?");
            dialog.getButtonTypes().clear();
            dialog.getButtonTypes().add(ButtonType.YES);
            dialog.getButtonTypes().add(ButtonType.NO);
            dialog.showAndWait().ifPresent((t) -> {
                if (t == ButtonType.YES) {
                    conectar();
                } else {
                    System.exit(0);
                }
            });
        }
    }

    public static Object obterLista(String operacao) {
        try {
            out.writeObject(operacao);
            return in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
            Platform.runLater(() -> {
                Alert dialog = new Alert(Alert.AlertType.ERROR);
                dialog.setTitle("Erro");
                dialog.setHeaderText("Não foi possivel se conectar com o servidor.");
                dialog.setContentText("Deseja se conectar novamente?");
                dialog.getButtonTypes().clear();
                dialog.getButtonTypes().add(ButtonType.YES);
                dialog.getButtonTypes().add(ButtonType.NO);
                dialog.showAndWait().ifPresent((t) -> {
                    if (t == ButtonType.YES) {
                        conectar();
                    } else {
                        System.exit(0);
                    }
                });
            });
            return null;
        }
    }

    public static Object realizarOperacao(String operacao, Object obj) {
        try {
            out.writeObject(operacao);
            in.readObject();
            out.reset();
            out.writeObject(obj);
            return in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
            Platform.runLater(() -> {
                Alert dialog = new Alert(Alert.AlertType.ERROR);
                dialog.setTitle("Erro");
                dialog.setHeaderText("Não foi possivel se conectar com o servidor.");
                dialog.setContentText("Deseja se conectar novamente?");
                dialog.getButtonTypes().clear();
                dialog.getButtonTypes().add(ButtonType.YES);
                dialog.getButtonTypes().add(ButtonType.NO);
                dialog.showAndWait().ifPresent((t) -> {
                    if (t == ButtonType.YES) {
                        conectar();
                    } else {
                        System.exit(0);
                    }
                });
            });
            return null;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    //<editor-fold defaultstate="collapsed" desc="Get & Set">
    public static Socket getServidor() {
        return servidor;
    }

    public static ObjectInputStream getEntrada() {
        return in;
    }

    public static ObjectOutputStream getSaida() {
        return out;
    }

    public static Usuario getUser() {
        return user;
    }

    public static void setUser(Usuario aUser) {
        user = aUser;
    }
    //</editor-fold>
}
