package com.gbattisti.listadecompras.dominio.economia.recycler;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gbattisti.listadecompras.R;
import com.gbattisti.listadecompras.dominio.economia.Economia;
import com.gbattisti.listadecompras.dominio.economia.EconomiaBanco;

import java.util.List;

public class EconomiaAdapter extends RecyclerView.Adapter<EconomiaHolder> {

    private final List<Economia> listaEconomia;

    public EconomiaAdapter(List<Economia> listaEconomia) {
        this.listaEconomia = listaEconomia;
    }

    @Override
    public EconomiaHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new EconomiaHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_linha_economia, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(EconomiaHolder holder, int i) {
        if(listaEconomia == null || listaEconomia.size() < 1){
            return;
        }
        final Economia economia = listaEconomia.get(i);
        holder.nomeEconomia.setText(economia.getNome());
        holder.porcentagemEconomia.setText(economia.getPorcentagemDeInvestimento().toString()+"%");
        holder.saldoEconomia.setText(String.valueOf(economia.getSaldoEconomia()));
        final int viewID = holder.getAdapterPosition();
        holder.buttonExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = v;
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Confirmação")
                        .setMessage("Tem certeza que deseja excluir este item?")
                        .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    EconomiaBanco economiaBanco = new EconomiaBanco(view.getContext());
                                    economiaBanco.excluir(economia);
                                    listaEconomia.remove(viewID);
                                    notifyItemRemoved(viewID);
                                    notifyItemRangeChanged(viewID, listaEconomia.size());
                                    Snackbar.make(view, "Excluido com sucesso.", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                } catch (Exception ex) {
                                    Snackbar.make(view, "Erro ao excluir: " + ex.getCause(), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .create()
                        .show();
            }
        });
        holder.buttonAlterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = v;
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Confirmação")
                        .setMessage("Tem certeza que deseja excluir este item?")
                        .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    EconomiaBanco economiaBanco = new EconomiaBanco(view.getContext());
                                    economia.setNome("nomeeee");
                                    economiaBanco.alterar(economia);
                                    notifyItemRangeChanged(viewID, listaEconomia.size());
                                    Snackbar.make(view, "Alterado com sucesso.", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                } catch (Exception ex) {
                                    Snackbar.make(view, "Erro ao Alterar: " + ex.getCause(), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .create()
                        .show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaEconomia != null ? listaEconomia.size() : 0;
    }
}
