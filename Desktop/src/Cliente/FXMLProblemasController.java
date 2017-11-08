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

    private LinkedList<Problema> problemas;

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

        //<editor-fold defaultstate="collapsed" desc="TipoOnChange">
        cmbTipo.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            cmbProblema.getItems().clear();
            for (Problema problema : problemas) {
                if (problema.getTipo() == newValue.intValue() + 1) {
                    cmbProblema.getItems().add(problema.getDescricao());
                }
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Excluir">
        btExcluir.setOnAction((ActionEvent event) -> {
            for (Problema problema : problemas) {
                if (problema.getDescricao().equals(cmbProblema.getValue())) {
                    JFXDialogLayout layout = new JFXDialogLayout();
                    JFXDialog dialog = new JFXDialog(StackPane, layout, JFXDialog.DialogTransition.CENTER);

                    JFXButton btCancelar = new JFXButton("Cancelar");
                    btCancelar.setTextFill(Paint.valueOf("#29B6F6"));
                    btCancelar.setOnAction((ActionEvent event1) -> {
                        dialog.close();
                    });

                    JFXButton btExcluir = new JFXButton("Excluir");
                    btExcluir.setTextFill(Paint.valueOf("#FF0000"));
                    btExcluir.setOnAction((ActionEvent event1) -> {
                        try {
                            Principal.getSaida().writeObject("ExcluirProblema");
                            Principal.getEntrada().readObject();
                            Principal.getSaida().writeObject(problema);
                            if ((int) Principal.getEntrada().readObject() == 1) {
                                attDados();
                                JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Problema excluido.", "Ok", 3000, false, (MouseEvent event2) -> {
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
                    
                    layout.setHeading(new Text("Deseja realmente excluir este item?"));
                    layout.setActions(actions);

                    dialog.show();
                }
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Adicionar">
        btAdicionar.setOnAction((ActionEvent event) -> {
            JFXDialogLayout layout = new JFXDialogLayout();
            JFXDialog dialog = new JFXDialog(StackPane, layout, JFXDialog.DialogTransition.CENTER);

            AnchorPane content = new AnchorPane();

            JFXComboBox<String> cmbTipo = new JFXComboBox<>();
            cmbTipo.setFocusColor(Paint.valueOf("#29B6F6"));
            cmbTipo.setPromptText("Tipo");
            cmbTipo.setLabelFloat(true);
            cmbTipo.getItems().add("Hardware");
            cmbTipo.getItems().add("Software");
            cmbTipo.getSelectionModel().select(0);
            cmbTipo.setLayoutY(10.0);

            JFXTextField tfProblema = new JFXTextField();
            tfProblema.setFocusColor(Paint.valueOf("#29B6F6"));
            tfProblema.setPromptText("Descrição");
            tfProblema.setLabelFloat(true);
            tfProblema.setLayoutY(55.0);

            RequiredFieldValidator validator = new RequiredFieldValidator();
            validator.setMessage("O campo deve ser preenchido.");
            tfProblema.getValidators().add(validator);

            tfProblema.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                tfProblema.validate();
            });

            AnchorPane.setLeftAnchor(cmbTipo, 0.0);
            AnchorPane.setRightAnchor(cmbTipo, 0.0);

            AnchorPane.setLeftAnchor(tfProblema, 0.0);
            AnchorPane.setRightAnchor(tfProblema, 0.0);

            content.getChildren().add(cmbTipo);
            content.getChildren().add(tfProblema);

            JFXButton btCancelar = new JFXButton("Cancelar");
            btCancelar.setTextFill(Paint.valueOf("#FF0000"));
            btCancelar.setOnAction((ActionEvent event1) -> {
                dialog.close();
            });

            JFXButton btAdicionar = new JFXButton("Adicionar");
            btAdicionar.setTextFill(Paint.valueOf("#29B6F6"));
            btAdicionar.setOnAction((ActionEvent event1) -> {
                if (tfProblema.validate()) {
                    try {
                        Principal.getSaida().writeObject("AdicionarProblema");
                        Principal.getEntrada().readObject();

                        Problema problema = new Problema(tfProblema.getText(), cmbTipo.getSelectionModel().getSelectedIndex() + 1);
                        Principal.getSaida().writeObject(problema);
                        if ((int) Principal.getEntrada().readObject() == 1) {
                            //ADICIONOU
                            attDados();
                            JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Problema adicionado.", "Ok", 3000, false, (MouseEvent event2) -> {
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

            LinkedList<Node> actions = new LinkedList<Node>();
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
            Principal.getSaida().writeObject("Problemas");
            problemas = (LinkedList<Problema>) Principal.getEntrada().readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(FXMLSalasController.class.getName()).log(Level.SEVERE, null, ex);
        }

        cmbTipo.getItems().clear();
        cmbTipo.getItems().add("Hardware");
        cmbTipo.getItems().add("Software");
    }
}
