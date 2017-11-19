package com.example.joaop.fixit;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Classes.Usuario;

public class RegistrarActivity extends AppCompatActivity {
    Dados dados;

    TextInputLayout tilNomeRegistrar, tilEmailRegistrar, tilSenhaRegistrar, tilConfirmarSenhaRegistrar;
    EditText etNomeRegistrar, etEmailRegistrar, etSenhaRegistrar, etConfirmarSenhaRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dados = (Dados) getApplicationContext();

        tilNomeRegistrar = findViewById(R.id.tilNomeRegistrar);
        tilEmailRegistrar = findViewById(R.id.tilEmailRegistrar);
        tilSenhaRegistrar = findViewById(R.id.tilSenhaRegistrar);
        tilConfirmarSenhaRegistrar = findViewById(R.id.tilConfirmarSenhaRegistrar);

        etNomeRegistrar = findViewById(R.id.etNomeRegistrar);
        etEmailRegistrar = findViewById(R.id.etEmailRegistrar);
        etSenhaRegistrar = findViewById(R.id.etSenhaRegistrar);
        etConfirmarSenhaRegistrar = findViewById(R.id.etConfirmarSenhaRegistrar);

        etNomeRegistrar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validarCampos(etNomeRegistrar);
                }
            }
        });
        etEmailRegistrar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validarCampos(etEmailRegistrar);
                }
            }
        });
        etSenhaRegistrar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validarCampos(etSenhaRegistrar);
                }
            }
        });
        etConfirmarSenhaRegistrar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validarCampos(etConfirmarSenhaRegistrar);
                }
            }
        });
    }

    private boolean validarCampos(EditText etValidar) {
        if (etValidar.equals(etNomeRegistrar)) {
            tilNomeRegistrar.setError(etNomeRegistrar.getText().toString().equals("") ? "Informe o nome." : null);
            return !etNomeRegistrar.getText().toString().equals("");
        } else if (etValidar.equals(etEmailRegistrar)) {
            Pattern p = Pattern.compile("^[A-Za-z0-9-]+(\\-[A-Za-z0-9])*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9])");
            Matcher m = p.matcher(etEmailRegistrar.getText().toString());

            boolean emailCerto = m.find();

            if (emailCerto) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final int disponivel = (int) dados.realizarOperacao("EmailDisponivel", etEmailRegistrar.getText().toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tilEmailRegistrar.setError(disponivel != 1 ? "Email não disponível." : null);
                            }
                        });
                    }
                });
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return tilEmailRegistrar.getError() == null;
            } else {
                tilEmailRegistrar.setError("Email inválido.");
                return false;
            }

        } else if (etValidar.equals(etSenhaRegistrar)) {
            boolean tamanhoCerto = !(etSenhaRegistrar.getText().toString().length() > 20 || etSenhaRegistrar.getText().toString().length() < 6);
            if (tamanhoCerto) {
                String caracteresD = "";
                String alfabeto = "YPDe6FpaxH&yRNs+jMVBAOnShoEmg802tQ@r1i-$L%Jq*G3#9XTdW57lUCkzcubwZ4vKfI";

                for (char letra : etSenhaRegistrar.getText().toString().toCharArray()) {
                    if (!alfabeto.contains(String.valueOf(letra))) {
                        if (!caracteresD.contains(String.valueOf(letra))) {
                            caracteresD += letra;
                        }
                    }
                }

                tilSenhaRegistrar.setError(caracteresD.length() > 0 ? (caracteresD + " não permitido" + (caracteresD.length() == 1 ? "" : "s") + ".") : null);
                return caracteresD.length() == 0;
            } else {
                tilSenhaRegistrar.setError("Permitido somente 6-20 caracteres.");
                return false;
            }
        } else if (etValidar.equals(etConfirmarSenhaRegistrar)) {
            tilConfirmarSenhaRegistrar.setError(!etConfirmarSenhaRegistrar.getText().toString().equals(etSenhaRegistrar.getText().toString()) ? "Senha diferente." : null);
            return etConfirmarSenhaRegistrar.getText().toString().equals(etSenhaRegistrar.getText().toString());
        } else {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_registrar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_registrar:
                validarCampos(etConfirmarSenhaRegistrar);
                validarCampos(etSenhaRegistrar);
                validarCampos(etEmailRegistrar);
                validarCampos(etNomeRegistrar);
                if (validarCampos(etConfirmarSenhaRegistrar) && validarCampos(etSenhaRegistrar) && validarCampos(etEmailRegistrar) && validarCampos(etNomeRegistrar)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String senha = new Criptografia(etEmailRegistrar.getText().toString().charAt(0)).criptografar(etSenhaRegistrar.getText().toString());
                            Usuario usuario = new Usuario(etNomeRegistrar.getText().toString(), etEmailRegistrar.getText().toString(), senha, false);
                            usuario = (Usuario) dados.realizarOperacao("CadastrarUsuario", usuario);
                            dados.setUser(usuario);
                            if (usuario != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(RegistrarActivity.this, PrincipalActivity.class));
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final Snackbar snackbar = Snackbar.make(findViewById(R.id.clRegistrar), "Não foi possível cadastrar.", Snackbar.LENGTH_LONG);
                                        snackbar.setAction("Ok", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                snackbar.dismiss();
                                            }
                                        });
                                        snackbar.show();
                                    }
                                });
                            }
                        }
                    }).start();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
