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
import javafx.application.Platform;
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
    private JFXDialog dialogAdd;
    private JFXDialog dialogExc;

    private LinkedList<Sala> salas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //<editor-fold defaultstate="collapsed" desc="attDados">
        new Thread(() -> {
            salas = (LinkedList<Sala>) Principal.obterLista("Salas");
            Platform.runLater(() -> {
                cmbSala.getItems().clear();
                for (Sala sala : salas) {
                    cmbSala.getItems().add(String.valueOf(sala.getID()));
                }
                cmbSala.getSelectionModel().selectFirst();
            });
        }).start();
        //</editor-fold>

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
        JFXDialogLayout layoutAdd = new JFXDialogLayout();
        dialogAdd = new JFXDialog(StackPane, layoutAdd, JFXDialog.DialogTransition.CENTER);

        JFXButton btCancelarD = new JFXButton("Cancelar");
        btCancelarD.setTextFill(Paint.valueOf("#29B6F6"));
        btCancelarD.setOnAction((ActionEvent event) -> {
            dialogAdd.close();
        });

        JFXButton btExcluirD = new JFXButton("Excluir");
        btExcluirD.setTextFill(Paint.valueOf("#FF0000"));
        btExcluirD.setOnAction((ActionEvent event) -> {
            excluirSala(salas.get(cmbSala.getSelectionModel().getSelectedIndex()));
            dialogAdd.close();
        });

        LinkedList<Node> actions = new LinkedList<>();
        actions.add(btCancelarD);
        actions.add(btExcluirD);

        layoutAdd.setHeading(new Text("Deseja realmente excluir este item?"));
        layoutAdd.setActions(actions);

        btExcluir.setOnAction((ActionEvent event) -> {
            if (cmbSala.getValue() != null) {
                dialogAdd.show();
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Adicionar">
        JFXDialogLayout layoutExc = new JFXDialogLayout();
        dialogExc = new JFXDialog(StackPane, layoutExc, JFXDialog.DialogTransition.CENTER);

        AnchorPane content = new AnchorPane();

        RequiredFieldValidator requiredValidator = new RequiredFieldValidator();
        requiredValidator.setMessage("O campo deve ser preenchido.");

        JFXTextField tfSalaD = new JFXTextField();
        tfSalaD.setPromptText("Sala");
        tfSalaD.setLabelFloat(true);
        tfSalaD.setFocusColor(Paint.valueOf("#29B6F6"));
        tfSalaD.setLayoutY(10.0);
        tfSalaD.getValidators().add(requiredValidator);
        tfSalaD.setOnKeyReleased((event) -> {
            tfSalaD.validate();
        });
        tfSalaD.setOnKeyTyped((KeyEvent event) -> {
            if ((tfSalaD.getText().length() >= 3) || (!Character.isDigit(event.getCharacter().charAt(0)))) {
                event.consume();
            }
        });

        AnchorPane.setLeftAnchor(tfSalaD, 0.0);
        AnchorPane.setRightAnchor(tfSalaD, 0.0);
        content.getChildren().add(tfSalaD);

        JFXButton btCancelarD1 = new JFXButton("Cancelar");
        btCancelarD1.setTextFill(Paint.valueOf("#FF0000"));
        btCancelarD1.setOnAction((ActionEvent event) -> {
            tfSalaD.setText("");
            dialogExc.close();
        });

        JFXButton btAdicionarD1 = new JFXButton("Adicionar");
        btAdicionarD1.setTextFill(Paint.valueOf("#29B6F6"));
        btAdicionarD1.setOnAction((ActionEvent event) -> {
            if (tfSalaD.validate()) {
                adicionarSala(new Sala(Integer.parseInt(tfSalaD.getText())));
                tfSalaD.setText("");
                dialogExc.close();
            }
        });

        LinkedList<Node> actions1 = new LinkedList<>();
        actions1.add(btCancelarD1);
        actions1.add(btAdicionarD1);

        layoutExc.setHeading(new Text("Adicionar sala"));
        layoutExc.setBody(content);
        layoutExc.setActions(actions1);

        btAdicionar.setOnAction((ActionEvent event) -> {
            dialogExc.show();
        });
        //</editor-fold>
    }

    public void excluirSala(Sala sala) {
        new Thread(() -> {
            if ((int) Principal.realizarOperacao("ExcluirSala", sala) == 1) {
                Platform.runLater(() -> {
                    cmbSala.getItems().remove(salas.indexOf(sala));
                    salas.remove(sala);
                    SnackbarEvent barEvent = new SnackbarEvent("Sala excluida.", "Ok", 3000, false, (MouseEvent event) -> {
                        snackbar.close();
                    });
                    snackbar.enqueue(barEvent);
                });
            } else {
                Platform.runLater(() -> {
                    SnackbarEvent barEvent = new SnackbarEvent("Não foi possível excluir a sala.", "Tentar novamente", 3000, false, (MouseEvent event) -> {
                        excluirSala(sala);
                        snackbar.close();
                    });
                    snackbar.enqueue(barEvent);
                });
            }
        }).start();
    }

    public void adicionarSala(Sala sala) {
        new Thread(() -> {
            if ((int) Principal.realizarOperacao("AdicionarSala", sala) == 1) {
                salas.add(sala);
                Platform.runLater(() -> {
                    cmbSala.getItems().add(String.valueOf(sala.getID()));
                    cmbSala.getSelectionModel().selectLast();
                    SnackbarEvent barEvent = new SnackbarEvent("Sala adicionada.", "Ok", 3000, false, (MouseEvent event) -> {
                        snackbar.close();
                    });
                    snackbar.enqueue(barEvent);
                });
            } else {
                Platform.runLater(() -> {
                    SnackbarEvent barEvent = new SnackbarEvent("Não foi possível adicionar a sala.", "Tentar novamente", 3000, false, (MouseEvent event) -> {
                        adicionarSala(sala);
                        snackbar.close();
                    });
                    snackbar.enqueue(barEvent);
                });
            }
        }).start();
    }

}
