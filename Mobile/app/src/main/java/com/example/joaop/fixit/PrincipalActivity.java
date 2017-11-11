package com.example.joaop.fixit;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import Classes.Chamado;


public class PrincipalActivity extends AppCompatActivity {
    private Dados dados;
    private LinkedList<Chamado> todos_chamados, abertos, finalizados;
    private boolean onActionMode = false;
    private LinkedList<Chamado> selecionados;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private ActionMode actionMode;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        appBarLayout = findViewById(R.id.appbar);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Meus chamados");
        setSupportActionBar(toolbar);

        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(PrincipalActivity.this, AdicionarChamadoActivity.class);
                startActivityForResult(it, 1);
            }
        });

        selecionados = new LinkedList<>();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 1) {
            final Snackbar snackbar = Snackbar.make(findViewById(R.id.main_content), "Chamado adicionado", Snackbar.LENGTH_LONG);
            snackbar.setAction("Ok", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });

            snackbar.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!onActionMode) {
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
    }

    public void attDados() throws IOException, ClassNotFoundException {
        dados.getOut().writeObject("MeusChamados");
        todos_chamados = (LinkedList<Chamado>) dados.getIn().readObject();

        finalizados = new LinkedList<>();
        abertos = new LinkedList<>();

        for (Chamado chamado : todos_chamados) {
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
                RecyclerView rvChamados = ((ChamadosFragment) ((ViewPagerAdapter) mViewPager.getAdapter()).getItem(0)).getRvChamados();
                ChamadoAdapter chamadoAdapter = new ChamadoAdapter(abertos, new ChamadoAdapter.ChamadoOnClickListener() {
                    @Override
                    public void onClickAluno(View view, int position) {
                        if (onActionMode) {
                            Chamado chamado = abertos.get(position);
                            if (selecionados.contains(chamado)) {
                                ((CardView)view).setCardBackgroundColor(Color.WHITE);
                                selecionados.remove(chamado);
                                actionMode.setTitle(selecionados.size() + " selecionados.");
                                actionMode.getMenu().getItem(1).setVisible(selecionados.size() == 1 && selecionados.get(0).getStatus() != 3);

                                if (selecionados.size() == 0) {
                                    actionMode.finish();
                                }
                            } else {
                                ((CardView)view).setCardBackgroundColor(Color.LTGRAY);
                                selecionados.add(chamado);
                                actionMode.setTitle(selecionados.size() + " selecionados.");
                                actionMode.getMenu().getItem(1).setVisible(selecionados.size() == 1 && selecionados.get(0).getStatus() != 3);
                            }
                        }
                    }
                }, new ChamadoAdapter.ChamadoOnLongClickListener() {
                    @Override
                    public void onLongClickAluno(View view, int position) {
                        if (!onActionMode) {
                            MyActionMode callback = new MyActionMode();
                            actionMode = startActionMode(callback);
                            ((CardView)view).setCardBackgroundColor(Color.LTGRAY);
                            selecionados.add(abertos.get(position));
                            actionMode.setTitle(selecionados.size() + " selecionados.");
                        } else {
                            view.callOnClick();
                        }
                    }
                });
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(PrincipalActivity.this);
                rvChamados.setLayoutManager(mLayoutManager);
                rvChamados.setItemAnimator(new DefaultItemAnimator());
                rvChamados.setAdapter(chamadoAdapter);

                rvChamados = ((ChamadosFragment) ((ViewPagerAdapter) mViewPager.getAdapter()).getItem(1)).getRvChamados();
                chamadoAdapter = new ChamadoAdapter(finalizados, new ChamadoAdapter.ChamadoOnClickListener() {
                    @Override
                    public void onClickAluno(View view, int position) {
                        if (onActionMode) {
                            Chamado chamado = finalizados.get(position);
                            if (selecionados.contains(chamado)) {
                                ((CardView)view).setCardBackgroundColor(Color.WHITE);
                                selecionados.remove(chamado);
                                actionMode.setTitle(selecionados.size() + " selecionados.");
                                actionMode.getMenu().getItem(1).setVisible(selecionados.size() == 1 && selecionados.get(0).getStatus() != 3);

                                if (selecionados.size() == 0) {
                                    actionMode.finish();
                                }
                            } else {
                                ((CardView)view).setCardBackgroundColor(Color.LTGRAY);
                                selecionados.add(chamado);
                                actionMode.setTitle(selecionados.size() + " selecionados.");
                                actionMode.getMenu().getItem(1).setVisible(selecionados.size() == 1 && selecionados.get(0).getStatus() != 3);
                            }
                        }
                    }
                }, null);
                mLayoutManager = new LinearLayoutManager(PrincipalActivity.this);
                rvChamados.setLayoutManager(mLayoutManager);
                rvChamados.setItemAnimator(new DefaultItemAnimator());
                rvChamados.setAdapter(chamadoAdapter);
            }
        });
    }

    class MyActionMode implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_contextual_principal, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            toolbar.setVisibility(View.GONE);
            onActionMode = true;
            selecionados.clear();
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_editar:
                    Toast.makeText(PrincipalActivity.this, "Editar", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_excluir:
                    Toast.makeText(PrincipalActivity.this, "Excluir", Toast.LENGTH_SHORT).show();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            toolbar.setVisibility(View.VISIBLE);
            onActionMode = false;

            RecyclerView rvChamados = ((ChamadosFragment) ((ViewPagerAdapter) mViewPager.getAdapter()).getItem(0)).getRvChamados();
            for (int i=0; i<rvChamados.getAdapter().getItemCount(); i++) {
                CardView cardView = (CardView)rvChamados.getChildAt(i);
                cardView.setCardBackgroundColor(Color.WHITE);
            }

           rvChamados = ((ChamadosFragment) ((ViewPagerAdapter) mViewPager.getAdapter()).getItem(1)).getRvChamados();
            for (int i=0; i<rvChamados.getAdapter().getItemCount(); i++) {
                CardView cardView = (CardView)rvChamados.getChildAt(i);
                cardView.setCardBackgroundColor(Color.WHITE);
            }
        }
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