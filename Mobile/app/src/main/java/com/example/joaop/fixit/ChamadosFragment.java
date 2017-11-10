package com.example.joaop.fixit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChamadosFragment extends Fragment {
    private RecyclerView rvChamados;

    public ChamadosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chamados, container, false);

        rvChamados = rootView.findViewById(R.id.rvChamados);

        return rootView;
    }

    public RecyclerView getRvChamados() {
        return rvChamados;
    }

    public void setRvChamados(RecyclerView rvChamados) {
        this.rvChamados = rvChamados;
    }
}
