package com.fixit;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import Classes.Chamado;

public class ChamadoAdapter extends RecyclerView.Adapter<ChamadoAdapter.MyViewHolder> {
    private List<Chamado> listaChamados;
    private ChamadoOnClickListener chamadoOnClickListener;
    private ChamadoOnLongClickListener chamadoOnLongClickListener;
    private LinkedList<CardView> cardViews;

    public ChamadoAdapter(List<Chamado> listaChamados, ChamadoOnClickListener chamadoOnClickListener, ChamadoOnLongClickListener chamadoOnLongClickListener) {
        this.listaChamados = listaChamados;
        this.chamadoOnClickListener = chamadoOnClickListener;
        this.chamadoOnLongClickListener = chamadoOnLongClickListener;
        cardViews = new LinkedList<>();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public ChamadoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chamados_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ChamadoAdapter.MyViewHolder holder, final int position) {
        Chamado chamado = listaChamados.get(position);

        holder.tvChamado.setText("Chamado #" + chamado.getID_Chamado());
        holder.tvProblema.setText((chamado.getProblema().getTipo() == 1 ? "Hardware" : "Software") + " - " + chamado.getProblema().getDescricao());
        holder.tvSala.setText(chamado.getComputador().getSala() + " - " + chamado.getComputador().getNumero());
        if (chamado.getStatus() == 3) {
            holder.ivStatus.setColorFilter(Color.parseColor("#00ff00"));
        } else if (chamado.getStatus() == 2) {
            holder.ivStatus.setColorFilter(Color.parseColor("#ffff00"));
        } else {
            holder.ivStatus.setColorFilter(Color.parseColor("#ff0000"));
        }

        if (chamadoOnClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chamadoOnClickListener.onClickAluno(holder.itemView, position);
                }
            });
        }

        if (chamadoOnLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    chamadoOnLongClickListener.onLongClickAluno(holder.itemView, position);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listaChamados.size();
    }

    public LinkedList<CardView> getCardViews() {
        return cardViews;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvChamado, tvProblema, tvSala;
        ImageView ivStatus;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvChamado = itemView.findViewById(R.id.tvChamado);
            ivStatus = itemView.findViewById(R.id.ivStatus);
            tvProblema = itemView.findViewById(R.id.tvProblema);
            tvSala = itemView.findViewById(R.id.tvSala);
            getCardViews().add((CardView) itemView);
        }
    }

    public interface ChamadoOnClickListener {
        public void onClickAluno(View view, int position);
    }

    public interface ChamadoOnLongClickListener {
        public void onLongClickAluno(View view, int position);
    }
}