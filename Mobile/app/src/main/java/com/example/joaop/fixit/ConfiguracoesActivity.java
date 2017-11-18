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

public class ConfiguracoesActivity extends AppCompatActivity {

    EditText etConfiguracoesNome, etConfiguracoesEmail, etConfiguracoesSenhaAtual, etConfiguracoesNovaSenha,
            etConfiguracoesConfirmarSenha;
    Button btConfiguracoesAlterarNome, btConfiguracoesAlterarSenha;
    Dados dados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        etConfiguracoesNome = findViewById(R.id.etConfiguracoesNome);
        etConfiguracoesEmail = findViewById(R.id.etConfiguracoesEmail);
        etConfiguracoesSenhaAtual = findViewById(R.id.etConfiguracoesSenhaAtual);
        etConfiguracoesNovaSenha = findViewById(R.id.etConfiguracoesNovaSenha);
        etConfiguracoesConfirmarSenha = findViewById(R.id.etConfiguracoesConfirmarNovaSenha);
        btConfiguracoesAlterarNome = findViewById(R.id.btConfiguracoesAlterarNome);
        btConfiguracoesAlterarSenha = findViewById(R.id.btConfiguracoesAlterarSenha);
        dados = (Dados) getApplicationContext();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btConfiguracoesAlterarNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etConfiguracoesNome.getText().toString().equals("")) {
                    final String nome = etConfiguracoesNome.getText().toString();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                dados.getOut().writeObject("MudarNome");
                                dados.getIn().readObject();
                                dados.getOut().writeObject(nome);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(dados, "Nome alterado com sucesso!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } catch (IOException | ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                } else {
                    etConfiguracoesNome.setError("Informe o nome!");
                    etConfiguracoesNome.requestFocus();
                }
            }
        });

        btConfiguracoesAlterarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(dados, "Show de bola", Toast.LENGTH_SHORT).show();
                Toast.makeText(dados, ""+dados.getUser().getSenha(), Toast.LENGTH_SHORT).show();
            }
        });
    }



}
