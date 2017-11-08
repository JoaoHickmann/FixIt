package Cliente;

import Classes.Computador;
import Classes.Sala;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FXMLComputadoresController implements Initializable {

    @FXML
    private JFXComboBox<String> cmbSala;
    @FXML
    private JFXComboBox<String> cmbComputador;
    @FXML
    private JFXButton btExcluir;
    @FXML
    private JFXButton btAdicionar;
    @FXML
    private JFXButton btVoltar;
    @FXML
    private StackPane StackPane;

    private LinkedList<Computador> computadores;

    private JFXSnackbar snackbar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //<editor-fold defaultstate="collapsed" desc="SnackBar">
        snackbar = new JFXSnackbar(StackPane);
        snackbar.setPrefWidth(300);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Voltar">
        btVoltar.setOnAction((ActionEvent event) -> {
            try {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(FXMLLoader.load(getClass().getResource("FXMLPrincipal.fxml")), ((Node) event.getSource()).getScene().getWidth(), ((Node) event.getSource()).getScene().getHeight());
                scene.getStylesheets().add(getClass().getResource("cssSnackbar.css").toExternalForm());
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                Logger.getLogger(FXMLSalasController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="SalaOnChange">
        cmbSala.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            cmbComputador.getItems().clear();
            for (Computador computador : computadores) {
                if (computador.getSala() == Integer.parseInt(newValue)) {
                    cmbComputador.getItems().add(String.valueOf(computador.getNumero()));
                }
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Excluir">
        btExcluir.setOnAction((ActionEvent event) -> {
            for (Computador computador : computadores) {
                if ((computador.getSala() == Integer.parseInt(cmbSala.getValue())) && (computador.getNumero() == Integer.parseInt(cmbComputador.getValue()))) {
                    JFXDialogLayout layout = new JFXDialogLayout();
                    JFXDialog dialog = new JFXDialog(StackPane, layout, JFXDialog.DialogTransition.CENTER);

                    layout.setHeading(new Text("Deseja realmente excluir este item?"));

                    JFXButton btCancelar = new JFXButton("Cancelar");
                    btCancelar.setTextFill(Paint.valueOf("#29B6F6"));
                    btCancelar.setOnAction((ActionEvent event1) -> {
                        dialog.close();
                    });

                    JFXButton btExcluir = new JFXButton("Excluir");
                    btExcluir.setTextFill(Paint.valueOf("#FF0000"));
                    btExcluir.setOnAction((ActionEvent event1) -> {
                        try {
                            Principal.getSaida().writeObject("ExcluirComputador");
                            Principal.getEntrada().readObject();
                            Principal.getSaida().writeObject(computador);
                            if ((int) Principal.getEntrada().readObject() == 1) {
                                attDados();
                                JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Computador excluido.", "Ok", 3000, false, (MouseEvent event2) -> {
                                    snackbar.close();
                                });
                                snackbar.enqueue(barEvent);
                            } else {
                                //erro
                            }
                        } catch (IOException | ClassNotFoundException ex) {
                            Logger.getLogger(FXMLSalasController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        dialog.close();
                    });

                    LinkedList<Node> actions = new LinkedList<>();
                    actions.add(btCancelar);
                    actions.add(btExcluir);
                    layout.setActions(actions);

                    dialog.show();
                }
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Adicionar">
        btAdicionar.setOnAction((ActionEvent event) -> {
            RequiredFieldValidator requiredValidator = new RequiredFieldValidator();
            requiredValidator.setMessage("O campo deve ser preenchido.");

            JFXDialogLayout layout = new JFXDialogLayout();
            JFXDialog dialog = new JFXDialog(StackPane, layout, JFXDialog.DialogTransition.CENTER);

            AnchorPane content = new AnchorPane();

            JFXComboBox<String> cmbSala = new JFXComboBox<>();
            try {
                Principal.getSaida().writeObject("Salas");
                LinkedList<Sala> salas = (LinkedList<Sala>) Principal.getEntrada().readObject();

                for (Sala sala : salas) {
                    cmbSala.getItems().add(String.valueOf(sala.getID()));
                }
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(FXMLSalasController.class.getName()).log(Level.SEVERE, null, ex);
            }
            cmbSala.setFocusColor(Paint.valueOf("#29B6F6"));
            cmbSala.setPromptText("Sala");
            cmbSala.setLabelFloat(true);
            cmbSala.getSelectionModel().select(0);
            cmbSala.setLayoutY(10.0);

            JFXTextField tfNumero = new JFXTextField();
            tfNumero.setFocusColor(Paint.valueOf("#29B6F6"));
            tfNumero.setPromptText("Número");
            tfNumero.setLabelFloat(true);
            tfNumero.setLayoutY(55.0);
            tfNumero.getValidators().add(requiredValidator);
            tfNumero.setOnKeyTyped((KeyEvent event1) -> {
                if ((tfNumero.getText().length() >= 2) || (!Character.isDigit(event1.getCharacter().charAt(0)))) {
                    event1.consume();
                }
            });
            tfNumero.setOnKeyReleased((KeyEvent event1) -> {
                tfNumero.validate();
            });

            AnchorPane.setLeftAnchor(cmbSala, 0.0);
            AnchorPane.setRightAnchor(cmbSala, 0.0);

            AnchorPane.setLeftAnchor(tfNumero, 0.0);
            AnchorPane.setRightAnchor(tfNumero, 0.0);

            content.getChildren().add(cmbSala);
            content.getChildren().add(tfNumero);

            JFXButton btCancelar = new JFXButton("Cancelar");
            btCancelar.setTextFill(Paint.valueOf("#FF0000"));
            btCancelar.setOnAction((ActionEvent event1) -> {
                dialog.close();
            });

            JFXButton btAdicionar = new JFXButton("Adicionar");
            btAdicionar.setTextFill(Paint.valueOf("#29B6F6"));
            btAdicionar.setOnAction((ActionEvent event1) -> {
                if (tfNumero.validate()) {
                    try {
                        Principal.getSaida().writeObject("AdicionarComputador");
                        Principal.getEntrada().readObject();

                        Computador computador = new Computador(Integer.valueOf(tfNumero.getText()), Integer.valueOf(cmbSala.getValue()));
                        Principal.getSaida().writeObject(computador);
                        if ((int) Principal.getEntrada().readObject() == 1) {
                            attDados();
                            JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Computador adicionado.", "Ok", 3000, false, (MouseEvent event2) -> {
                                snackbar.close();
                            });
                            snackbar.enqueue(barEvent);
                        } else {
                            //ERRO
                        }
                    } catch (IOException | ClassNotFoundException ex) {
                        Logger.getLogger(FXMLSalasController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    dialog.close();
                }
            });

            LinkedList<Node> actions = new LinkedList<>();
            actions.add(btCancelar);
            actions.add(btAdicionar);

            layout.setHeading(new Text("Adicionar problema"));
            layout.setBody(content);
            layout.setActions(actions);

            dialog.show();
        });
        //</editor-fold>

        attDados();
    }

    public void attDados() {
        try {
            Principal.getSaida().writeObject("Computadores");
            computadores = (LinkedList<Computador>) Principal.getEntrada().readObject();

            cmbSala.getItems().clear();
            cmbComputador.getItems().clear();
            for (Computador computador : computadores) {
                if (!cmbSala.getItems().contains(String.valueOf(computador.getSala()))) {
                    cmbSala.getItems().add(String.valueOf(computador.getSala()));
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(FXMLSalasController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
