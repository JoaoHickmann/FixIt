package com.example.joaop.fixit;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.LinkedList;

import Classes.Chamado;
import Classes.Computador;
import Classes.Problema;

public class AdicionarChamadoActivity extends AppCompatActivity {
    Dados dados;

    EditText etObservacao;
    Spinner spTipoProblema, spProblema, spSala, spComputador;

    LinkedList<Problema> problemas;
    LinkedList<Computador> computadores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_chamado);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dados = (Dados) getApplicationContext();

        spTipoProblema = findViewById(R.id.spTipoProblemaAdicionar);
        spProblema = findViewById(R.id.spProblemaAdicionar);
        spSala = findViewById(R.id.spSalaAdicionar);
        spComputador = findViewById(R.id.spComputadorAdicionar);
        etObservacao = findViewById(R.id.etObservacaoAdicionar);

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
                            if (problema.getTipo() == 1) {
                                nomes_problemas.add(problema.getDescricao());
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(AdicionarChamadoActivity.this, android.R.layout.simple_spinner_dropdown_item, nomes_problemas);
                        spProblema.setAdapter(adapter);

                        LinkedList<String> nomes_salas = new LinkedList<>();

                        for (Computador computador : computadores) {
                            if (!nomes_salas.contains(String.valueOf(computador.getSala()))) {
                                nomes_salas.add(String.valueOf(computador.getSala()));
                            }
                        }

                        adapter = new ArrayAdapter<String>(AdicionarChamadoActivity.this, android.R.layout.simple_spinner_dropdown_item, nomes_salas);
                        spSala.setAdapter(adapter);

                        spTipoProblema.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                LinkedList<String> nomes_problemas = new LinkedList<>();

                                for (Problema problema : problemas) {
                                    if (problema.getTipo() == position + 1) {
                                        nomes_problemas.add(problema.getDescricao());
                                    }
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(AdicionarChamadoActivity.this, android.R.layout.simple_spinner_dropdown_item, nomes_problemas);
                                spProblema.setAdapter(adapter);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        spSala.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                LinkedList<String> nomes_computadores = new LinkedList<>();

                                for (Computador computador : computadores) {
                                    if (String.valueOf(computador.getSala()).equals(spSala.getSelectedItem())) {
                                        nomes_computadores.add(String.valueOf(computador.getNumero()));
                                    }
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(AdicionarChamadoActivity.this, android.R.layout.simple_spinner_dropdown_item, nomes_computadores);
                                spComputador.setAdapter(adapter);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_realizar_chamado, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_adicionar_adicionar:
                if (spProblema.getCount() != 0 && spSala.getCount() != 0 && spComputador.getCount() != 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Computador computador = null;
                            for (Computador computador1 : computadores) {
                                if (computador1.getSala() == Integer.valueOf(spSala.getSelectedItem().toString()) && computador1.getNumero() == Integer.valueOf(spComputador.getSelectedItem().toString())) {
                                    computador = computador1;
                                    break;
                                }
                            }

                            Problema problema = null;
                            for (Problema problema1 : problemas) {
                                if (problema1.getTipo() == spTipoProblema.getSelectedItemPosition() + 1 && problema1.getDescricao().equals(spProblema.getSelectedItem().toString())) {
                                    problema = problema1;
                                    break;
                                }
                            }

                            Chamado chamado = new Chamado(computador, problema, etObservacao.getText().toString());

                            if ((int) dados.realizarOperacao("AdicionarChamado", chamado) == 1) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setResult(1);
                                        finish();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final Snackbar snackbar = Snackbar.make(findViewById(R.id.cl_adicionar), R.string.chamado_adicionado_fail, Snackbar.LENGTH_LONG);
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
                    return true;
                } else {
                    return false;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
