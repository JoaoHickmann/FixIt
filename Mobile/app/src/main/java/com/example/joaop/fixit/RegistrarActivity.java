package com.example.joaop.fixit;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import Classes.Usuario;

public class RegistrarActivity extends AppCompatActivity {

    EditText etNomeRegistrar, etEmailRegistrar, etSenhaRegistrar, etConfirmarSenhaRegistrar;
    Button btRegistrar;
    Dados dados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        etNomeRegistrar = findViewById(R.id.etNomeRegistrar);
        etEmailRegistrar = findViewById(R.id.etEmailRegistrar);
        etSenhaRegistrar = findViewById(R.id.etSenhaRegistrar);
        etConfirmarSenhaRegistrar = findViewById(R.id.etSenhaRegistrar);
        btRegistrar = findViewById(R.id.btRegistrarRegistrar);
        dados = (Dados) getApplicationContext();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    btRegistrar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new Thread(new Runnable() {
                @Override
                public void run() {






                            try {
                                dados.getOut().writeObject("CadastrarUsuario");
                                dados.getIn().readObject();

                                String nome = etNomeRegistrar.getText().toString();
                                String email = etEmailRegistrar.getText().toString();
                                String senha = new Criptografia(etEmailRegistrar.getText().toString().charAt(0)).criptografar(etSenhaRegistrar.getText().toString());
                                Usuario usuario = new Usuario(nome, email, senha, false);
                                dados.getOut().writeObject(usuario);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                    Toast.makeText(dados, "Usuário adicionado com sucesso!", Toast.LENGTH_SHORT).show();
                                        etNomeRegistrar.setText("");
                                        etEmailRegistrar.setText("");
                                        etSenhaRegistrar.setText("");
                                        etConfirmarSenhaRegistrar.setText("");
                                    }
                                });


                            } catch (IOException | ClassNotFoundException e) {
                                e.printStackTrace();
                            }




                }
            }).start();
        }
    });

    }



}
