package Cliente;

import Classes.*;
import com.jfoenix.controls.JFXButton;
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
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FXMLLoginController implements Initializable {

    @FXML
    private JFXTextField tfEmail;
    @FXML
    private JFXPasswordField pfSenha;
    @FXML
    private JFXButton btEntrar;
    @FXML
    private Label lblMensagem;
    @FXML
    private JFXButton btSenha;
    @FXML
    private StackPane stack;

    JFXSnackbar snackbar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //<editor-fold defaultstate="collapsed" desc="SnackBar">
        snackbar = new JFXSnackbar(stack);
        snackbar.setPrefWidth(300);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Validators">
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

        ValidatorBase emailEsqueceuSenhaValidator = new ValidatorBase("Email inválido.") {
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

        ValidatorBase senhaTamanhoValidator = new ValidatorBase("Máximo de 20 caracteres.") {
            @Override
            protected void eval() {
                TextInputControl textField = (TextInputControl) srcControl.get();

                hasErrors.set(textField.getText().length() > 20);
            }
        };

        tfEmail.getValidators().add(emailValidator);
        tfEmail.setOnKeyReleased((event) -> {
            tfEmail.validate();
        });

        pfSenha.getValidators().add(requiredValidator);
        pfSenha.getValidators().add(senhaTamanhoValidator);
        pfSenha.setOnKeyReleased((event) -> {
            pfSenha.validate();
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Entrar">
        EventHandler entrar = (EventHandler) (Event event) -> {
            tfEmail.validate();
            pfSenha.validate();
            if (tfEmail.validate() && pfSenha.validate()) {
                new Thread(() -> {
                    String senha = new Criptografia(tfEmail.getText().charAt(0)).criptografar(pfSenha.getText());
                    Usuario usuario = new Usuario(tfEmail.getText(), senha);

                    try {
                        usuario = (Usuario) Principal.realizarOperacao("Login", usuario);
                        if (usuario != null && usuario.isAdministrador()) {
                            Principal.setUser(usuario);

                            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("FXMLPrincipal.fxml")), ((Node) event.getSource()).getScene().getWidth(), ((Node) event.getSource()).getScene().getHeight());
                            scene.getStylesheets().add(getClass().getResource("cssSnackbar.css").toExternalForm());
                            Platform.runLater(() -> {
                                stage.setScene(scene);
                                stage.show();
                            });
                        } else if (usuario != null && !usuario.isAdministrador()) {
                            Platform.runLater(() -> {
                                lblMensagem.setText("Usuário sem permissões de administrador!");
                            });
                        } else {
                            Platform.runLater(() -> {
                                lblMensagem.setText("Email ou Senha incorreto!");
                            });
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLLoginController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }).start();
            }
        };

        pfSenha.setOnAction(entrar);
        btEntrar.setOnAction(entrar);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Esqueceu Senha">
        JFXDialogLayout layout = new JFXDialogLayout();
        JFXDialog dialog = new JFXDialog(stack, layout, JFXDialog.DialogTransition.CENTER);

        AnchorPane content = new AnchorPane();

        JFXTextField tfEmailEsqueceuSenha = new JFXTextField();
        tfEmailEsqueceuSenha.setFocusColor(Paint.valueOf("#29B6F6"));
        tfEmailEsqueceuSenha.setPromptText("Email");
        tfEmailEsqueceuSenha.setLabelFloat(true);
        tfEmailEsqueceuSenha.setLayoutY(10.0);
        tfEmailEsqueceuSenha.getValidators().add(emailEsqueceuSenhaValidator);
        tfEmailEsqueceuSenha.setOnKeyReleased((event) -> {
            tfEmailEsqueceuSenha.validate();
        });

        AnchorPane.setLeftAnchor(tfEmailEsqueceuSenha, 0.0);
        AnchorPane.setRightAnchor(tfEmailEsqueceuSenha, 0.0);
        content.getChildren().add(tfEmailEsqueceuSenha);

        JFXButton btCancelarD = new JFXButton("Cancelar");
        btCancelarD.setTextFill(Paint.valueOf("#FF0000"));
        btCancelarD.setOnAction((ActionEvent event) -> {
            dialog.close();
        });

        JFXButton btAdicionarD = new JFXButton("Enviar email");
        btAdicionarD.setTextFill(Paint.valueOf("#29B6F6"));
        btAdicionarD.setOnAction((ActionEvent event) -> {
            new Thread(() -> {
                if (tfEmailEsqueceuSenha.validate() && (int) Principal.realizarOperacao("EsqueceuSenha", tfEmailEsqueceuSenha.getText()) == 1) {
                    JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Email enviado para " + tfEmailEsqueceuSenha.getText() + "", "Ok", 3000, false, (MouseEvent event1) -> {
                        snackbar.close();
                    });
                    Platform.runLater(() -> {
                        snackbar.enqueue(barEvent);
                        dialog.close();
                    });
                }
            }).start();
        });

        LinkedList<Node> actions = new LinkedList<>();
        actions.add(btCancelarD);
        actions.add(btAdicionarD);

        layout.setHeading(new Text("Recuperação de senha"));
        layout.setBody(content);
        layout.setActions(actions);

        btSenha.setOnAction((event) -> {
            dialog.show();
        });
        //</editor-fold>
    }
}
