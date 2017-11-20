package com.fixit;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

public class ConfiguracoesActivity extends AppCompatActivity {

    TextInputLayout tilNomeConfiguracoes, tilSenhaAtualConfiguracoes, tilNovaSenhaConfiguracoes, tilConfirmarSenhaConfiguracoes;
    EditText etNomeConfiguracoes, etEmailConfiguracoes, etSenhaAtualConfiguracoes, etNovaSenhaConfiguracoes,
            etConfirmarSenhaConfiguracoes;
    Button btAlterarNomeConfiguracoes, btAlterarSenhaConfiguracoes;
    Dados dados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tilNomeConfiguracoes = findViewById(R.id.tilNomeConfiguracoes);
        tilSenhaAtualConfiguracoes = findViewById(R.id.tilSenhaAtualConfiguracoes);
        tilNovaSenhaConfiguracoes = findViewById(R.id.tilNovaSenhaConfiguracoes);
        tilConfirmarSenhaConfiguracoes = findViewById(R.id.tilConfirmarSenhaConfiguracoes);

        etNomeConfiguracoes = findViewById(R.id.etNomeConfiguracoes);
        etEmailConfiguracoes = findViewById(R.id.etEmailConfiguracoes);
        etSenhaAtualConfiguracoes = findViewById(R.id.etSenhaAtualConfiguracoes);
        etNovaSenhaConfiguracoes = findViewById(R.id.etNovaSenhaConfiguracoes);
        etConfirmarSenhaConfiguracoes = findViewById(R.id.etConfirmarNovaSenhaConfiguracoes);

        btAlterarNomeConfiguracoes = findViewById(R.id.btAlterarNomeConfiguracoes);
        btAlterarSenhaConfiguracoes = findViewById(R.id.btAlterarSenhaConfiguracoes);
        dados = (Dados) getApplicationContext();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etEmailConfiguracoes.setText(dados.getUser().getEmail());
        btAlterarNomeConfiguracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarCampos(etNomeConfiguracoes);

                if (validarCampos(etNomeConfiguracoes)) {
                    final String nome = etNomeConfiguracoes.getText().toString();

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

                }
            }
        });

        btAlterarSenhaConfiguracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String senha = new Criptografia(etEmailConfiguracoes.getText().toString().charAt(0)).criptografar(etSenhaAtualConfiguracoes.getText().toString());
                final String novaSenha = new Criptografia(etEmailConfiguracoes.getText().toString().charAt(0)).criptografar(etNovaSenhaConfiguracoes.getText().toString());
                if (dados.getUser().getSenha().equals(senha)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                dados.getOut().writeObject("MudarSenha");
                                dados.getIn().readObject();
                                dados.getOut().writeObject(novaSenha);

                            } catch (IOException | ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(dados, "A senha atual está errada!", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
    private boolean validarCampos(EditText etValidar) {
        if (etValidar.equals(etNomeConfiguracoes)) {
            tilNomeConfiguracoes.setError(etNomeConfiguracoes.getText().toString().equals("") ? "Informe o nome." : null);
            return !etNomeConfiguracoes.getText().toString().equals("");
        }
        return false;
    }


}
