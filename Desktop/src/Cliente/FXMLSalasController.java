package Cliente;

import Classes.Sala;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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

public class FXMLSalasController implements Initializable {

    @FXML
    private JFXComboBox<String> cmbSala;
    @FXML
    private JFXButton btExcluir;
    @FXML
    private JFXButton btAdicionar;
    @FXML
    private JFXButton btVoltar;
    @FXML
    private StackPane StackPane;
    @FXML
    private AnchorPane AnchorPane;

    private JFXSnackbar snackbar;

    private LinkedList<Sala> salas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //<editor-fold defaultstate="collapsed" desc="SnackBar">
        snackbar = new JFXSnackbar(AnchorPane);
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

        //<editor-fold defaultstate="collapsed" desc="Excluir">
        btExcluir.setOnAction((ActionEvent event) -> {
            if (cmbSala.getValue() != null) {
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
                        Principal.getSaida().writeObject("ExcluirSala");
                        Principal.getEntrada().readObject();
                        Principal.getSaida().writeObject(salas.get(cmbSala.getSelectionModel().getSelectedIndex()));
                        if ((int) Principal.getEntrada().readObject() == 1) {
                            //JDIALOG EXCLUIU
                            attDados();
                            SnackbarEvent barEvent = new SnackbarEvent("Sala excluida.", "Ok", 3000, false, (MouseEvent event2) -> {
                                snackbar.close();
                            });
                            snackbar.enqueue(barEvent);
                        } else {
                            //JDIALOG ERRO
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
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Adicionar">
        btAdicionar.setOnAction((ActionEvent event) -> {
            JFXDialogLayout layout = new JFXDialogLayout();
            JFXDialog dialog = new JFXDialog(StackPane, layout, JFXDialog.DialogTransition.CENTER);

            AnchorPane content = new AnchorPane();

            RequiredFieldValidator validator = new RequiredFieldValidator();
            validator.setMessage("O campo deve ser preenchido.");

            JFXTextField tfSala = new JFXTextField();
            tfSala.setPromptText("Sala");
            tfSala.setLabelFloat(true);
            tfSala.setFocusColor(Paint.valueOf("#29B6F6"));
            tfSala.setLayoutY(10.0);
            tfSala.getValidators().add(validator);
            tfSala.setOnKeyReleased((event1) -> {
                tfSala.validate();
            });
            tfSala.setOnKeyTyped((KeyEvent event1) -> {
                if ((tfSala.getText().length() >= 3) || (!Character.isDigit(event1.getCharacter().charAt(0)))) {
                    event1.consume();
                }
            });

            AnchorPane.setLeftAnchor(tfSala, 0.0);
            AnchorPane.setRightAnchor(tfSala, 0.0);
            content.getChildren().add(tfSala);

            JFXButton btCancelar = new JFXButton("Cancelar");
            btCancelar.setTextFill(Paint.valueOf("#FF0000"));
            btCancelar.setOnAction((ActionEvent event1) -> {
                dialog.close();
            });

            JFXButton btAdicionar = new JFXButton("Adicionar");
            btAdicionar.setTextFill(Paint.valueOf("#29B6F6"));
            btAdicionar.setOnAction((ActionEvent event1) -> {
                if (tfSala.validate()) {
                    try {
                        Principal.getSaida().writeObject("AdicionarSala");
                        Principal.getEntrada().readObject();
                        Sala sala = new Sala(Integer.parseInt(tfSala.getText()));
                        Principal.getSaida().writeObject(sala);
                        if ((int) Principal.getEntrada().readObject() == 1) {
                            //ADICIONOU
                            attDados();
                            SnackbarEvent barEvent = new SnackbarEvent("Sala adicionada.", "Ok", 3000, false, (MouseEvent event2) -> {
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

            layout.setHeading(new Text("Adicionar sala"));
            layout.setBody(content);
            layout.setActions(actions);

            dialog.show();
        });
        //</editor-fold>

        attDados();
    }

    private void attDados() {
        try {
            Principal.getSaida().writeObject("Salas");
            salas = (LinkedList<Sala>) Principal.getEntrada().readObject();
            cmbSala.getItems().clear();
            for (Sala sala : salas) {
                cmbSala.getItems().add(String.valueOf(sala.getID()));
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(FXMLSalasController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
