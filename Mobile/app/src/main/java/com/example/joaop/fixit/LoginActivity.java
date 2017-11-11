package com.example.joaop.fixit;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import Classes.Usuario;

public class LoginActivity extends AppCompatActivity {
    Dados dados;
    EditText etEmail, etSenha;
    Button btEntrar, btRegistrar;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_login);

        dados = (Dados) getApplicationContext();

        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);

        btEntrar = findViewById(R.id.btEntrar);
        btRegistrar = findViewById(R.id.btRegistrar);

        btEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String senha = new Criptografia(etEmail.getText().toString().charAt(0)).criptografar(etSenha.getText().toString());
                            Usuario usuario = new Usuario(etEmail.getText().toString(), senha);

                            dados.getOut().writeObject("Login");
                            dados.getIn().readObject();
                            dados.getOut().writeObject(usuario);
                            usuario = (Usuario) dados.getIn().readObject();

                            dados.setUser(usuario);

                            if (usuario == null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this, "Usuário ou senha incorreta!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(LoginActivity.this, PrincipalActivity.class));
                                        finish();
                                    }
                                });
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        conectar();
    }

    public void conectar() {
        progress = new ProgressDialog(LoginActivity.this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setMessage("Conectando");
        progress.setTitle("Conectando-se ao servidor");
        progress.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket servidor = new Socket();
                    servidor.setSoTimeout(2000);
//                    servidor.connect();
                    servidor.setSoTimeout(3000);
                    servidor.connect(new InetSocketAddress("192.168.0.200", 12345), 3000);

                    dados.setServidor(servidor);
                    dados.setIn(new ObjectInputStream(servidor.getInputStream()));
                    dados.setOut(new ObjectOutputStream(servidor.getOutputStream()));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            btEntrar.setEnabled(true);
                            btRegistrar.setEnabled(true);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setTitle("Não foi possivel se conectar ao servidor")
                                    .setMessage("Deseja se conectar novamente?")
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
