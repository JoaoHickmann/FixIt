package Cliente;

import Classes.Problema;
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
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FXMLProblemasController implements Initializable {

    @FXML
    private JFXComboBox<String> cmbTipo;
    @FXML
    private JFXComboBox<String> cmbProblema;
    @FXML
    private JFXButton btExcluir;
    @FXML
    private JFXButton btAdicionar;
    @FXML
    private JFXButton btVoltar;
    @FXML
    private StackPane StackPane;

    private JFXSnackbar snackbar;
    private JFXDialog dialogAdd;
    private JFXDialog dialogExc;

    private LinkedList<Problema> problemas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbTipo.getItems().add("Hardware");
        cmbTipo.getItems().add("Software");

        //<editor-fold defaultstate="collapsed" desc="attDados">
        new Thread(() -> {
            try {
                problemas = (LinkedList<Problema>) Principal.obterLista("Problemas");
                Platform.runLater(() -> {
                    cmbTipo.getSelectionModel().selectFirst();
                });
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(FXMLProblemasController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();
        //</editor-fold>

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

        //<editor-fold defaultstate="collapsed" desc="TipoOnChange">
        cmbTipo.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            cmbProblema.getItems().clear();
            for (Problema problema : problemas) {
                if (problema.getTipo() == newValue.intValue() + 1) {
                    cmbProblema.getItems().add(problema.getDescricao());
                }
            }
            cmbProblema.getSelectionModel().selectFirst();
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Excluir">
        JFXDialogLayout layoutExc = new JFXDialogLayout();
        dialogExc = new JFXDialog(StackPane, layoutExc, JFXDialog.DialogTransition.CENTER);

        JFXButton btCancelarD = new JFXButton("Cancelar");
        btCancelarD.setTextFill(Paint.valueOf("#29B6F6"));
        btCancelarD.setOnAction((ActionEvent event) -> {
            dialogExc.close();
        });

        JFXButton btExcluirD = new JFXButton("Excluir");
        btExcluirD.setTextFill(Paint.valueOf("#FF0000"));
        btExcluirD.setOnAction((ActionEvent event) -> {
            for (Problema problema : problemas) {
                if (problema.getDescricao().equals(cmbProblema.getValue())) {
                    excluirProblema(problema);
                }
            }
            dialogExc.close();
        });

        LinkedList<Node> actions = new LinkedList<>();
        actions.add(btCancelarD);
        actions.add(btExcluirD);

        layoutExc.setHeading(new Text("Deseja realmente excluir este item?"));
        layoutExc.setActions(actions);

        btExcluir.setOnAction((ActionEvent event) -> {
            if (cmbProblema.getValue() != null) {
                dialogExc.show();
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Adicionar">
        JFXDialogLayout layoutAdd = new JFXDialogLayout();
        dialogAdd = new JFXDialog(StackPane, layoutAdd, JFXDialog.DialogTransition.CENTER);

        AnchorPane content = new AnchorPane();

        JFXComboBox<String> cmbTipoD = new JFXComboBox<>();
        cmbTipoD.setFocusColor(Paint.valueOf("#29B6F6"));
        cmbTipoD.setPromptText("Tipo");
        cmbTipoD.setLabelFloat(true);
        cmbTipoD.setLayoutY(10.0);

        cmbTipoD.getItems().add("Hardware");
        cmbTipoD.getItems().add("Software");
        cmbTipoD.getSelectionModel().selectFirst();

        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("O campo deve ser preenchido.");

        JFXTextField tfProblemaD = new JFXTextField();
        tfProblemaD.setFocusColor(Paint.valueOf("#29B6F6"));
        tfProblemaD.setPromptText("Descrição");
        tfProblemaD.setLabelFloat(true);
        tfProblemaD.setLayoutY(55.0);
        tfProblemaD.getValidators().add(validator);
        tfProblemaD.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            tfProblemaD.validate();
        });

        AnchorPane.setLeftAnchor(cmbTipoD, 0.0);
        AnchorPane.setRightAnchor(cmbTipoD, 0.0);

        AnchorPane.setLeftAnchor(tfProblemaD, 0.0);
        AnchorPane.setRightAnchor(tfProblemaD, 0.0);

        content.getChildren().add(cmbTipoD);
        content.getChildren().add(tfProblemaD);

        JFXButton btCancelarD1 = new JFXButton("Cancelar");
        btCancelarD1.setTextFill(Paint.valueOf("#FF0000"));
        btCancelarD1.setOnAction((ActionEvent event) -> {
            dialogAdd.close();
        });

        JFXButton btAdicionarD1 = new JFXButton("Adicionar");
        btAdicionarD1.setTextFill(Paint.valueOf("#29B6F6"));
        btAdicionarD1.setOnAction((ActionEvent event) -> {
            if (tfProblemaD.validate()) {
                adicionarProblema(new Problema(tfProblemaD.getText(), cmbTipoD.getSelectionModel().getSelectedIndex() + 1));
                dialogAdd.close();
            }
        });

        LinkedList<Node> actions1 = new LinkedList<>();
        actions1.add(btCancelarD1);
        actions1.add(btAdicionarD1);

        layoutAdd.setHeading(new Text("Adicionar problema"));
        layoutAdd.setBody(content);
        layoutAdd.setActions(actions1);

        btAdicionar.setOnAction((ActionEvent event) -> {
            cmbTipoD.getSelectionModel().selectFirst();
            tfProblemaD.setText("");
            dialogAdd.show();
        });
        //</editor-fold>
    }

    public void excluirProblema(Problema problema) {
        new Thread(() -> {
            try {
                if ((int) Principal.realizarOperacao("ExcluirProblema", problema) == 1) {
                    Platform.runLater(() -> {
                        cmbProblema.getItems().remove(cmbProblema.getSelectionModel().getSelectedIndex());
                        problemas.remove(problema);
                        JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Problema excluido.", "Ok", 3000, false, (MouseEvent event) -> {
                            snackbar.close();
                        });
                        snackbar.enqueue(barEvent);
                    });
                } else {
                    Platform.runLater(() -> {
                        JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Não foi possível excluir o problema.", "Tentar novamente", 3000, false, (MouseEvent event) -> {
                            excluirProblema(problema);
                            snackbar.close();
                        });
                        snackbar.enqueue(barEvent);
                    });
                }
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(FXMLSalasController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();
    }

    public void adicionarProblema(Problema problema) {
        new Thread(() -> {
            try {
                int result = (int) Principal.realizarOperacao("AdicionarProblema", problema);
                if (result != 0) {
                    problema.setID(result);
                    Platform.runLater(() -> {
                        problemas.add(problema);
                        if (cmbTipo.getSelectionModel().getSelectedIndex() == problema.getTipo() - 1) {
                            cmbProblema.getItems().add(problema.getDescricao());
                        } else {
                            cmbTipo.getSelectionModel().select(problema.getTipo() - 1);
                        }
                        cmbProblema.getSelectionModel().selectLast();

                        JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Problema adicionado.", "Ok", 3000, false, (MouseEvent event2) -> {
                            snackbar.close();
                        });
                        snackbar.enqueue(barEvent);
                    });
                } else {
                    Platform.runLater(() -> {
                        JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Não foi possível adicionar o problema.", "Tentar novamente", 3000, false, (MouseEvent event) -> {
                            adicionarProblema(problema);
                            snackbar.close();
                        });
                        snackbar.enqueue(barEvent);
                    });
                }
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(FXMLSalasController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();
    }
}
