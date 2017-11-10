package com.example.joaop.fixit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChamadosFragment extends Fragment {
    private RecyclerView rvChamados;
    private SwipeRefreshLayout swipeRefresh;

    public ChamadosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chamados, container, false);

        rvChamados = rootView.findViewById(R.id.rvChamados);

        swipeRefresh = rootView.findViewById(R.id.swipeRefresh);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ((PrincipalActivity)getActivity()).attRecycler();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    swipeRefresh.setRefreshing(false);
                                }
                            });
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        return rootView;
    }

    public RecyclerView getRvChamados() {
        return rvChamados;
    }

    public void setRvChamados(RecyclerView rvChamados) {
        this.rvChamados = rvChamados;
    }
}
