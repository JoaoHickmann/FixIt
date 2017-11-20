package com.example.joaop.fixit;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import Classes.Chamado;


public class PrincipalActivity extends AppCompatActivity {
    private final int REQUEST_NOVO_CHAMADO = 1;
    private final int REQUEST_ATUALIZAR_CHAMADO = 2;

    private Dados dados;

    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private ActionMode actionMode;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private FloatingActionButton fab;

    private LinkedList<Chamado> todos_chamados, abertos, finalizados, selecionados;
    private boolean onActionMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        appBarLayout = findViewById(R.id.appbar);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fab = findViewById(R.id.fab);

        dados = (Dados) getApplicationContext();

        selecionados = new LinkedList<>();

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0 && fab.getVisibility() == View.GONE && !onActionMode) {
                    fab.show();
                } else if (verticalOffset != 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                }
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (onActionMode) {
                    actionMode.finish();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(PrincipalActivity.this, AdicionarChamadoActivity.class);
                startActivityForResult(it, REQUEST_NOVO_CHAMADO);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_configuracoes_principal:
                startActivity(new Intent(PrincipalActivity.this, ConfiguracoesActivity.class));
                return true;
            case R.id.action_sair_principal:
                dados.setUser(null);

                SharedPreferences sharedPref = getSharedPreferences("com.example.joaop.fixit", dados.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("Username", null);
                editor.putString("Senha", null);
                editor.commit();

                startActivity(new Intent(PrincipalActivity.this, LoginActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!onActionMode) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    attRecycler();
                }
            }).start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_NOVO_CHAMADO && resultCode == 1) {
            final Snackbar snackbar = Snackbar.make(findViewById(R.id.main_content), "Chamado adicionado.", Snackbar.LENGTH_LONG);
            snackbar.setAction("Ok", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }
        if (requestCode == REQUEST_ATUALIZAR_CHAMADO && resultCode == 1) {
            final Snackbar snackbar = Snackbar.make(findViewById(R.id.main_content), "Chamado excluido.", Snackbar.LENGTH_LONG);
            snackbar.setAction("Ok", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }
    }


    public void attRecycler() {
        todos_chamados = (LinkedList<Chamado>) dados.obterLista("MeusChamados");

        finalizados = new LinkedList<>();
        abertos = new LinkedList<>();

        for (Chamado chamado : todos_chamados) {
            if (chamado.getStatus() == 3) {
                finalizados.add(chamado);
            } else {
                abertos.add(chamado);
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (onActionMode) {
                    actionMode.finish();
                }

                RecyclerView rvChamados = ((ChamadosFragment) ((ViewPagerAdapter) mViewPager.getAdapter()).getItem(0)).getRvChamadosChamados();
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(PrincipalActivity.this);
                rvChamados.setLayoutManager(mLayoutManager);
                rvChamados.setItemAnimator(new DefaultItemAnimator());
                rvChamados.setAdapter(new ChamadoAdapter(abertos, new ChamadoAdapter.ChamadoOnClickListener() {
                    @Override
                    public void onClickAluno(View view, int position) {
                        if (onActionMode) {
                            if (selecionados.contains(abertos.get(position))) {
                                selecionados.remove(abertos.get(position));
                                ((CardView) view).setCardBackgroundColor(Color.WHITE);

                                if (selecionados.size() == 0) {
                                    actionMode.finish();
                                }
                            } else {
                                selecionados.add(abertos.get(position));
                                ((CardView) view).setCardBackgroundColor(Color.LTGRAY);
                            }

                            actionMode.setTitle(selecionados.size() + " selecionado" + (selecionados.size() > 1 ? "s" : "") + ".");
                            actionMode.getMenu().getItem(0).setVisible(selecionados.size() == 1);
                        } else {
                            Intent it = new Intent(PrincipalActivity.this, DetalhesActivity.class);
                            it.putExtra("Editavel", true);
                            it.putExtra("Chamado", abertos.get(position));
                            startActivityForResult(it, REQUEST_ATUALIZAR_CHAMADO);
                        }
                    }
                }, new ChamadoAdapter.ChamadoOnLongClickListener() {
                    @Override
                    public void onLongClickAluno(View view, int position) {
                        if (onActionMode) {
                            view.callOnClick();
                        } else {
                            MyActionMode callback = new MyActionMode();
                            actionMode = startActionMode(callback);
                            ((CardView) view).setCardBackgroundColor(Color.LTGRAY);
                            selecionados.add(abertos.get(position));
                            actionMode.setTitle("1 selecionado.");
                        }
                    }
                }));

                rvChamados = ((ChamadosFragment) ((ViewPagerAdapter) mViewPager.getAdapter()).getItem(1)).getRvChamadosChamados();
                mLayoutManager = new LinearLayoutManager(PrincipalActivity.this);
                rvChamados.setLayoutManager(mLayoutManager);
                rvChamados.setItemAnimator(new DefaultItemAnimator());
                rvChamados.setAdapter(new ChamadoAdapter(finalizados, new ChamadoAdapter.ChamadoOnClickListener() {
                    @Override
                    public void onClickAluno(View view, int position) {
                        Intent it = new Intent(PrincipalActivity.this, DetalhesActivity.class);
                        it.putExtra("Chamado", finalizados.get(position));
                        startActivity(it);
                    }
                }, null));
            }
        });
    }

    private void excluirChamados(LinkedList<Chamado> chamados) {
        final boolean plural = chamados.size() > 1;

        if ((int) dados.realizarOperacao("ExcluirChamado", chamados) != 0) {
            attRecycler();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snackbar = Snackbar.make(findViewById(R.id.main_content), "Chamado" + (plural ? "s" : "") + " excluido" + (plural ? "s" : "") + ".", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                }
            });
        } else {
            final Snackbar snackbar = Snackbar.make(findViewById(R.id.main_content), "Não foi possível excluir o" + (plural ? "s" : "") + " chamado" + (plural ? "s" : "") + ".", Snackbar.LENGTH_LONG);
            snackbar.setAction("Ok", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }
    }

    class MyActionMode implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_contextual_principal, menu);

            selecionados.clear();
            onActionMode = true;
            fab.hide();

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_editar_principal:
                    Intent it = new Intent(PrincipalActivity.this, DetalhesActivity.class);
                    it.putExtra("Editavel", true);
                    it.putExtra("Chamado", selecionados.get(0));
                    startActivityForResult(it, REQUEST_ATUALIZAR_CHAMADO);
                    return true;
                case R.id.action_excluir_principal:
                    AlertDialog.Builder builder = new AlertDialog.Builder(PrincipalActivity.this);
                    builder.setTitle("Excluir chamado" + (selecionados.size() == 1 ? "" : "s"))
                            .setMessage("Deseja realmente excluir este" + (selecionados.size() == 1 ? "" : "s") + " chamado" + (selecionados.size() == 1 ? "" : "s") + "?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            excluirChamados(selecionados);
                                        }
                                    }).start();
                                }
                            })
                            .setNegativeButton("Não", null);

                    AlertDialog alerta = builder.create();
                    alerta.show();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            RecyclerView rvChamados = ((ChamadosFragment) ((ViewPagerAdapter) mViewPager.getAdapter()).getItem(0)).getRvChamadosChamados();
            for (CardView cardView : ((ChamadoAdapter) rvChamados.getAdapter()).getCardViews()) {
                cardView.setCardBackgroundColor(Color.WHITE);
            }

            tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            fab.show();

            onActionMode = false;
        }
    }

    public void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChamadosFragment(), "Abertos");
        adapter.addFragment(new ChamadosFragment(), "Finalizados");
        viewPager.setAdapter(adapter);
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