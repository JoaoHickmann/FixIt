package com.fixit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChamadosFragment extends Fragment {
    private RecyclerView rvChamadosChamados;
    private SwipeRefreshLayout swipeRefreshChamados;

    public ChamadosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chamados, container, false);

        rvChamadosChamados = rootView.findViewById(R.id.rvChamados);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvChamadosChamados.setLayoutManager(mLayoutManager);
        rvChamadosChamados.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvChamadosChamados.getContext(), OrientationHelper.VERTICAL);
        rvChamadosChamados.addItemDecoration(dividerItemDecoration);

        swipeRefreshChamados = rootView.findViewById(R.id.srChamados);
        swipeRefreshChamados.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));

        swipeRefreshChamados.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ((PrincipalActivity) getActivity()).attRecycler();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshChamados.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });

        return rootView;
    }

    public RecyclerView getRvChamadosChamados() {
        return rvChamadosChamados;
    }
}
