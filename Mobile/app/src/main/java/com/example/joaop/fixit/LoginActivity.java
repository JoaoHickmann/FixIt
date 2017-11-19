package com.example.joaop.fixit;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Inflater;

import Classes.Usuario;

public class LoginActivity extends AppCompatActivity {
    Dados dados;
    ProgressDialog progress;

    TextInputLayout tilEmailLogin, tilSenhaLogin, tilEmailDialog;
    EditText etEmailLogin, etSenhaLogin, etEmailDialog;
    CheckBox cbManterConectadoLogin;
    TextView tvEsqueceuSenhaLogin;
    Button btEntrarLogin, btRegistrarLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_login);

        dados = (Dados) getApplicationContext();

        tilEmailLogin = findViewById(R.id.tilEmailLogin);
        tilSenhaLogin = findViewById(R.id.tilSenhaLogin);

        etEmailLogin = findViewById(R.id.etEmailLogin);
        etSenhaLogin = findViewById(R.id.etSenhaLogin);

        cbManterConectadoLogin = findViewById(R.id.cbManterConectadoLogin);
        tvEsqueceuSenhaLogin = findViewById(R.id.tvEsqueceuSenhaLogin);

        btEntrarLogin = findViewById(R.id.btEntrarLogin);
        btRegistrarLogin = findViewById(R.id.btRegistrarLogin);

        etEmailLogin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validarCampos(etEmailLogin);
                }
            }
        });
        etSenhaLogin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validarCampos(etSenhaLogin);
                }
            }
        });

        tvEsqueceuSenhaLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                final View dialogView = getLayoutInflater().inflate(R.layout.dialog_esqueceu_senha, null);

                etEmailDialog = dialogView.findViewById(R.id.etEmailDialog);
                tilEmailDialog = dialogView.findViewById(R.id.tilEmailDialog);

                etEmailDialog.setText(etEmailLogin.getText().toString());
                etEmailDialog.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            validarCampos(etEmailDialog);
                        }
                    }
                });

                builder.setTitle("Recuperação de senha")
                        .setView(dialogView)
                        .setPositiveButton("Enviar email", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        });

                final AlertDialog alerta = builder.create();
                alerta.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        alerta.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (validarCampos(etEmailDialog)) {
                                    alerta.dismiss();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if ((int) dados.realizarOperacao("EsqueceuSenha", etEmailDialog.getText().toString()) == 1) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        final Snackbar snackbar = Snackbar.make(findViewById(R.id.clLogin), "Email enviado para " + etEmailDialog.getText().toString() + ".", Snackbar.LENGTH_LONG);
                                                        snackbar.setAction("Ok", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                snackbar.dismiss();
                                                            }
                                                        });
                                                        snackbar.show();
                                                    }
                                                });
                                            } else {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        final Snackbar snackbar = Snackbar.make(findViewById(R.id.clLogin), "Não foi possível enviar o email.", Snackbar.LENGTH_LONG);
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
                            }
                        });
                    }
                });
                alerta.show();
            }
        });

        btEntrarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarCampos(etEmailLogin);
                validarCampos(etSenhaLogin);
                if (validarCampos(etEmailLogin) && validarCampos(etSenhaLogin)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String senha = new Criptografia(etEmailLogin.getText().toString().charAt(0)).criptografar(etSenhaLogin.getText().toString());
                            Usuario usuario = new Usuario(etEmailLogin.getText().toString(), senha);

                            login(usuario);
                        }
                    }).start();
                }
            }
        });

        btRegistrarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LoginActivity.this, RegistrarActivity.class);
                startActivity(it);
            }
        });

        conectar();
    }

    private void login(Usuario usuario) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress = new ProgressDialog(LoginActivity.this);
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setIndeterminate(true);
                progress.setCancelable(false);
                progress.setMessage("Entrando");
                progress.show();
            }
        });

        usuario = (Usuario) dados.realizarOperacao("Login", usuario);

        if (usuario == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "Usuário ou senha incorreta!", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }
            });
        } else {
            dados.setUser(usuario);
            if (cbManterConectadoLogin.isChecked()) {
                SharedPreferences sharedPref = getSharedPreferences("com.example.joaop.fixit", dados.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("Username", usuario.getEmail());
                editor.putString("Senha", usuario.getSenha());
                editor.commit();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(LoginActivity.this, PrincipalActivity.class));
                    progress.dismiss();
                    finish();
                }
            });
        }
    }

    private boolean validarCampos(EditText etValidar) {
        Pattern p;
        Matcher m;
        boolean emailCerto;

        switch (etValidar.getId()) {
            case R.id.etEmailLogin:
                p = Pattern.compile("^[A-Za-z0-9-]+(\\-[A-Za-z0-9])*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9])");
                m = p.matcher(etEmailLogin.getText().toString());

                emailCerto = m.find();

                tilEmailLogin.setError(!emailCerto ? "Email inválido." : null);
                return emailCerto;
            case R.id.etEmailDialog:
                p = Pattern.compile("^[A-Za-z0-9-]+(\\-[A-Za-z0-9])*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9])");
                m = p.matcher(etEmailDialog.getText().toString());

                emailCerto = m.find();

                tilEmailDialog.setError(!emailCerto ? "Email inválido." : null);
                return emailCerto;
            case R.id.etSenhaLogin:
                tilSenhaLogin.setError(etSenhaLogin.getText().toString().equals("") ? "Informe a senha." : null);
                return !etSenhaLogin.getText().toString().equals("");
            default:
                return false;
        }
    }

    public void conectar() {
        progress = new ProgressDialog(LoginActivity.this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.setMessage("Conectando");
        progress.setTitle("Conectando-se ao servidor");
        progress.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket servidor = new Socket();
                    servidor.setSoTimeout(5000);
                    servidor.connect(new InetSocketAddress("192.168.0.200", 12345), 5000);

                    dados.setServidor(servidor);
                    dados.setIn(new ObjectInputStream(servidor.getInputStream()));
                    dados.setOut(new ObjectOutputStream(servidor.getOutputStream()));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                        }
                    });

                    SharedPreferences sharedPref = getSharedPreferences("com.example.joaop.fixit", dados.MODE_PRIVATE);
                    String username = sharedPref.getString("Username", null);
                    if (username != null) {
                        login(new Usuario(username, sharedPref.getString("Senha", null)));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setTitle("Não foi possivel se conectar ao servidor")
                                    .setMessage("Tentar novamente?")
                                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            conectar();
                                        }
                                    })
                                    .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            finish();
                                        }
                                    })
                                    .setCancelable(false);

                            AlertDialog alerta = builder.create();
                            progress.dismiss();
                            alerta.show();
                        }
                    });
                }
            }
        }).start();
    }
}