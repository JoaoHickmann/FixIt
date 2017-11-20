package com.example.joaop.fixit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChamadosFragment extends Fragment {
    private RecyclerView rvChamadosChamados;
    private SwipeRefreshLayout swipeRefreshChamados;

    public ChamadosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chamados, container, false);

        rvChamadosChamados = rootView.findViewById(R.id.rvChamados);

        swipeRefreshChamados = rootView.findViewById(R.id.srChamados);

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
