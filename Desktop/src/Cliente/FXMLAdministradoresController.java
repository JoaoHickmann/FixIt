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
import javafx.application.Platform;
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
    private JFXDialog dialogExc;
    private JFXDialog dialogAdd;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //<editor-fold defaultstate="collapsed" desc="attDados">
        new Thread(() -> {
            administradores = (LinkedList<Usuario>) Principal.obterLista("Administradores");
            for (Usuario usuario : administradores) {
                cmbEmail.getItems().add(usuario.getEmail());
            }
            cmbEmail.getSelectionModel().selectFirst();
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

        //<editor-fold defaultstate="collapsed" desc="EmailOnChange">
        cmbEmail.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            tfNome.setText(administradores.get(newValue.intValue()).getNome());
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
            excluirAdministrador(administradores.get(cmbEmail.getSelectionModel().getSelectedIndex()));
            dialogExc.close();
        });

        LinkedList<Node> actions = new LinkedList<>();
        actions.add(btCancelarD);
        actions.add(btExcluirD);

        layoutExc.setHeading(new Text("Deseja realmente excluir?"));
        layoutExc.setActions(actions);

        btExcluir.setOnAction((ActionEvent event) -> {
            if (cmbEmail.getValue() != null) {
                dialogExc.show();
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Adicionar">
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

        ValidatorBase senhaTamanhoValidator = new ValidatorBase("Máximo de 20 caracteres.") {
            @Override
            protected void eval() {
                TextInputControl textField = (TextInputControl) srcControl.get();

                hasErrors.set(textField.getText().length() > 20);
            }
        };

        JFXDialogLayout layoutAdd = new JFXDialogLayout();
        dialogAdd = new JFXDialog(StackPane, layoutAdd, JFXDialog.DialogTransition.CENTER);

        AnchorPane content = new AnchorPane();

        JFXTextField tfNomeD = new JFXTextField();
        tfNomeD.setFocusColor(Paint.valueOf("#29B6F6"));
        tfNomeD.setPromptText("Nome");
        tfNomeD.setLabelFloat(true);
        tfNomeD.setLayoutY(10.0);
        tfNomeD.getValidators().add(requiredValidator);
        tfNomeD.setOnKeyReleased((event) -> {
            tfNomeD.validate();
        });

        JFXTextField tfEmailD = new JFXTextField();
        tfEmailD.setFocusColor(Paint.valueOf("#29B6F6"));
        tfEmailD.setPromptText("Email");
        tfEmailD.setLabelFloat(true);
        tfEmailD.setLayoutY(70.0);
        tfEmailD.getValidators().add(emailValidator);
        tfEmailD.getValidators().add(emailDisponivelValidator);
        tfEmailD.setOnKeyReleased((event) -> {
            tfEmailD.validate();
        });

        JFXPasswordField pfSenhaD = new JFXPasswordField();
        pfSenhaD.setFocusColor(Paint.valueOf("#29B6F6"));
        pfSenhaD.setPromptText("Senha");
        pfSenhaD.setLabelFloat(true);
        pfSenhaD.setLayoutY(130.0);
        pfSenhaD.getValidators().add(senhaValidator);
        pfSenhaD.getValidators().add(senhaTamanhoValidator);
        pfSenhaD.setOnKeyReleased((event) -> {
            pfSenhaD.validate();
        });

        ValidatorBase confirmaSenhaValidator = new ValidatorBase("Senha diferente.") {
            @Override
            protected void eval() {
                TextInputControl textField = (TextInputControl) srcControl.get();

                hasErrors.set(!textField.getText().equals(pfSenhaD.getText()));
            }
        };

        JFXPasswordField pfConfirmaSenhaD = new JFXPasswordField();
        pfConfirmaSenhaD.setPromptText("Confirmar Senha");
        pfConfirmaSenhaD.setFocusColor(Paint.valueOf("#29B6F6"));
        pfConfirmaSenhaD.setLabelFloat(true);
        pfConfirmaSenhaD.setLayoutY(190.0);
        pfConfirmaSenhaD.getValidators().add(confirmaSenhaValidator);
        pfConfirmaSenhaD.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                pfConfirmaSenhaD.validate();
            }
        });

        AnchorPane.setLeftAnchor(tfNomeD, 0.0);
        AnchorPane.setRightAnchor(tfNomeD, 0.0);

        AnchorPane.setLeftAnchor(tfEmailD, 0.0);
        AnchorPane.setRightAnchor(tfEmailD, 0.0);

        AnchorPane.setLeftAnchor(pfSenhaD, 0.0);
        AnchorPane.setRightAnchor(pfSenhaD, 0.0);

        AnchorPane.setLeftAnchor(pfConfirmaSenhaD, 0.0);
        AnchorPane.setRightAnchor(pfConfirmaSenhaD, 0.0);

        content.getChildren().add(tfNomeD);
        content.getChildren().add(tfEmailD);
        content.getChildren().add(pfSenhaD);
        content.getChildren().add(pfConfirmaSenhaD);

        JFXButton btCancelarD1 = new JFXButton("Cancelar");
        btCancelarD1.setTextFill(Paint.valueOf("#FF0000"));
        btCancelarD1.setOnAction((ActionEvent event) -> {
            dialogAdd.close();
        });

        JFXButton btAdicionarD1 = new JFXButton("Adicionar");
        btAdicionarD1.setTextFill(Paint.valueOf("#29B6F6"));
        btAdicionarD1.setOnAction((ActionEvent event) -> {
            tfNomeD.validate();
            tfEmailD.validate();
            pfSenhaD.validate();
            pfConfirmaSenhaD.validate();
            if (tfNomeD.validate() && tfEmailD.validate() && pfSenhaD.validate() && pfConfirmaSenhaD.validate()) {
                Usuario usuario = new Usuario(tfNomeD.getText(), tfEmailD.getText(), new Criptografia(tfEmailD.getText().charAt(0)).criptografar(pfSenhaD.getText()), true);
                adicionarAdministrador(usuario);
                dialogAdd.close();
            }
        });

        LinkedList<Node> actions1 = new LinkedList<>();
        actions1.add(btCancelarD1);
        actions1.add(btAdicionarD1);

        layoutAdd.setHeading(new Text("Adicionar administrador"));
        layoutAdd.setBody(content);
        layoutAdd.setActions(actions1);

        btAdicionar.setOnAction((ActionEvent event) -> {
            dialogAdd.show();
        });
        //</editor-fold>
    }

    public void excluirAdministrador(Usuario usuario) {
        new Thread(() -> {
            if ((int) Principal.realizarOperacao("ExcluirAdministrador", usuario) == 1) {
                Platform.runLater(() -> {
                    tfNome.setText("");
                    cmbEmail.getItems().remove(administradores.indexOf(usuario));
                    administradores.remove(usuario);
                    JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Administrador excluido.", "Ok", 3000, false, (MouseEvent event) -> {
                        snackbar.close();
                    });
                    snackbar.enqueue(barEvent);
                });
            } else {
                Platform.runLater(() -> {
                    JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Não foi possível excluir o administrador.", "Tentar novamente", 3000, false, (MouseEvent event) -> {
                        excluirAdministrador(usuario);
                        snackbar.close();
                    });
                    snackbar.enqueue(barEvent);
                });
            }
        }).start();
    }

    public void adicionarAdministrador(Usuario usuario) {
        new Thread(() -> {
            Usuario user = (Usuario) Principal.realizarOperacao("CadastrarUsuario", usuario);
            if (user != null) {
                administradores.add(user);
                Platform.runLater(() -> {
                    cmbEmail.getItems().add(user.getEmail());
                    cmbEmail.getSelectionModel().selectLast();
                    JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Administrador adicionado.", "Ok", 3000, false, (MouseEvent event) -> {
                        snackbar.close();
                    });
                    snackbar.enqueue(barEvent);
                });
            } else {
                Platform.runLater(() -> {
                    JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Não foi possível adicionar o adminitrador.", "Tentar novamete", 3000, false, (MouseEvent event) -> {
                        adicionarAdministrador(usuario);
                        snackbar.close();
                    });
                    snackbar.enqueue(barEvent);
                });
            }
        }).start();
    }
}
