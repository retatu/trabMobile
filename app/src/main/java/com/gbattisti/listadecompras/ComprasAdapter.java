package com.gbattisti.listadecompras;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gbattisti.listadecompras.DAO.Compra;
import com.gbattisti.listadecompras.DAO.CompraDAO;

import java.util.List;

public class ComprasAdapter extends RecyclerView.Adapter<ComprasHolder> {

    private final List<Compra> compras;

    public ComprasAdapter(List<Compra> compras) {
        this.compras = compras;
    }

    @Override
    public ComprasHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ComprasHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linha_compras, parent, false));
    }

    @Override
    public void onBindViewHolder(ComprasHolder holder, final int position) {
        holder.item.setText(compras.get(position).getItem());
        holder.quantidade.setText(compras.get(position).getQuantidade());
        final long deleteDbID = compras.get(position).getID();
        final int deleteViewID = holder.getAdapterPosition();

        holder.btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = v;
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Confirmação")
                        .setMessage("Tem certeza que deseja excluir este item?")
                        .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CompraDAO dao = new CompraDAO(view.getContext());
                                int numItens = dao.excluirItem(deleteDbID);
                                if (numItens > 0) {
                                    compras.remove(deleteViewID);
                                    notifyItemRemoved(deleteViewID);
                                    notifyItemRangeChanged(deleteViewID, compras.size());
                                    Snackbar.make(view, "Excluiu!", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                } else {
                                    Snackbar.make(view, "Erro ao excluir o cliente!", Snackbar.LENGTH_LONG)
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
        return compras != null ? compras.size() : 0;
    }

    public void adicionarCompra(Compra compra) {
        compras.add(compra);
        notifyItemInserted(getItemCount());
    }

    public void removerCompra(int deleteViewID) {
        compras.remove(deleteViewID);
        notifyItemRemoved(deleteViewID);
        notifyItemRangeChanged(deleteViewID, compras.size());
    }
    public void cancelarRemocao(int deleteViewID){
        notifyItemChanged(deleteViewID);
    }
    public long getDbID(int deleteViewID){
        return compras.get(deleteViewID).getID();
    }


}