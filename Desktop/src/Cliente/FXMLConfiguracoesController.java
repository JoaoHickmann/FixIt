package Cliente;

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
import javafx.application.Platform;
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

public class FXMLConfiguracoesController implements Initializable {

    @FXML
    private JFXButton btVoltar;
    @FXML
    private JFXButton btSettings;
    @FXML
    private JFXTextField tfNome;
    @FXML
    private JFXButton btAlterarNome;
    @FXML
    private JFXButton btAlterarSenha;
    @FXML
    private StackPane StackPane;
    @FXML
    private JFXTextField tfEmail;
    @FXML
    private JFXButton btExcluirConta;

    JFXSnackbar snackbar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //<editor-fold defaultstate="collapsed" desc="SnackBar">
        snackbar = new JFXSnackbar(StackPane);
        snackbar.setPrefWidth(300);
        //</editor-fold>

        tfNome.setText(Principal.getUser().getNome());
        tfEmail.setText(Principal.getUser().getEmail());

        //<editor-fold defaultstate="collapsed" desc="Validators">
        RequiredFieldValidator requiredValidator = new RequiredFieldValidator();
        requiredValidator.setMessage("O campo deve ser preenchido.");

        ValidatorBase novaSenhaValidator = new ValidatorBase("Senha inválida. Caracteres especiais permitido: @#$%&*-+.") {
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

        ValidatorBase novaSenhaTamanhoValidator = new ValidatorBase("Permitido somente 6-20 caracteres") {
            @Override
            protected void eval() {
                TextInputControl textField = (TextInputControl) srcControl.get();

                hasErrors.set(textField.getText().length() > 20 || textField.getText().length() < 6);
            }
        };

        ValidatorBase senhaValidator = new ValidatorBase("Senha incorreta.") {
            @Override
            protected void eval() {
                TextInputControl textField = (TextInputControl) srcControl.get();

                hasErrors.set(!(new Criptografia(Principal.getUser().getEmail().charAt(0)).criptografar(textField.getText()).equals(Principal.getUser().getSenha())));
            }
        };

        tfNome.getValidators().add(requiredValidator);
        tfNome.setOnKeyReleased((event) -> {
            tfNome.validate();
        });
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

        //<editor-fold defaultstate="collapsed" desc="Alterar Nome">
        btAlterarNome.setOnAction((ActionEvent event) -> {
            if (tfNome.validate() && !tfNome.getText().equals(Principal.getUser().getNome())) {
                new Thread(() -> {
                    if ((int) Principal.realizarOperacao("MudarNome", tfNome.getText()) == 1) {
                        Principal.getUser().setNome(tfNome.getText());
                        Platform.runLater(() -> {
                            JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Nome alterado.", "Ok", 3000, false, (MouseEvent event2) -> {
                                snackbar.close();
                            });
                            snackbar.enqueue(barEvent);
                        });
                    } else {
                        Platform.runLater(() -> {
                            JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Não foi possível alterar o nome.", "Ok", 3000, false, (MouseEvent event2) -> {
                                snackbar.close();
                            });
                            snackbar.enqueue(barEvent);
                        });
                    }
                }).start();
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Alterar Senha">
        JFXDialogLayout layout = new JFXDialogLayout();
        JFXDialog dialog = new JFXDialog(StackPane, layout, JFXDialog.DialogTransition.CENTER);

        AnchorPane content = new AnchorPane();

        JFXPasswordField pfSenhaAtual = new JFXPasswordField();
        pfSenhaAtual.setPromptText("Senha Atual");
        pfSenhaAtual.setLabelFloat(true);
        pfSenhaAtual.setFocusColor(Paint.valueOf("#29B6F6"));
        pfSenhaAtual.setLayoutY(10.0);
        pfSenhaAtual.getValidators().add(senhaValidator);
        pfSenhaAtual.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                pfSenhaAtual.validate();
            }
        });

        JFXPasswordField pfNovaSenha = new JFXPasswordField();
        pfNovaSenha.setPromptText("Nova Senha");
        pfNovaSenha.setLabelFloat(true);
        pfNovaSenha.setFocusColor(Paint.valueOf("#29B6F6"));
        pfNovaSenha.setLayoutY(70.0);
        pfNovaSenha.getValidators().add(novaSenhaValidator);
        pfNovaSenha.getValidators().add(novaSenhaTamanhoValidator);
        pfNovaSenha.setOnKeyReleased((event1) -> {
            pfNovaSenha.validate();
        });

        ValidatorBase confirmaSenhaValidator = new ValidatorBase("Senha diferente.") {
            @Override
            protected void eval() {
                TextInputControl textField = (TextInputControl) srcControl.get();

                hasErrors.set(!textField.getText().equals(pfNovaSenha.getText()));
            }
        };

        JFXPasswordField pfConfirmarSenha = new JFXPasswordField();
        pfConfirmarSenha.setPromptText("Confirmar Senha");
        pfConfirmarSenha.setLabelFloat(true);
        pfConfirmarSenha.setLayoutY(130.0);
        pfConfirmarSenha.setFocusColor(Paint.valueOf("#29B6F6"));
        pfConfirmarSenha.getValidators().add(confirmaSenhaValidator);
        pfConfirmarSenha.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                pfConfirmarSenha.validate();
            }
        });

        AnchorPane.setLeftAnchor(pfSenhaAtual, 0.0);
        AnchorPane.setRightAnchor(pfSenhaAtual, 0.0);

        AnchorPane.setLeftAnchor(pfNovaSenha, 0.0);
        AnchorPane.setRightAnchor(pfNovaSenha, 0.0);

        AnchorPane.setLeftAnchor(pfConfirmarSenha, 0.0);
        AnchorPane.setRightAnchor(pfConfirmarSenha, 0.0);

        content.getChildren().add(pfSenhaAtual);
        content.getChildren().add(pfNovaSenha);
        content.getChildren().add(pfConfirmarSenha);

        JFXButton btCancelar = new JFXButton("Cancelar");
        btCancelar.setTextFill(Paint.valueOf("#FF0000"));
        btCancelar.setOnAction((ActionEvent event1) -> {
            dialog.close();
        });

        JFXButton btAlterar = new JFXButton("Alterar");
        btAlterar.setTextFill(Paint.valueOf("#29B6F6"));
        btAlterar.setOnAction((ActionEvent event1) -> {
            new Thread(() -> {
                String senha = new Criptografia(Principal.getUser().getEmail().charAt(0)).criptografar(pfNovaSenha.getText());
                if ((int) Principal.realizarOperacao("MudarSenha", senha) == 1) {
                    Principal.getUser().setSenha(senha);
                    Platform.runLater(() -> {
                        JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Senha alterada.", "Ok", 3000, false, (MouseEvent event2) -> {
                            snackbar.close();
                        });
                        snackbar.enqueue(barEvent);
                    });
                } else {
                    Platform.runLater(() -> {
                        JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Não foi possível alterar a senha.", "Tentar novamente", 3000, false, (MouseEvent event2) -> {
                            dialog.show();
                            snackbar.close();
                        });
                        snackbar.enqueue(barEvent);
                    });
                }
            }).start();
            dialog.close();
        });

        LinkedList<Node> actions = new LinkedList<>();
        actions.add(btCancelar);
        actions.add(btAlterar);

        layout.setHeading(new Text("Alterar Senha"));
        layout.setBody(content);
        layout.setActions(actions);

        btAlterarSenha.setOnAction((ActionEvent event) -> {
            dialog.show();
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Excluir Conta">
        JFXDialogLayout layoutExc = new JFXDialogLayout();
        JFXDialog dialogExc = new JFXDialog(StackPane, layoutExc, JFXDialog.DialogTransition.CENTER);

        JFXButton btCancelarD = new JFXButton("Cancelar");
        btCancelarD.setTextFill(Paint.valueOf("#29B6F6"));
        btCancelarD.setOnAction((ActionEvent event) -> {
            dialogExc.close();
        });

        JFXButton btExcluirD = new JFXButton("Excluir");
        btExcluirD.setTextFill(Paint.valueOf("#FF0000"));
        btExcluirD.setOnAction((ActionEvent event) -> {
            dialogExc.close();
            new Thread(() -> {
                if ((int) Principal.realizarOperacao("ExcluirConta", Principal.getUser()) == 1) {
                    
                } else {
                    Platform.runLater(() -> {
                        JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Não foi possível excluir a conta.", "Ok", 3000, false, (MouseEvent event2) -> {
                            snackbar.close();
                        });
                        snackbar.enqueue(barEvent);
                    });
                }
            }).start();
        });

        LinkedList<Node> actions1 = new LinkedList<>();
        actions1.add(btCancelarD);
        actions1.add(btExcluirD);

        layoutExc.setHeading(new Text("Excluir conta?"));
        layoutExc.setBody(new Text("Após a exclusão a conta não poderá ser recuperada."));
        layoutExc.setActions(actions1);

        btExcluirConta.setOnAction((ActionEvent event) -> {
            dialogExc.show();
        });
        //</editor-fold>
    }
}
