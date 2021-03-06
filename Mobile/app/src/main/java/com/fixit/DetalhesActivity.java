package com.fixit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.LinkedList;

import Classes.Chamado;
import Classes.Computador;
import Classes.Problema;

public class DetalhesActivity extends AppCompatActivity {
    private Dados dados;

    private EditText etAdministradorDetalhes, etCriacaoDetalhes, etFinalizacaoDetalhes, etObservacaoDetalhes;
    private Spinner spTipoProblemaDetalhes, spProblemaDetalhes, spSalaDetalhes, spComputadorDetalhes;

    private Chamado chamado;
    private LinkedList<Problema> problemas;
    private LinkedList<Computador> computadores;

    private boolean editavel = false;
    private boolean pronto = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dados = (Dados) getApplicationContext();

        etAdministradorDetalhes = findViewById(R.id.etAdministradorDetalhes);
        etCriacaoDetalhes = findViewById(R.id.etCriacaoDetalhes);
        etFinalizacaoDetalhes = findViewById(R.id.etFinalizacaoDetalhes);
        etObservacaoDetalhes = findViewById(R.id.etObservacaoDetalhes);
        spTipoProblemaDetalhes = findViewById(R.id.spTipoProblemaDetalhes);
        spProblemaDetalhes = findViewById(R.id.spProblemaDetalhes);
        spSalaDetalhes = findViewById(R.id.spSalaDetalhes);
        spComputadorDetalhes = findViewById(R.id.spComputadorDetalhes);

        Intent it = getIntent();
        if (it != null) {
            editavel = it.getBooleanExtra("Editavel", false);

            chamado = (Chamado) it.getSerializableExtra("Chamado");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            setTitle(getString(R.string.chamado) + " #" + chamado.getID_Chamado());

            etAdministradorDetalhes.setText(chamado.getNome_Administrador());
            etCriacaoDetalhes.setText(sdf.format(chamado.getData_Inicial()));
            etFinalizacaoDetalhes.setText(chamado.getData_Final() != null ? sdf.format(chamado.getData_Final()) : "");
            etObservacaoDetalhes.setText(chamado.getObservacao());

            spTipoProblemaDetalhes.setSelection(chamado.getProblema().getTipo() - 1);
        }

        if (editavel) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    problemas = (LinkedList<Problema>) dados.obterLista("Problemas");
                    computadores = (LinkedList<Computador>) dados.obterLista("Computadores");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LinkedList<String> nomes_problemas = new LinkedList<>();

                            for (Problema problema : problemas) {
                                if (problema.getTipo() == chamado.getProblema().getTipo()) {
                                    nomes_problemas.add(problema.getDescricao());
                                }
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(DetalhesActivity.this, android.R.layout.simple_spinner_dropdown_item, nomes_problemas);
                            spProblemaDetalhes.setAdapter(adapter);

                            spProblemaDetalhes.setSelection(adapter.getPosition(chamado.getProblema().getDescricao()));

                            LinkedList<String> nomes_salas = new LinkedList<>();

                            for (Computador computador : computadores) {
                                if (!nomes_salas.contains(String.valueOf(computador.getSala()))) {
                                    nomes_salas.add(String.valueOf(computador.getSala()));
                                }
                            }

                            adapter = new ArrayAdapter<>(DetalhesActivity.this, android.R.layout.simple_spinner_dropdown_item, nomes_salas);
                            spSalaDetalhes.setAdapter(adapter);

                            spSalaDetalhes.setSelection(adapter.getPosition(String.valueOf(chamado.getComputador().getSala())));

                            spTipoProblemaDetalhes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    LinkedList<String> nomes_problemas = new LinkedList<>();

                                    for (Problema problema : problemas) {
                                        if (problema.getTipo() == position + 1) {
                                            nomes_problemas.add(problema.getDescricao());
                                        }
                                    }

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(DetalhesActivity.this, android.R.layout.simple_spinner_dropdown_item, nomes_problemas);
                                    spProblemaDetalhes.setAdapter(adapter);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                            spSalaDetalhes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    LinkedList<String> nomes_computadores = new LinkedList<>();

                                    for (Computador computador : computadores) {
                                        if (String.valueOf(computador.getSala()).equals(spSalaDetalhes.getSelectedItem())) {
                                            nomes_computadores.add(String.valueOf(computador.getNumero()));
                                        }
                                    }

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(DetalhesActivity.this, android.R.layout.simple_spinner_dropdown_item, nomes_computadores);
                                    spComputadorDetalhes.setAdapter(adapter);

                                    spComputadorDetalhes.setSelection(adapter.getPosition(String.valueOf(chamado.getComputador().getNumero())) == -1 ? 0 : adapter.getPosition(String.valueOf(chamado.getComputador().getNumero())));
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                    });
                }
            }).start();
        } else {
            LinkedList<String> tipo = new LinkedList<>();
            tipo.add(chamado.getProblema().getTipo() == 1 ? "Hardware" : "Software");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(DetalhesActivity.this, android.R.layout.simple_spinner_dropdown_item, tipo);
            spTipoProblemaDetalhes.setAdapter(adapter);

            LinkedList<String> problema = new LinkedList<>();
            problema.add(chamado.getProblema().getDescricao());
            adapter = new ArrayAdapter<>(DetalhesActivity.this, android.R.layout.simple_spinner_dropdown_item, problema);
            spProblemaDetalhes.setAdapter(adapter);

            LinkedList<String> sala = new LinkedList<>();
            sala.add(String.valueOf(chamado.getComputador().getSala()));
            adapter = new ArrayAdapter<>(DetalhesActivity.this, android.R.layout.simple_spinner_dropdown_item, sala);
            spSalaDetalhes.setAdapter(adapter);

            LinkedList<String> computador = new LinkedList<>();
            computador.add(String.valueOf(chamado.getComputador().getNumero()));
            adapter = new ArrayAdapter<>(DetalhesActivity.this, android.R.layout.simple_spinner_dropdown_item, computador);
            spComputadorDetalhes.setAdapter(adapter);

            etObservacaoDetalhes.setFocusable(false);
            etObservacaoDetalhes.setCursorVisible(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (editavel) {
            getMenuInflater().inflate(R.menu.menu_detalhes, menu);
            return true;
        } else {
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_salvar_detalhes:
                if (spProblemaDetalhes.getCount() != 0 && spSalaDetalhes.getCount() != 0 && spComputadorDetalhes.getCount() != 0 && pronto) {
                    pronto = false;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (Problema problema : problemas) {
                                if (problema.getTipo() == spTipoProblemaDetalhes.getSelectedItemPosition() + 1 && problema.getDescricao() == spProblemaDetalhes.getSelectedItem()) {
                                    chamado.setProblema(problema);
                                    break;
                                }
                            }

                            for (Computador computador : computadores) {
                                if (computador.getSala() == Integer.valueOf((String) spSalaDetalhes.getSelectedItem())
                                        && computador.getNumero() == Integer.valueOf((String) spComputadorDetalhes.getSelectedItem())) {
                                    chamado.setComputador(computador);
                                    break;
                                }
                            }

                            chamado.setObservacao(etObservacaoDetalhes.getText().toString());

                            atualizarChamado(chamado);
                            pronto = true;
                        }
                    }).start();
                }
                return true;
            case R.id.action_excluir_detalhes:
                AlertDialog.Builder builder = new AlertDialog.Builder(DetalhesActivity.this);
                builder.setTitle(R.string.excluir_chamado)
                        .setMessage(R.string.confirmar_excluir_chamado)
                        .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        LinkedList<Chamado> excluir = new LinkedList<>();
                                        excluir.add(chamado);
                                        if ((int) dados.realizarOperacao("ExcluirChamado", excluir) != 0) {
                                            setResult(1);
                                            finish();
                                        } else {
                                            final Snackbar snackbar = Snackbar.make(findViewById(R.id.clDetalhes), R.string.chamado_excluido_fail, Snackbar.LENGTH_LONG);
                                            snackbar.setAction(R.string.ok, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    snackbar.dismiss();
                                                }
                                            });
                                            snackbar.show();
                                        }
                                    }
                                }).start();
                            }
                        })
                        .setNegativeButton(R.string.nao, null);
                AlertDialog alerta = builder.create();
                alerta.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void atualizarChamado(final Chamado chamado) {
        if ((int) dados.realizarOperacao("AtualizarChamado", chamado) == 1) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snackbar = Snackbar.make(findViewById(R.id.clDetalhes), R.string.chamado_atualizado, Snackbar.LENGTH_LONG);
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
                    final Snackbar snackbar = Snackbar.make(findViewById(R.id.clDetalhes), R.string.chamado_atualizado_fail, Snackbar.LENGTH_LONG);
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
}
