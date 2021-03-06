package com.fixit;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Classes.Usuario;

public class RegistrarActivity extends AppCompatActivity {
    private Dados dados;

    private TextInputLayout tilNomeRegistrar, tilEmailRegistrar, tilSenhaRegistrar, tilConfirmarSenhaRegistrar;
    private EditText etNomeRegistrar, etEmailRegistrar, etSenhaRegistrar, etConfirmarSenhaRegistrar;

    private Timer timer = new Timer();
    private boolean emailValido = false, pronto = true, emailPronto = true;

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

        etNomeRegistrar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validarCampos(etNomeRegistrar);
            }
        });
        etEmailRegistrar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                emailPronto = false;
                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                emailValido = validarCampos(etEmailRegistrar);
                                emailPronto = true;
                            }
                        },
                        500
                );
            }
        });
        etSenhaRegistrar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validarCampos(etSenhaRegistrar);
            }
        });
        etConfirmarSenhaRegistrar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validarCampos(etConfirmarSenhaRegistrar);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_registrar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_registrar_registrar:
                boolean valido = validarCampos(etConfirmarSenhaRegistrar);
                valido = validarCampos(etSenhaRegistrar) && valido;

                while (!emailPronto) {
                }

                if (!emailValido) {
                    etEmailRegistrar.requestFocus();
                    if (tilEmailRegistrar.getError() == null) {
                        tilEmailRegistrar.setError(getString(R.string.email_invalido_error));
                    }
                }

                valido = emailValido && valido;
                valido = validarCampos(etNomeRegistrar) && valido;
                if (valido && pronto) {
                    pronto = false;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String senha = new Criptografia(etEmailRegistrar.getText().toString().charAt(0)).criptografar(etSenhaRegistrar.getText().toString());
                            Usuario usuario = new Usuario(etNomeRegistrar.getText().toString(), etEmailRegistrar.getText().toString(), senha, false);
                            registrar(usuario);
                            pronto = true;
                        }
                    }).start();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void registrar(Usuario usuario) {
        usuario = (Usuario) dados.realizarOperacao("CadastrarUsuario", usuario);
        dados.setUser(usuario);
        if (usuario != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(RegistrarActivity.this, PrincipalActivity.class));
                    finish();
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snackbar = Snackbar.make(findViewById(R.id.clRegistrar), R.string.cadastrar_usuario_fail, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.ok, new View.OnClickListener() {
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

    private boolean validarCampos(EditText etValidar) {
        boolean valido;

        switch (etValidar.getId()) {
            case R.id.etNomeRegistrar:
                valido = !etNomeRegistrar.getText().toString().equals("");

                tilNomeRegistrar.setError(valido ? null : getString(R.string.informe_nome));
                if (!valido) {
                    etNomeRegistrar.requestFocus();
                }
                return valido;
            case R.id.etEmailRegistrar:
                Pattern p = Pattern.compile("^[A-Za-z0-9-]+(\\-[A-Za-z0-9])*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9])");
                Matcher m = p.matcher(etEmailRegistrar.getText().toString());

                valido = m.find();

                if (valido && pronto) {
                    final int disponivel = (int) dados.realizarOperacao("EmailDisponivel", etEmailRegistrar.getText().toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tilEmailRegistrar.setError(disponivel == 1 ? null : getString(R.string.email_nao_disponivel_error));
                        }
                    });

                    return disponivel == 1;
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tilEmailRegistrar.setError(getString(R.string.email_invalido_error));
                        }
                    });
                    return false;
                }
            case R.id.etSenhaRegistrar:
                valido = etSenhaRegistrar.getText().toString().length() >= 6 && etSenhaRegistrar.getText().toString().length() <= 20;

                if (valido) {
                    String caracteresD = "";
                    String alfabeto = "YPDe6FpaxH&yRNs+jMVBAOnShoEmg802tQ@r1i-$L%Jq*G3#9XTdW57lUCkzcubwZ4vKfI";

                    for (char letra : etSenhaRegistrar.getText().toString().toCharArray()) {
                        if (!alfabeto.contains(String.valueOf(letra))) {
                            if (!caracteresD.contains(String.valueOf(letra))) {
                                caracteresD += letra;
                            }
                        }
                    }

                    valido = caracteresD.length() == 0;

                    tilSenhaRegistrar.setError(valido ? null : (caracteresD + " " + (caracteresD.length() == 1 ? getString(R.string.nao_permitido_error) : getString(R.string.nao_permitido_error_plural))));
                    if (!valido) {
                        etSenhaRegistrar.requestFocus();
                    }
                    return valido;
                } else {
                    tilSenhaRegistrar.setError(getString(R.string.senha_tamanho_error));
                    etSenhaRegistrar.requestFocus();
                    return false;
                }
            case R.id.etConfirmarSenhaRegistrar:
                valido = etConfirmarSenhaRegistrar.getText().toString().equals(etSenhaRegistrar.getText().toString());

                tilConfirmarSenhaRegistrar.setError(valido ? null : getString(R.string.senha_diferente_error));
                if (!valido) {
                    etConfirmarSenhaRegistrar.requestFocus();
                }
                return valido;
            default:
                return false;
        }
    }
}
