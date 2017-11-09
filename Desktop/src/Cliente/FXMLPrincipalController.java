package Cliente;

import Classes.Chamado;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXNodesList;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXPopup.PopupHPosition;
import com.jfoenix.controls.JFXPopup.PopupVPosition;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.io.IOException;
import java.net.URL;
import static java.util.Collections.singletonList;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FXMLPrincipalController implements Initializable {

    @FXML
    private JFXTextField tfID;
    @FXML
    private JFXTextField tfUsuario;
    @FXML
    private JFXTextField tfAdministrador;
    @FXML
    private JFXTextField tfCriacao;
    @FXML
    private JFXTextField tfFinalizacao;
    @FXML
    private JFXTextArea taObservacoes;
    @FXML
    private JFXTextField tfTipoProblema;
    @FXML
    private JFXTextField tfProblema;
    @FXML
    private StackPane StackPane;
    @FXML
    private AnchorPane apBody;
    @FXML
    private JFXButton btAtualizar;
    @FXML
    private JFXButton btAtender;
    @FXML
    private JFXButton btFinalizar;
    @FXML
    private JFXButton btMenu;
    @FXML
    private JFXListView<AnchorPane> lvChamados;

    JFXSnackbar snackbar;

    private LinkedList<Chamado> chamados;
    @FXML
    private JFXButton btExcluir;
    @FXML
    private JFXTextField tfSala;
    @FXML
    private JFXTextField tfComputador;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //<editor-fold defaultstate="collapsed" desc="SnackBar">
        snackbar = new JFXSnackbar(StackPane);
        snackbar.setPrefWidth(300);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Menu">
        JFXPopup menu = new JFXPopup();

        JFXButton btConfig = new JFXButton("Configurações");
        btConfig.setPadding(new Insets(10));
        btConfig.setMinWidth(100.0);
        btConfig.setOnAction((event) -> {
            try {
                menu.hide();
                Stage stage = (Stage) btMenu.getScene().getWindow();
                Scene scene = new Scene(FXMLLoader.load(getClass().getResource("FXMLConfiguracoes.fxml")), btMenu.getScene().getWidth(), btMenu.getScene().getHeight());
                scene.getStylesheets().add(getClass().getResource("cssSnackbar.css").toExternalForm());
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                Logger.getLogger(FXMLSalasController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        JFXButton btSair = new JFXButton("Sair");
        btSair.setPadding(new Insets(10));
        btSair.setMinWidth(100.0);
        btSair.setOnAction((event) -> {
            try {
                menu.hide();
                Stage stage = (Stage) btMenu.getScene().getWindow();
                Scene scene = new Scene(FXMLLoader.load(getClass().getResource("FXMLLogin.fxml")), btMenu.getScene().getWidth(), btMenu.getScene().getHeight());
                scene.getStylesheets().add(getClass().getResource("cssSnackbar.css").toExternalForm());
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                Logger.getLogger(FXMLSalasController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        VBox vBox = new VBox(btConfig, btSair);
        menu.setPopupContent(vBox);

        btMenu.setOnAction((ActionEvent event) -> {
            menu.show(btMenu, PopupVPosition.TOP, PopupHPosition.RIGHT);
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Popup">
        JFXPopup popup = new JFXPopup();

        JFXButton btAtenderP = new JFXButton("Atender");
        btAtenderP.setPadding(new Insets(10));
        btAtenderP.setMinWidth(100.0);
        btAtenderP.setOnAction((event) -> {
            Chamado chamado = chamados.get(lvChamados.getSelectionModel().getSelectedIndex());

            if (chamado.getStatus() == 1) {
                chamado.setID_Administrador(Principal.getUser().getID_Usuario());
                atenderChamado(chamado);
            }
            popup.hide();
        });

        JFXButton btFinalizarP = new JFXButton("Finalizar");
        btFinalizarP.setPadding(new Insets(10));
        btFinalizarP.setMinWidth(100.0);
        btFinalizarP.setOnAction((event) -> {
            Chamado chamado = chamados.get(lvChamados.getSelectionModel().getSelectedIndex());
            if (chamado.getStatus() == 2) {
                finalizarChamado(chamado);
            }
            popup.hide();
        });

        JFXButton btExcluirP = new JFXButton("Excluir");
        btExcluirP.setPadding(new Insets(10));
        btExcluirP.setMinWidth(100.0);
        btExcluirP.setOnAction((event) -> {
            excluirChamado(chamados.get(lvChamados.getSelectionModel().getSelectedIndex()));
            popup.hide();
        });

        VBox vBox1 = new VBox(btAtenderP, btFinalizarP, btExcluirP);
        popup.setPopupContent(vBox1);

        lvChamados.setOnMouseClicked((event) -> {
            Chamado chamado = chamados.get(lvChamados.getSelectionModel().getSelectedIndex());
            btAtenderP.setDisable(chamado.getStatus() != 1);
            btFinalizarP.setDisable(chamado.getStatus() != 2);

            if (event.getButton() == MouseButton.SECONDARY) {
                popup.show(lvChamados, PopupVPosition.TOP, PopupHPosition.LEFT, event.getX(), event.getY());
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="FAB">
        MaterialDesignIconView icon;

        icon = new MaterialDesignIconView(MaterialDesignIcon.PENCIL);
        icon.setSize("30px");
        icon.setGlyphStyle("-fx-fill: #FFFFFF;");

        JFXButton btAdicionar = new JFXButton();
        btAdicionar.setButtonType(JFXButton.ButtonType.RAISED);
        btAdicionar.setGraphic(icon);
        btAdicionar.setContentDisplay(ContentDisplay.RIGHT);
        btAdicionar.setMinSize(50, 50);
        btAdicionar.setStyle("-fx-background-radius: 50px; -fx-background-color: #29B6F6;");

        icon = new MaterialDesignIconView(MaterialDesignIcon.ACCOUNT);
        icon.setSize("25px");
        icon.setGlyphStyle("-fx-fill: #29B6F6;");

        JFXButton btAdicionarAdministrador = new JFXButton();
        btAdicionarAdministrador.setButtonType(JFXButton.ButtonType.RAISED);
        btAdicionarAdministrador.setGraphic(icon);
        btAdicionarAdministrador.setContentDisplay(ContentDisplay.RIGHT);
        btAdicionarAdministrador.setText("Administrador");
        btAdicionarAdministrador.setMinSize(130, 50);
        btAdicionarAdministrador.setStyle("-fx-background-radius: 50px; -fx-background-color: #FFFFFF;");
        btAdicionarAdministrador.setOnAction((ActionEvent event) -> {
            try {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(FXMLLoader.load(getClass().getResource("FXMLAdministradores.fxml")), ((Node) event.getSource()).getScene().getWidth(), ((Node) event.getSource()).getScene().getHeight());
                scene.getStylesheets().add(getClass().getResource("cssSnackbar.css").toExternalForm());
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                Logger.getLogger(FXMLPrincipalController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        icon = new MaterialDesignIconView(MaterialDesignIcon.HOME);
        icon.setSize("25px");
        icon.setGlyphStyle("-fx-fill: #29B6F6;");

        JFXButton btAdicionarSala = new JFXButton();
        btAdicionarSala.setButtonType(JFXButton.ButtonType.RAISED);
        btAdicionarSala.setGraphic(icon);
        btAdicionarSala.setContentDisplay(ContentDisplay.RIGHT);
        btAdicionarSala.setText("Sala");
        btAdicionarSala.setMinSize(130, 50);
        btAdicionarSala.setStyle("-fx-background-radius: 50px; -fx-background-color: #FFFFFF;");
        btAdicionarSala.setOnAction((ActionEvent event) -> {
            try {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(FXMLLoader.load(getClass().getResource("FXMLSalas.fxml")), ((Node) event.getSource()).getScene().getWidth(), ((Node) event.getSource()).getScene().getHeight());
                scene.getStylesheets().add(getClass().getResource("cssSnackbar.css").toExternalForm());
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                Logger.getLogger(FXMLPrincipalController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        icon = new MaterialDesignIconView(MaterialDesignIcon.SETTINGS);
        icon.setSize("25px");
        icon.setGlyphStyle("-fx-fill: #29B6F6;");

        JFXButton btAdicionarProblema = new JFXButton();
        btAdicionarProblema.setButtonType(JFXButton.ButtonType.RAISED);
        btAdicionarProblema.setGraphic(icon);
        btAdicionarProblema.setContentDisplay(ContentDisplay.RIGHT);
        btAdicionarProblema.setText("Problema");
        btAdicionarProblema.setMinSize(130, 50);
        btAdicionarProblema.setStyle("-fx-background-radius: 50px; -fx-background-color: #FFFFFF;");
        btAdicionarProblema.setOnAction((ActionEvent event) -> {
            try {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(FXMLLoader.load(getClass().getResource("FXMLProblemas.fxml")), ((Node) event.getSource()).getScene().getWidth(), ((Node) event.getSource()).getScene().getHeight());
                scene.getStylesheets().add(getClass().getResource("cssSnackbar.css").toExternalForm());
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                Logger.getLogger(FXMLPrincipalController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        icon = new MaterialDesignIconView(MaterialDesignIcon.LAPTOP);
        icon.setSize("25px");
        icon.setGlyphStyle("-fx-fill: #29B6F6;");

        JFXButton btAdicionarComputador = new JFXButton();
        btAdicionarComputador.setButtonType(JFXButton.ButtonType.RAISED);
        btAdicionarComputador.setGraphic(icon);
        btAdicionarComputador.setContentDisplay(ContentDisplay.RIGHT);
        btAdicionarComputador.setText("Computador");
        btAdicionarComputador.setMinSize(130, 50);
        btAdicionarComputador.setStyle("-fx-background-radius: 50px; -fx-background-color: #FFFFFF;");
        btAdicionarComputador.setOnAction((ActionEvent event) -> {
            try {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(FXMLLoader.load(getClass().getResource("FXMLComputadores.fxml")), ((Node) event.getSource()).getScene().getWidth(), ((Node) event.getSource()).getScene().getHeight());
                scene.getStylesheets().add(getClass().getResource("cssSnackbar.css").toExternalForm());
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                Logger.getLogger(FXMLPrincipalController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        JFXNodesList nodeList = new JFXNodesList();
        nodeList.addAnimatedNode(btAdicionar, (Boolean expanded) -> singletonList(new KeyValue(
                btAdicionar.getGraphic().rotateProperty(),
                expanded ? -90 : 0,
                Interpolator.EASE_BOTH)));
        nodeList.addAnimatedNode(btAdicionarSala);
        nodeList.addAnimatedNode(btAdicionarAdministrador);
        nodeList.addAnimatedNode(btAdicionarProblema);
        nodeList.addAnimatedNode(btAdicionarComputador);
        nodeList.setSpacing(10);
        nodeList.setRotate(180);

        AnchorPane.setBottomAnchor(nodeList, 10.0);
        AnchorPane.setRightAnchor(nodeList, 10.0);

        apBody.getChildren().add(2, nodeList);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Chamadas OnChange">
        lvChamados.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (lvChamados.getSelectionModel().getSelectedIndex() == -1) {
                tfID.setText("");
                tfUsuario.setText("");
                tfAdministrador.setText("");
                tfCriacao.setText("");
                tfFinalizacao.setText("");
                tfTipoProblema.setText("");
                tfProblema.setText("");
                taObservacoes.setText("");
                tfSala.setText("");
                tfComputador.setText("");
                btAtender.setDisable(true);
                btFinalizar.setDisable(true);
            } else {
                Chamado chamado = chamados.get(lvChamados.getSelectionModel().getSelectedIndex());
                tfID.setText(chamado.getID_Chamado() + "");
                tfUsuario.setText(chamado.getNome_Usuario());
                tfAdministrador.setText(chamado.getNome_Administrador());
                tfCriacao.setText(chamado.getData_Inicial());
                tfFinalizacao.setText(chamado.getData_Final());
                tfTipoProblema.setText(chamado.getTipo_Problema() == 1 ? "Hardware" : "Software");
                tfProblema.setText(chamado.getProblema());
                taObservacoes.setText(chamado.getObservacao());
                tfSala.setText(String.valueOf(chamado.getSala()));
                tfComputador.setText(String.valueOf(chamado.getComputador()));

                btAtender.setDisable(chamado.getStatus() != 1);
                btFinalizar.setDisable(chamado.getStatus() != 2);
                btAtenderP.setDisable(chamado.getStatus() != 1);
                btFinalizarP.setDisable(chamado.getStatus() != 2);
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Atualizar">
        RotateTransition rt = new RotateTransition(Duration.millis(500), btAtualizar.getGraphic());
        rt.setToAngle(360);
        rt.setOnFinished((ActionEvent event1) -> {
            btAtualizar.getGraphic().setRotate(0);
        });
        rt.setCycleCount(1);
        rt.setInterpolator(Interpolator.EASE_BOTH);

        btAtualizar.setOnAction((ActionEvent event) -> {
            rt.play();
            attDados();
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Atender">
        btAtender.setOnAction((ActionEvent event) -> {
            Chamado chamado = chamados.get(lvChamados.getSelectionModel().getSelectedIndex());

            if (chamado.getStatus() == 1) {
                chamado.setID_Administrador(Principal.getUser().getID_Usuario());
                atenderChamado(chamado);
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Finalizar">
        btFinalizar.setOnAction((ActionEvent event) -> {
            Chamado chamado = chamados.get(lvChamados.getSelectionModel().getSelectedIndex());
            if (chamado.getStatus() == 2) {
                finalizarChamado(chamado);
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Excluir">
        btExcluir.setOnAction((ActionEvent event) -> {
            excluirChamado(chamados.get(lvChamados.getSelectionModel().getSelectedIndex()));
        });
        //</editor-fold>

        attDados();
    }

    public void atenderChamado(Chamado chamado) {
        try {
            Principal.getSaida().writeObject("AtenderChamado");
            Principal.getEntrada().readObject();

            Principal.getSaida().writeObject(chamado);
            if ((Integer) Principal.getEntrada().readObject() == 1) {
                attDados();
                JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Chamado atendido.", "Ok", 3000, false, (MouseEvent event1) -> {
                    snackbar.close();
                });
                snackbar.enqueue(barEvent);
            } else {

            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(FXMLSalasController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void finalizarChamado(Chamado chamado) {
        try {
            Principal.getSaida().writeObject("FinalizarChamado");
            Principal.getEntrada().readObject();

            Principal.getSaida().writeObject(chamado);
            if ((Integer) Principal.getEntrada().readObject() == 1) {
                attDados();
                JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Chamado Finalizado.", "Ok", 3000, false, (MouseEvent event1) -> {
                    snackbar.close();
                });
                snackbar.enqueue(barEvent);
            } else {

            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(FXMLSalasController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void excluirChamado(Chamado chamado) {
        try {
            Principal.getSaida().writeObject("ExcluirChamado");
            Principal.getEntrada().readObject();

            Principal.getSaida().writeObject(chamado);
            if ((Integer) Principal.getEntrada().readObject() == 1) {
                attDados();
                JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Chamado excluido", "Ok", 3000, false, (MouseEvent event1) -> {
                    snackbar.close();
                });
                snackbar.enqueue(barEvent);
            } else {

            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(FXMLSalasController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void attDados() {
        try {
            Principal.getSaida().writeObject("Chamados");
            chamados = (LinkedList<Chamado>) Principal.getEntrada().readObject();

            lvChamados.getItems().clear();

            for (Chamado chamado : chamados) {
                Label lblChamado = new Label("Chamado #" + chamado.getID_Chamado());
                lblChamado.setFont(Font.font(14));

                Label lblTipo = new Label((chamado.getTipo_Problema() == 1 ? "Hardware" : "Software") + " - " + chamado.getProblema());
                lblTipo.setFont(Font.font(12));
                lblTipo.setLayoutY(20);

                Label lblLocal = new Label(chamado.getSala() + " - " + chamado.getComputador());
                lblLocal.setFont(Font.font(12));
                lblLocal.setLayoutY(20);

                AnchorPane.setRightAnchor(lblLocal, 0.0);

                FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.CIRCLE);
                String color;
                if (chamado.getStatus() == 1) {
                    color = "#FF0000";
                } else if (chamado.getStatus() == 2) {
                    color = "#FFFF00";
                } else {
                    color = "#00FF00";
                }
                icon.setStyle("-fx-fill: " + color + ";");

                AnchorPane.setRightAnchor(icon, 0.0);
                AnchorPane.setTopAnchor(icon, 0.0);

                lvChamados.getItems().add(new AnchorPane(lblChamado, icon, lblTipo, lblLocal));
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(FXMLPrincipalController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
