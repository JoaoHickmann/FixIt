package com.example.joaop.fixit;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_login);

        dados = (Dados) getApplicationContext();

        etEmail = (EditText) findViewById(R.id.etEmail);
        etSenha = (EditText) findViewById(R.id.etSenha);

        btEntrar = (Button) findViewById(R.id.btEntrar);
        btRegistrar = (Button) findViewById(R.id.btRegistrar);

        btEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dados.getOut().writeObject("Login");
                            dados.getIn().readObject();
                            String senha = new Criptografia(etEmail.getText().toString().charAt(0)).criptografar(etSenha.getText().toString());

                            Usuario usuario = new Usuario(etEmail.getText().toString(), senha);
                            dados.getOut().writeObject(usuario);
                            usuario = (Usuario) dados.getIn().readObject();

                            if (usuario == null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this, "Errou", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(LoginActivity.this, PrincipalActivity.class));
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket servidor = new Socket();
                    servidor.setSoTimeout(2000);


//                    servidor.connect();


                    dados.setServidor(servidor);
                    dados.setIn(new ObjectInputStream(servidor.getInputStream()));
                    dados.setOut(new ObjectOutputStream(servidor.getOutputStream()));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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
                                    });

                            AlertDialog alerta = builder.create();
                            alerta.show();
                        }
                    });
                }


            }
        }).start();
    }
}
