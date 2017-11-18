package Cliente;

import Classes.Usuario;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.base.ValidatorBase;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class FXMLRegistrarController implements Initializable {

    @FXML
    private JFXTextField tfNome;
    @FXML
    private JFXTextField tfEmail;
    @FXML
    private JFXPasswordField pfSenha;
    @FXML
    private JFXButton btEntrar;
    @FXML
    private JFXPasswordField pfConfirmarSenha;
    @FXML
    private StackPane StackPane;

    JFXSnackbar snackbar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //<editor-fold defaultstate="collapsed" desc="SnackBar">
        snackbar = new JFXSnackbar(StackPane);
        snackbar.setPrefWidth(300);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Validators">
        RequiredFieldValidator nomeRequiredValidator = new RequiredFieldValidator();
        nomeRequiredValidator.setMessage("O campo deve ser preenchido.");

        RequiredFieldValidator emailRequiredValidator = new RequiredFieldValidator();
        emailRequiredValidator.setMessage("O campo deve ser preenchido.");

        ValidatorBase emailValidator = new ValidatorBase("Email inválido.") {
            @Override
            protected void eval() {
                TextInputControl textField = (TextInputControl) srcControl.get();
                Pattern p = Pattern.compile("^[A-Za-z0-9-]+(\\-[A-Za-z0-9])*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9])");
                Matcher m = p.matcher(textField.getText());

                hasErrors.set(!m.find());
            }
        };

        ValidatorBase emailDisponivelValidator = new ValidatorBase("Email não disponível.") {
            @Override
            protected void eval() {
                TextInputControl textField = (TextInputControl) srcControl.get();
                Pattern p = Pattern.compile("^[A-Za-z0-9-]+(\\-[A-Za-z0-9])*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9])");
                Matcher m = p.matcher(textField.getText());
                int disponivel = 0;

                if (m.find()) {
                    disponivel = (int) Principal.realizarOperacao("EmailDisponivel", textField.getText());
                }

                hasErrors.set(disponivel == 0);
            }
        };

        RequiredFieldValidator senhaRequiredValidator = new RequiredFieldValidator();
        senhaRequiredValidator.setMessage("O campo deve ser preenchido.");

        ValidatorBase senhaCaracteresValidator = new ValidatorBase() {
            @Override
            protected void eval() {
                TextInputControl textField = (TextInputControl) srcControl.get();

                boolean caracteresD = false;
                String alfabeto = "YPDe6FpaxH&yRNs+jMVBAOnShoEmg802tQ@r1i-$L%Jq*G3#9XTdW57lUCkzcubwZ4vKfI";

                for (char letra : textField.getText().toCharArray()) {
                    if (!alfabeto.contains(String.valueOf(letra))) {
                        caracteresD = true;
                        setMessage(letra + " não permitido.");
                        break;
                    }
                }

                hasErrors.set(caracteresD);
            }
        };

        ValidatorBase senhaTamanhoValidator = new ValidatorBase("Permitido somente 6-20 caracteres") {
            @Override
            protected void eval() {
                TextInputControl textField = (TextInputControl) srcControl.get();

                hasErrors.set(textField.getText().length() > 20 || textField.getText().length() < 6);
            }
        };

        ValidatorBase confirmaSenhaValidator = new ValidatorBase("Senha diferente.") {
            @Override
            protected void eval() {
                TextInputControl textField = (TextInputControl) srcControl.get();

                hasErrors.set(!textField.getText().equals(pfSenha.getText()));
            }
        };

        tfNome.getValidators().add(nomeRequiredValidator);
        tfNome.setOnKeyReleased((event) -> {
            tfNome.validate();
        });

        tfEmail.getValidators().add(emailValidator);
        tfEmail.getValidators().add(emailRequiredValidator);
        tfEmail.getValidators().add(emailDisponivelValidator);
        tfEmail.setOnKeyReleased((event) -> {
            tfEmail.validate();
        });

        pfSenha.getValidators().add(senhaCaracteresValidator);
        pfSenha.getValidators().add(senhaTamanhoValidator);
        pfSenha.getValidators().add(senhaRequiredValidator);
        pfSenha.setOnKeyReleased((event) -> {
            pfSenha.validate();
        });

        pfConfirmarSenha.getValidators().add(confirmaSenhaValidator);
        pfConfirmarSenha.setOnKeyReleased((event) -> {
            pfConfirmarSenha.validate();
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Entrar">
        btEntrar.setOnAction((ActionEvent event) -> {
            tfNome.validate();
            tfEmail.validate();
            pfSenha.validate();
            pfConfirmarSenha.validate();

            if (tfNome.validate() && tfEmail.validate() && pfSenha.validate() && pfConfirmarSenha.validate()) {
                new Thread(() -> {
                    String senha = new Criptografia(tfEmail.getText().charAt(0)).criptografar(pfSenha.getText());
                    Usuario user = new Usuario(tfNome.getText(), tfEmail.getText(), senha, true);

                    try {
                        user = (Usuario) Principal.realizarOperacao("CadastrarUsuario", user);
                        Principal.setUser(user);

                        if (user != null) {
                            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("FXMLPrincipal.fxml")), ((Node) event.getSource()).getScene().getWidth(), ((Node) event.getSource()).getScene().getHeight());
                            scene.getStylesheets().add(getClass().getResource("cssSnackbar.css").toExternalForm());
                            Platform.runLater(() -> {
                                stage.setScene(scene);
                                stage.show();
                            });
                        } else {
                            Platform.runLater(() -> {
                                JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Não foi possível cadastrar o usuário.", "Ok", 3000, false, (MouseEvent event1) -> {
                                    snackbar.close();
                                });
                                snackbar.enqueue(barEvent);
                            });
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLRegistrarController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }).start();
            }
        });
        //</editor-fold>
    }
}
