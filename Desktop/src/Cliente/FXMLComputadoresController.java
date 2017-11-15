package Cliente;

import Classes.Computador;
import Classes.Sala;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.base.ValidatorBase;
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
import javafx.scene.control.TextInputControl;
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

    private JFXSnackbar snackbar;
    private JFXDialog dialogAdd;
    private JFXDialog dialogExc;

    private LinkedList<Computador> computadores;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //<editor-fold defaultstate="collapsed" desc="attDados">
        new Thread(() -> {
            try {
                computadores = (LinkedList<Computador>) Principal.obterLista("Computadores");
                Platform.runLater(() -> {
                    for (Computador computador : computadores) {
                        if (!cmbSala.getItems().contains(String.valueOf(computador.getSala()))) {
                            cmbSala.getItems().add(String.valueOf(computador.getSala()));
                        }
                    }
                    cmbSala.getSelectionModel().selectFirst();
                });
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(FXMLComputadoresController.class.getName()).log(Level.SEVERE, null, ex);
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

        //<editor-fold defaultstate="collapsed" desc="SalaOnChange">
        cmbSala.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            cmbComputador.getItems().clear();
            for (Computador computador : computadores) {
                if (computador.getSala() == Integer.parseInt(newValue)) {
                    cmbComputador.getItems().add(String.valueOf(computador.getNumero()));
                }
            }
            cmbComputador.getSelectionModel().selectFirst();
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Excluir">
        JFXDialogLayout layoutExc = new JFXDialogLayout();
        dialogExc = new JFXDialog(StackPane, layoutExc, JFXDialog.DialogTransition.CENTER);

        layoutExc.setHeading(new Text("Deseja realmente excluir este item?"));

        JFXButton btCancelarD = new JFXButton("Cancelar");
        btCancelarD.setTextFill(Paint.valueOf("#29B6F6"));
        btCancelarD.setOnAction((ActionEvent event) -> {
            dialogExc.close();
        });

        JFXButton btExcluirD = new JFXButton("Excluir");
        btExcluirD.setTextFill(Paint.valueOf("#FF0000"));
        btExcluirD.setOnAction((ActionEvent event) -> {
            for (Computador computador : computadores) {
                if (computador.getSala() == Integer.parseInt(cmbSala.getValue()) && computador.getNumero() == Integer.parseInt(cmbComputador.getValue())) {
                    excluirComputador(computador);
                }
            }
            dialogExc.close();
        });

        LinkedList<Node> actions = new LinkedList<>();
        actions.add(btCancelarD);
        actions.add(btExcluirD);
        layoutExc.setActions(actions);

        btExcluir.setOnAction((ActionEvent event) -> {
            if (cmbSala.getValue() != null && cmbComputador.getValue() != null) {
                dialogExc.show();
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Adicionar">        
        ValidatorBase computadorValidator = new ValidatorBase("Valor inválido.") {
            @Override
            protected void eval() {
                TextInputControl textField = (TextInputControl) srcControl.get();

                boolean erro = false;
                if (textField.getText().contains("-")) {
                    if (textField.getText().indexOf("-") == textField.getText().length() - 1 || (textField.getText().indexOf("-") != 1 && textField.getText().indexOf("-") != 2)) {
                        erro = true;
                    } else {
                        int a = Integer.parseInt(textField.getText().split("-")[0]);
                        int b = Integer.parseInt(textField.getText().split("-")[1]);
                        erro = (a > b) || (a == b) || (a < 1 || a > 99) || (b < 1 || b > 99) || (textField.getText().split("-").length > 2);
                    }
                }

                hasErrors.set(textField.getText() == null || textField.getText().isEmpty() || erro);
            }
        };

        JFXDialogLayout layoutAdd = new JFXDialogLayout();
        dialogAdd = new JFXDialog(StackPane, layoutAdd, JFXDialog.DialogTransition.CENTER);

        AnchorPane content = new AnchorPane();

        JFXComboBox<String> cmbSalaD = new JFXComboBox<>();
        cmbSalaD.setFocusColor(Paint.valueOf("#29B6F6"));
        cmbSalaD.setPromptText("Sala");
        cmbSalaD.setLabelFloat(true);
        cmbSalaD.setLayoutY(10.0);

        JFXTextField tfNumeroD = new JFXTextField();
        tfNumeroD.setFocusColor(Paint.valueOf("#29B6F6"));
        tfNumeroD.setPromptText("Número");
        tfNumeroD.setLabelFloat(true);
        tfNumeroD.setLayoutY(55.0);
        tfNumeroD.getValidators().add(computadorValidator);
        tfNumeroD.setOnKeyReleased((KeyEvent event) -> {
            tfNumeroD.validate();
        });
        tfNumeroD.setOnKeyTyped((KeyEvent event) -> {
            if (!(Character.isDigit(event.getCharacter().charAt(0)) || event.getCharacter().equals("-"))) {
                event.consume();
            } else if (event.getCharacter().equals("-") && tfNumeroD.getText().contains("-")) {
                event.consume();
            }
        });

        AnchorPane.setLeftAnchor(cmbSalaD, 0.0);
        AnchorPane.setRightAnchor(cmbSalaD, 0.0);

        AnchorPane.setLeftAnchor(tfNumeroD, 0.0);
        AnchorPane.setRightAnchor(tfNumeroD, 0.0);

        content.getChildren().add(cmbSalaD);
        content.getChildren().add(tfNumeroD);

        JFXButton btCancelarD1 = new JFXButton("Cancelar");
        btCancelarD1.setTextFill(Paint.valueOf("#FF0000"));
        btCancelarD1.setOnAction((ActionEvent event) -> {
            dialogAdd.close();
        });

        JFXButton btAdicionarD = new JFXButton("Adicionar");
        btAdicionarD.setTextFill(Paint.valueOf("#29B6F6"));
        btAdicionarD.setOnAction((ActionEvent event) -> {
            if (tfNumeroD.validate()) {
                LinkedList<Computador> pcs = new LinkedList<>();
                if (tfNumeroD.getText().contains("-")) {
                    pcs.add(new Computador(Integer.parseInt(tfNumeroD.getText().split("-")[0]), Integer.parseInt(cmbSalaD.getValue())));
                    pcs.add(new Computador(Integer.parseInt(tfNumeroD.getText().split("-")[1]), Integer.parseInt(cmbSalaD.getValue())));
                } else {
                    pcs.add(new Computador(Integer.parseInt(tfNumeroD.getText()), Integer.parseInt(cmbSalaD.getValue())));
                }
                adicionarComputador(pcs);
                dialogAdd.close();
            }
        });

        LinkedList<Node> actions1 = new LinkedList<>();
        actions1.add(btCancelarD1);
        actions1.add(btAdicionarD);

        layoutAdd.setHeading(new Text("Adicionar problema"));
        layoutAdd.setBody(content);
        layoutAdd.setActions(actions1);

        btAdicionar.setOnAction((ActionEvent event) -> {
            new Thread(() -> {
                try {
                    LinkedList<Sala> salas = (LinkedList<Sala>) Principal.obterLista("Salas");
                    Platform.runLater(() -> {
                        cmbSalaD.getItems().clear();
                        for (Sala sala : salas) {
                            cmbSalaD.getItems().add(String.valueOf(sala.getID()));
                        }
                        cmbSalaD.getSelectionModel().selectFirst();
                    });
                } catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(FXMLComputadoresController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }).start();
            tfNumeroD.setText("");
            dialogAdd.show();
        });
        //</editor-fold>
    }

    public void adicionarComputador(LinkedList<Computador> pcs) {
        new Thread(() -> {
            try {
                int result = (int) Principal.realizarOperacao("AdicionarComputador", pcs);
                if (result != 0) {
                    if (pcs.size() == 1) {
                        pcs.get(0).setID(result);
                        computadores.add(pcs.get(0));
                        Platform.runLater(() -> {
                            if (cmbSala.getItems().contains(String.valueOf(pcs.get(0).getSala()))) {
                                if (cmbSala.getValue().equals(String.valueOf(pcs.get(0).getSala()))) {
                                    cmbComputador.getItems().add(String.valueOf(pcs.get(0).getNumero()));
                                } else {
                                    cmbSala.getSelectionModel().select(String.valueOf(pcs.get(0).getSala()));
                                }
                                cmbComputador.getSelectionModel().selectLast();
                            } else {
                                cmbSala.getItems().add(String.valueOf(pcs.get(0).getSala()));
                                cmbSala.getSelectionModel().selectLast();
                            }
                        });
                    } else {
                        for (int i = pcs.get(0).getNumero(); i <= pcs.get(1).getNumero(); i++) {
                            computadores.add(new Computador(result - (pcs.get(1).getNumero() - i), i, pcs.get(0).getSala()));
                        }
                        Platform.runLater(() -> {
                            if (cmbSala.getItems().contains(String.valueOf(pcs.get(0).getSala()))) {
                                cmbSala.getSelectionModel().select(String.valueOf(pcs.get(0).getSala()));
                                cmbComputador.getItems().clear();
                                for (Computador computador : computadores) {
                                    if (computador.getSala() == pcs.get(0).getSala()) {
                                        cmbComputador.getItems().add(String.valueOf(computador.getNumero()));
                                    }
                                }
                            } else {
                                cmbSala.getItems().add(String.valueOf(pcs.get(0).getSala()));
                                cmbSala.getSelectionModel().selectLast();
                            }
                            cmbComputador.getSelectionModel().selectLast();
                        });
                    }
                    Platform.runLater(() -> {
                        JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Computador" + (pcs.size() == 1 ? "" : "s") + " adicionado" + (pcs.size() == 1 ? "" : "s") + ".", "Ok", 3000, false, (MouseEvent event) -> {
                            snackbar.close();
                        });
                        snackbar.enqueue(barEvent);
                    });
                } else {
                    Platform.runLater(() -> {
                        JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Não foi possível adicionar o" + (pcs.size() == 1 ? "" : "s") + " computador" + (pcs.size() == 1 ? "" : "s") + ".", "Tentar novamente", 3000, false, (MouseEvent event) -> {
                            adicionarComputador(pcs);
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

    public void excluirComputador(Computador computador) {
        new Thread(() -> {
            try {
                if ((int) Principal.realizarOperacao("ExcluirComputador", computador) == 1) {
                    computadores.remove(computador);
                    Platform.runLater(() -> {
                        cmbComputador.getItems().remove(cmbComputador.getSelectionModel().getSelectedIndex());
                        cmbComputador.getSelectionModel().selectFirst();
                        if (cmbComputador.getItems().size() == 0) {
                            cmbSala.getItems().remove(cmbSala.getSelectionModel().getSelectedIndex());
                            cmbSala.getSelectionModel().selectFirst();
                        }
                        JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Computador excluido.", "Ok", 3000, false, (MouseEvent event) -> {
                            snackbar.close();
                        });
                        snackbar.enqueue(barEvent);
                    });
                } else {
                    Platform.runLater(() -> {
                        JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Não foi possível excluir o computador.", "Tentar novamente", 3000, false, (MouseEvent event) -> {
                            excluirComputador(computador);
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
