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
import java.util.LinkedList;

import Classes.Chamado;

public class PrincipalActivity extends AppCompatActivity {
    private Dados dados;
    static LinkedList<Chamado> chamados;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

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

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        dados = (Dados) getApplicationContext();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dados.getOut().writeObject("MeusChamados");
                    chamados = (LinkedList<Chamado>) dados.getIn().readObject();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_principal, container, false);

            LinkedList<Chamado> lista = new LinkedList<>();

            while (chamados == null) {
            }

            for (Chamado chamado : chamados) {
                if (getArguments().getInt(ARG_SECTION_NUMBER) == 2 && chamado.getStatus() == 3) {
                    lista.add(chamado);
                } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 1 && chamado.getStatus() != 3) {
                    lista.add(chamado);
                }
            }

            ChamadoAdapter chamadoAdapter;
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                chamadoAdapter = new ChamadoAdapter(lista, new ChamadoAdapter.ChamadoOnClickListener() {
                    @Override
                    public void onClickAluno(View view, int position) {
                        int i = 0;
                        for (Chamado chamado : chamados) {
                            if (chamado.getStatus() == 3) {
                                if (i == position) {
                                    Toast.makeText(getContext(), "" + chamado.getID_Chamado(), Toast.LENGTH_SHORT).show();
                                    break;
                                }
                                i++;
                            }
                        }
                    }
                });
            } else {
                chamadoAdapter = new ChamadoAdapter(lista, new ChamadoAdapter.ChamadoOnClickListener() {
                    @Override
                    public void onClickAluno(View view, int position) {
                        int i = 0;
                        for (Chamado chamado : chamados) {
                            if (chamado.getStatus() != 3) {
                                if (i == position) {
                                    Toast.makeText(getContext(), "" + chamado.getID_Chamado(), Toast.LENGTH_SHORT).show();
                                    break;
                                }
                                i++;
                            }
                        }
                    }
                });
            }

            RecyclerView rvChamados = rootView.findViewById(R.id.rvChamados);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            rvChamados.setLayoutManager(mLayoutManager);
            rvChamados.setItemAnimator(new DefaultItemAnimator());
            rvChamados.setAdapter(chamadoAdapter);

            final SwipeRefreshLayout swipeRefresh = rootView.findViewById(R.id.swipeRefresh);

            swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefresh.setRefreshing(false);
                }
            });

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}