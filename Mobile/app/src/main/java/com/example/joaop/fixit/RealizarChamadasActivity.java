package com.example.joaop.fixit;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.LinkedList;

import Classes.Computador;
import Classes.Problema;

public class RealizarChamadasActivity extends AppCompatActivity {

    ImageButton btSalvar;
    Spinner spTipoProblema, spProblema, spSala, spComputador;
    LinkedList <Problema> problemas;
    LinkedList <Computador> computadores;
    Dados dados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realizar_chamadas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btSalvar = findViewById(R.id.btSalvar);
        spTipoProblema = findViewById(R.id.spTipoProblema);
        spProblema = findViewById(R.id.spProblema);
        spSala = findViewById(R.id.spSala);
        spComputador = findViewById(R.id.spComputador);
        dados = (Dados)getApplicationContext();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dados.getOut().writeObject("Problemas");
                    problemas = (LinkedList<Problema>) dados.getIn().readObject();
                    dados.getOut().writeObject("Computadores");
                    computadores = (LinkedList<Computador>) dados.getIn().readObject();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            spTipoProblema.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    LinkedList<String> nomes_problemas = new LinkedList<>();

                                    for (Problema problema : problemas) {
                                        if (problema.getTipo() == position+1) {
                                            nomes_problemas.add(problema.getDescricao());
                                        }
                                    }

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(RealizarChamadasActivity.this, android.R.layout.simple_spinner_item, nomes_problemas);
                                    spProblema.setAdapter(adapter);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                            spSala.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    spSala.getSelectedItem();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                    });

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();

        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

}
