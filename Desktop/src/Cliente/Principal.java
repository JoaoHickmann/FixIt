package Cliente;

import Classes.Usuario;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class Principal extends Application {

    private static Socket servidor;
    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    private static Usuario user;

    @Override
    public void start(Stage stage) throws Exception, IOException {
        //<editor-fold defaultstate="collapsed" desc="Conexão">
        servidor = new Socket();
        servidor.setSoTimeout(2000);
        servidor.connect(new InetSocketAddress("localhost", 12345), 2000);
        in = new ObjectInputStream(getServidor().getInputStream());
        out = new ObjectOutputStream(getServidor().getOutputStream());
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="PossuiAdmin">
        out.writeObject("PossuiAdmin");

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
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/Imagens/logo-sem-fundo.png")));
        stage.show();
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
                                .onAction((ActionEvent event1) -> {
                                    try {
                                        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("FXML"+msg.split(";")[2]+".fxml")), stage.getScene().getWidth(), stage.getScene().getHeight()));
                                        stage.show();
                                    } catch (IOException ex) {
                                        Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                });

                        Platform.runLater(() -> {
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
