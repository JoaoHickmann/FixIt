package Cliente;

import Classes.Usuario;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.base.ValidatorBase;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FXMLAdministradoresController implements Initializable {

    @FXML
    private JFXComboBox<String> cmbEmail;
    @FXML
    private JFXTextField tfNome;
    @FXML
    private JFXButton btExcluir;
    @FXML
    private JFXButton btAdicionar;
    @FXML
    private JFXButton btVoltar;
    @FXML
    private StackPane StackPane;

    private LinkedList<Usuario> administradores;

    JFXSnackbar snackbar;

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

        //<editor-fold defaultstate="collapsed" desc="EmailOnChange">
        cmbEmail.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            tfNome.setText(administradores.get(newValue.intValue()).getNome());
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Excluir">
        btExcluir.setOnAction((ActionEvent event) -> {
            if (cmbEmail.getValue() != null) {
                JFXDialogLayout layout = new JFXDialogLayout();
                JFXDialog dialog = new JFXDialog(StackPane, layout, JFXDialog.DialogTransition.CENTER);

                JFXButton btCancelar = new JFXButton("Cancelar");
                btCancelar.setTextFill(Paint.valueOf("#29B6F6"));
                btCancelar.setOnAction((ActionEvent event1) -> {
                    dialog.close();
                });

                JFXButton btAdicionar = new JFXButton("Excluir");
                btAdicionar.setTextFill(Paint.valueOf("#FF0000"));
                btAdicionar.setOnAction((ActionEvent event1) -> {
                    try {
                        Principal.getSaida().writeObject("ExcluirAdministrador");
                        Principal.getEntrada().readObject();
                        Principal.getSaida().writeObject(administradores.get(cmbEmail.getSelectionModel().getSelectedIndex()));
                        if ((int) Principal.getEntrada().readObject() == 1) {
                            attDados();
                            JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Administrador excluido.", "Ok", 3000, false, (MouseEvent event2) -> {
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
                actions.add(btAdicionar);

                layout.setHeading(new Text("Deseja realmente excluir?"));
                layout.setActions(actions);

                dialog.show();
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Adicionar">
        btAdicionar.setOnAction((ActionEvent event) -> {
            ValidatorBase emailValidator = new ValidatorBase("Email inválido.") {
                @Override
                protected void eval() {
                    TextInputControl textField = (TextInputControl) srcControl.get();
                    Pattern p = Pattern.compile("^[A-Za-z0-9-]+(\\-[A-Za-z0-9])*@"
                            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9])");
                    Matcher m = p.matcher(textField.getText());

                    hasErrors.set(textField.getText() == null || textField.getText().isEmpty() || !m.find());
                }
            };

            RequiredFieldValidator requiredValidator = new RequiredFieldValidator();
            requiredValidator.setMessage("O campo deve ser preenchido.");

            ValidatorBase senhaValidator = new ValidatorBase("Senha inválida. Caracteres especiais permitido: @#$%&*-+.") {
                @Override
                protected void eval() {
                    TextInputControl textField = (TextInputControl) srcControl.get();
                    boolean caracteresD = false;
                    String alfabeto = "YPDe6FpaxH&yRNs+jMVBAOnShoEmg802tQ@r1i-$L%Jq*G3#9XTdW57lUCkzcubwZ4vKfI";

                    for (char letra : textField.getText().toCharArray()) {
                        if (!alfabeto.contains(String.valueOf(letra))) {
                            caracteresD = true;
                            break;
                        }
                    }

                    hasErrors.set(textField.getText().isEmpty() || textField.getText() == null || caracteresD);
                }
            };

            JFXDialogLayout layout = new JFXDialogLayout();
            JFXDialog dialog = new JFXDialog(StackPane, layout, JFXDialog.DialogTransition.CENTER);

            AnchorPane content = new AnchorPane();

            JFXTextField tfNome = new JFXTextField();
            tfNome.setFocusColor(Paint.valueOf("#29B6F6"));
            tfNome.setPromptText("Nome");
            tfNome.setLabelFloat(true);
            tfNome.setLayoutY(10.0);
            tfNome.getValidators().add(requiredValidator);
            tfNome.setOnKeyReleased((event1) -> {
                tfNome.validate();
            });

            JFXTextField tfEmail = new JFXTextField();
            tfEmail.setFocusColor(Paint.valueOf("#29B6F6"));
            tfEmail.setPromptText("Email");
            tfEmail.setLabelFloat(true);
            tfEmail.setLayoutY(70.0);
            tfEmail.getValidators().add(emailValidator);
            tfEmail.setOnKeyReleased((event1) -> {
                tfEmail.validate();
            });

            JFXPasswordField pfSenha = new JFXPasswordField();
            pfSenha.setFocusColor(Paint.valueOf("#29B6F6"));
            pfSenha.setPromptText("Senha");
            pfSenha.setLabelFloat(true);
            pfSenha.setLayoutY(130.0);
            pfSenha.getValidators().add(senhaValidator);
            pfSenha.setOnKeyReleased((event1) -> {
                pfSenha.validate();
            });
            pfSenha.setOnKeyTyped((event1) -> {
                if (pfSenha.getText().length() >= 20) {
                    event.consume();
                }
            });

            ValidatorBase confirmaSenhaValidator = new ValidatorBase("Senha diferente.") {
                @Override
                protected void eval() {
                    TextInputControl textField = (TextInputControl) srcControl.get();

                    hasErrors.set(!textField.getText().equals(pfSenha.getText()));
                }
            };

            JFXPasswordField pfConfirmarSenha = new JFXPasswordField();
            pfConfirmarSenha.setPromptText("Confirmar Senha");
            pfConfirmarSenha.setLabelFloat(true);
            pfConfirmarSenha.setLayoutY(190.0);
            pfConfirmarSenha.getValidators().add(confirmaSenhaValidator);
            pfConfirmarSenha.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    pfConfirmarSenha.validate();
                }
            });

            AnchorPane.setLeftAnchor(tfNome, 0.0);
            AnchorPane.setRightAnchor(tfNome, 0.0);

            AnchorPane.setLeftAnchor(tfEmail, 0.0);
            AnchorPane.setRightAnchor(tfEmail, 0.0);

            AnchorPane.setLeftAnchor(pfSenha, 0.0);
            AnchorPane.setRightAnchor(pfSenha, 0.0);

            AnchorPane.setLeftAnchor(pfConfirmarSenha, 0.0);
            AnchorPane.setRightAnchor(pfConfirmarSenha, 0.0);

            content.getChildren().add(tfNome);
            content.getChildren().add(tfEmail);
            content.getChildren().add(pfSenha);
            content.getChildren().add(pfConfirmarSenha);

            JFXButton btCancelar = new JFXButton("Cancelar");
            btCancelar.setTextFill(Paint.valueOf("#FF0000"));
            btCancelar.setOnAction((ActionEvent event1) -> {
                dialog.close();
            });

            JFXButton btAdicionar = new JFXButton("Adicionar");
            btAdicionar.setTextFill(Paint.valueOf("#29B6F6"));
            btAdicionar.setOnAction((ActionEvent event1) -> {
                tfNome.validate();
                tfEmail.validate();
                pfSenha.validate();
                pfConfirmarSenha.validate();
                if (tfNome.validate() && tfEmail.validate() && pfSenha.validate() && pfConfirmarSenha.validate()) {
                    try {
                        Principal.getSaida().writeObject("CadastrarUsuario");
                        Principal.getEntrada().readObject();

                        Usuario usuario = new Usuario(tfNome.getText(), tfEmail.getText(), new Criptografia(tfEmail.getText().charAt(0)).criptografar(pfSenha.getText()), true);
                        Principal.getSaida().writeObject(usuario);
                        if (((Usuario) Principal.getEntrada().readObject()).getID_Usuario() != 0) {
                            attDados();
                            JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Administrador adicionado.", "Ok", 3000, false, (MouseEvent event2) -> {
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

            layout.setHeading(new Text("Adicionar administrador"));
            layout.setBody(content);
            layout.setActions(actions);

            dialog.show();
        });
        //</editor-fold>

        attDados();
    }

    public void attDados() {
        try {
            Principal.getSaida().writeObject("Administradores");
            administradores = (LinkedList<Usuario>) Principal.getEntrada().readObject();

            cmbEmail.getItems().clear();
            tfNome.setText("");
            for (Usuario usuario : administradores) {
                cmbEmail.getItems().add(usuario.getEmail());
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(FXMLSalasController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
