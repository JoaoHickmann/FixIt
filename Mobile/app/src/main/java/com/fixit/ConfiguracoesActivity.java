package com.fixit;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConfiguracoesActivity extends AppCompatActivity {
    Dados dados;

    TextInputLayout tilNomeConfiguracoes, tilSenhaAtualDialog, tilNovaSenhaDialog, tilConfirmarSenhaDialog;
    EditText etNomeConfiguracoes, etEmailConfiguracoes, etSenhaAtualDialog, etNovaSenhaDialog, etConfirmarSenhaDialog;
    Button btAlterarSenhaConfiguracoes, btExcluirContaConfiguracoes;

    private boolean pronto = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dados = (Dados) getApplicationContext();

        tilNomeConfiguracoes = findViewById(R.id.tilNomeConfiguracoes);

        etNomeConfiguracoes = findViewById(R.id.etNomeConfiguracoes);
        etEmailConfiguracoes = findViewById(R.id.etEmailConfiguracoes);

        btAlterarSenhaConfiguracoes = findViewById(R.id.btAlterarSenhaConfiguracoes);
        btExcluirContaConfiguracoes = findViewById(R.id.btExcluirContaConfiguracoes);

        etNomeConfiguracoes.setText(dados.getUser().getNome());
        etEmailConfiguracoes.setText(dados.getUser().getEmail());

        etNomeConfiguracoes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validarCampos(etNomeConfiguracoes);
            }
        });

        btAlterarSenhaConfiguracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ConfiguracoesActivity.this);

                final View dialogView = getLayoutInflater().inflate(R.layout.dialog_trocar_senha, null);

                tilSenhaAtualDialog = dialogView.findViewById(R.id.tilSenhaAtualDialog);
                tilNovaSenhaDialog = dialogView.findViewById(R.id.tilNovaSenhaDialog);
                tilConfirmarSenhaDialog = dialogView.findViewById(R.id.tilConfirmarSenhaDialog);

                etSenhaAtualDialog = dialogView.findViewById(R.id.etSenhaAtualDialog);
                etNovaSenhaDialog = dialogView.findViewById(R.id.etNovaSenhaDialog);
                etConfirmarSenhaDialog = dialogView.findViewById(R.id.etConfirmarSenhaDialog);

                etSenhaAtualDialog.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            validarCampos(etSenhaAtualDialog);
                        }
                    }
                });
                etNovaSenhaDialog.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        validarCampos(etNovaSenhaDialog);
                    }
                });
                etConfirmarSenhaDialog.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        validarCampos(etConfirmarSenhaDialog);
                    }
                });

                builder.setTitle(R.string.alteracao_senha)
                        .setView(dialogView)
                        .setPositiveButton(R.string.alterar, null)
                        .setNegativeButton(R.string.cancelar, null);

                final AlertDialog alerta = builder.create();

                alerta.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        alerta.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                validarCampos(etNovaSenhaDialog);
                                validarCampos(etConfirmarSenhaDialog);
                                if (!validarCampos(etSenhaAtualDialog)) {
                                    etSenhaAtualDialog.requestFocus();
                                }
                                if (validarCampos(etSenhaAtualDialog) && validarCampos(etNovaSenhaDialog) && validarCampos(etConfirmarSenhaDialog)) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String senha = new Criptografia(dados.getUser().getEmail().charAt(0)).criptografar(etNovaSenhaDialog.getText().toString());

                                            if ((int) dados.realizarOperacao("MudarSenha", senha) == 1) {
                                                dados.getUser().setSenha(senha);

                                                SharedPreferences sharedPref = getSharedPreferences("com.fixit", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPref.edit();
                                                editor.putString("Senha", senha);
                                                editor.apply();

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        final Snackbar snackbar = Snackbar.make(findViewById(R.id.clConfiguracoes), R.string.senha_alterada, Snackbar.LENGTH_LONG);
                                                        snackbar.setAction(R.string.ok, new View.OnClickListener() {
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
                                                        final Snackbar snackbar = Snackbar.make(findViewById(R.id.clConfiguracoes), R.string.senha_alterada_fail, Snackbar.LENGTH_LONG);
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

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    alerta.dismiss();
                                                }
                                            });
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

        btExcluirContaConfiguracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ConfiguracoesActivity.this);
                builder.setTitle(R.string.excluir_conta_dialog)
                        .setMessage(R.string.excluir_dialog_message)
                        .setPositiveButton(R.string.excluir, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if ((int) dados.realizarOperacao("ExcluirConta", dados.getUser()) == 1) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    dados.setUser(null);

                                                    SharedPreferences sharedPref = getSharedPreferences("com.fixit", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPref.edit();
                                                    editor.putString("Username", null);
                                                    editor.putString("Senha", null);
                                                    editor.apply();

                                                    Intent it = new Intent(ConfiguracoesActivity.this, LoginActivity.class);
                                                    it.putExtra("Exclusao", true);
                                                    startActivity(it);
                                                    finish();
                                                }
                                            });
                                        } else {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    final Snackbar snackbar = Snackbar.make(findViewById(R.id.clConfiguracoes), R.string.excluir_conta_fail, Snackbar.LENGTH_LONG);
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
                                }).start();
                            }
                        })
                        .setNegativeButton(R.string.cancelar, null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_configuracoes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_salvar_configuracoes:
                if (validarCampos(etNomeConfiguracoes) && !etNomeConfiguracoes.getText().toString().equals(dados.getUser().getNome()) && pronto) {
                    pronto = false;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if ((int) dados.realizarOperacao("MudarNome", etNomeConfiguracoes.getText().toString()) == 1) {
                                dados.getUser().setNome(etNomeConfiguracoes.getText().toString());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final Snackbar snackbar = Snackbar.make(findViewById(R.id.clConfiguracoes), R.string.nome_alterado, Snackbar.LENGTH_LONG);
                                        snackbar.setAction(R.string.ok, new View.OnClickListener() {
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
                                        final Snackbar snackbar = Snackbar.make(findViewById(R.id.clConfiguracoes), R.string.nome_alterador_fail, Snackbar.LENGTH_LONG);
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
                            pronto = true;
                        }
                    }).start();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean validarCampos(EditText etValidar) {
        boolean valido;

        switch (etValidar.getId()) {
            case R.id.etNomeConfiguracoes:
                valido = etNomeConfiguracoes.getText().toString().length() > 0;

                tilNomeConfiguracoes.setError(valido ? null : getString(R.string.informe_nome));
                if (!valido) {
                    etNomeConfiguracoes.requestFocus();
                }

                return valido;
            case R.id.etSenhaAtualDialog:
                String senha = new Criptografia(dados.getUser().getEmail().charAt(0)).criptografar(etSenhaAtualDialog.getText().toString());
                valido = senha.equals(dados.getUser().getSenha());

                tilSenhaAtualDialog.setError(valido ? null : getString(R.string.senha_incorreta));

                return valido;
            case R.id.etNovaSenhaDialog:
                valido = etNovaSenhaDialog.getText().toString().length() >= 6 && etNovaSenhaDialog.getText().toString().length() <= 20;

                if (valido) {
                    String caracteresD = "";
                    String alfabeto = "YPDe6FpaxH&yRNs+jMVBAOnShoEmg802tQ@r1i-$L%Jq*G3#9XTdW57lUCkzcubwZ4vKfI";

                    for (char letra : etNovaSenhaDialog.getText().toString().toCharArray()) {
                        if (!alfabeto.contains(String.valueOf(letra))) {
                            if (!caracteresD.contains(String.valueOf(letra))) {
                                caracteresD += letra;
                            }
                        }
                    }

                    valido = caracteresD.length() == 0;

                    tilNovaSenhaDialog.setError(valido ? null : (caracteresD + " " + (caracteresD.length() == 1 ? getString(R.string.nao_permitido_error) : getString(R.string.nao_permitido_error_plural))));
                    if (!valido) {
                        etNovaSenhaDialog.requestFocus();
                    }

                    return valido;
                } else {
                    tilNovaSenhaDialog.setError(getString(R.string.senha_tamanho_error));
                    etNovaSenhaDialog.requestFocus();

                    return false;
                }
            case R.id.etConfirmarSenhaDialog:
                valido = etConfirmarSenhaDialog.getText().toString().equals(etNovaSenhaDialog.getText().toString());

                tilConfirmarSenhaDialog.setError(valido ? null : getString(R.string.senha_diferente_error));
                if (!valido) {
                    etConfirmarSenhaDialog.requestFocus();
                }

                return valido;
            default:
                return false;
        }
    }
}
