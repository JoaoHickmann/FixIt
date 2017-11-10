package com.example.joaop.fixit;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import Classes.Chamado;

public class PrincipalActivity extends AppCompatActivity {
    private Dados dados;
    private LinkedList<Chamado> todos_chamados, abertos, finalizados;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(PrincipalActivity.this, RealizarChamadasActivity.class);
                startActivity(it);
            }
        });

        dados = (Dados) getApplicationContext();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    attRecycler();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    attRecycler();
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    public void attDados() throws IOException, ClassNotFoundException{
        dados.getOut().writeObject("MeusChamados");
        todos_chamados = (LinkedList<Chamado>) dados.getIn().readObject();

        finalizados = new LinkedList<>();
        abertos = new LinkedList<>();

        for (Chamado chamado:todos_chamados){
            if (chamado.getStatus() == 3) {
                finalizados.add(chamado);
            } else {
                abertos.add(chamado);
            }
        }
    }

    public void attRecycler() throws IOException, ClassNotFoundException {
        attDados();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView rvChamados = ((ChamadosFragment)((ViewPagerAdapter)mViewPager.getAdapter()).getItem(0)).getRvChamados();

                ChamadoAdapter chamadoAdapter = new ChamadoAdapter(abertos, new ChamadoAdapter.ChamadoOnClickListener() {
                    @Override
                    public void onClickAluno(View view, int position) {
                        Toast.makeText(PrincipalActivity.this, "Chamado #"+abertos.get(position).getID_Chamado(), Toast.LENGTH_SHORT).show();
                    }
                });

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(PrincipalActivity.this);
                rvChamados.setLayoutManager(mLayoutManager);
                rvChamados.setItemAnimator(new DefaultItemAnimator());
                rvChamados.setAdapter(chamadoAdapter);

                rvChamados = ((ChamadosFragment)((ViewPagerAdapter)mViewPager.getAdapter()).getItem(1)).getRvChamados();

                chamadoAdapter = new ChamadoAdapter(finalizados, new ChamadoAdapter.ChamadoOnClickListener() {
                    @Override
                    public void onClickAluno(View view, int position) {
                        Toast.makeText(PrincipalActivity.this, "Chamado #"+finalizados.get(position).getID_Chamado(), Toast.LENGTH_SHORT).show();
                    }
                });

                mLayoutManager = new LinearLayoutManager(PrincipalActivity.this);
                rvChamados.setLayoutManager(mLayoutManager);
                rvChamados.setItemAnimator(new DefaultItemAnimator());
                rvChamados.setAdapter(chamadoAdapter);
            }
        });
    }

    public void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChamadosFragment(), "Abertos");
        adapter.addFragment(new ChamadosFragment(), "Finalizados");
        viewPager.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_sair) {
            startActivity(new Intent(PrincipalActivity.this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}