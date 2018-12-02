package com.gbattisti.listadecompras.dominio.banco.recycler;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gbattisti.listadecompras.R;
import com.gbattisti.listadecompras.dominio.banco.Banco;
import com.gbattisti.listadecompras.dominio.banco.BancoBanco;

import java.util.List;

public class BancoAdapter extends RecyclerView.Adapter<BancoHolder> {

    private final List<Banco> listaBanco;

    public BancoAdapter(List<Banco> listaBanco) {
        this.listaBanco = listaBanco;
    }

    @Override
    public BancoHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new BancoHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_linha_banco, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(BancoHolder holder, int i) {
        final Banco banco = listaBanco.get(i);
        holder.nomeBanco.setText(banco.getNome());
        holder.valorNoBanco.setText("Valor: "+banco.getSaldo() == null ? "0.0" : String.valueOf(banco.getSaldo()));
        final int deleteViewID = holder.getAdapterPosition();
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
                                    BancoBanco bancoBanco = new BancoBanco(view.getContext());
                                    listaBanco.remove(deleteViewID);
                                    bancoBanco.excluir(banco);
                                    notifyItemRemoved(deleteViewID);
                                    notifyItemRangeChanged(deleteViewID, listaBanco.size());
                                    Snackbar.make(view, "Exclido com sucesso.", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                } catch (Exception ex) {
                                    Snackbar.make(view, "Erro ao excluir: " + ex.getMessage(), Snackbar.LENGTH_LONG)
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
        return listaBanco != null ? listaBanco.size() : 0;
    }

    public void adicionarBanco(Banco banco) {
        listaBanco.add(banco);
        notifyItemInserted(getItemCount());
        notifyDataSetChanged();
    }

    public void removerBanco(int deleteViewID) {
        listaBanco.remove(deleteViewID);
        notifyItemRemoved(deleteViewID);
        notifyItemRangeChanged(deleteViewID, listaBanco.size());
    }
    public void cancelarRemocao(int deleteViewID){
        notifyItemChanged(deleteViewID);
    }
    public Banco getBanco(int deleteViewID){
        return listaBanco.get(deleteViewID);
    }


}
