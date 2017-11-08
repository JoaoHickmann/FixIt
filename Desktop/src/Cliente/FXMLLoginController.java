package Cliente;

import Classes.Criptografia;
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
                try {
                    Principal.getSaida().writeObject("Login");
                    Principal.getEntrada().readObject();
                    
                    String senha = new Criptografia(tfEmail.getText().charAt(0)).criptografar(pfSenha.getText());
                    Usuario usuario = new Usuario(tfEmail.getText(), senha);
                    
                    Principal.getSaida().writeObject(usuario);
                    usuario = (Usuario) Principal.getEntrada().readObject();
                    
                    if (usuario != null && usuario.isAdministrador()) {
                        Principal.setUser(usuario);
                        
                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("FXMLPrincipal.fxml")), ((Node) event.getSource()).getScene().getWidth(), ((Node) event.getSource()).getScene().getHeight());
                        scene.getStylesheets().add(getClass().getResource("cssSnackbar.css").toExternalForm());
                        stage.setScene(scene);
                        stage.show();
                    } else if (usuario != null && !usuario.isAdministrador()) {
                        lblMensagem.setText("Usuário sem permissões de administrador!");
                    } else {
                        lblMensagem.setText("Email ou Senha incorreto!");
                    }
                } catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(FXMLLoginController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        
        pfSenha.setOnAction(entrar);
        btEntrar.setOnAction(entrar);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Esqueceu Senha">
        btSenha.setOnAction((event) -> {
            JFXDialogLayout layout = new JFXDialogLayout();
            JFXDialog dialog = new JFXDialog(stack, layout, JFXDialog.DialogTransition.CENTER);
            
            AnchorPane content = new AnchorPane();
            
            JFXTextField tfEmail = new JFXTextField();
            tfEmail.setFocusColor(Paint.valueOf("#29B6F6"));
            tfEmail.setPromptText("Email");
            tfEmail.setLabelFloat(true);
            tfEmail.setLayoutY(10.0);
            tfEmail.getValidators().add(emailValidator);
            tfEmail.setOnKeyReleased((event1) -> {
                tfEmail.validate();
            });
            
            AnchorPane.setLeftAnchor(tfEmail, 0.0);
            AnchorPane.setRightAnchor(tfEmail, 0.0);
            content.getChildren().add(tfEmail);
            
            JFXButton btCancelar = new JFXButton("Cancelar");
            btCancelar.setTextFill(Paint.valueOf("#FF0000"));
            btCancelar.setOnAction((ActionEvent event1) -> {
                dialog.close();
            });
            
            JFXButton btAdicionar = new JFXButton("Enviar email");
            btAdicionar.setTextFill(Paint.valueOf("#29B6F6"));
            btAdicionar.setOnAction((ActionEvent event1) -> {
                if (tfEmail.validate()) {
                    try {
                        Principal.getSaida().writeObject("EsqueceuSenha");
                        Principal.getEntrada().readObject();
                        Principal.getSaida().writeObject(tfEmail.getText());
                        if ((int) Principal.getEntrada().readObject() == 1) {
                            JFXSnackbar.SnackbarEvent barEvent = new JFXSnackbar.SnackbarEvent("Email enviado para " + tfEmail.getText() + "", "Ok", 3000, false, (MouseEvent event2) -> {
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
            
            layout.setHeading(new Text("Recuperação de senha"));
            layout.setBody(content);
            layout.setActions(actions);
            
            dialog.show();
        });
        //</editor-fold>
    }
}
