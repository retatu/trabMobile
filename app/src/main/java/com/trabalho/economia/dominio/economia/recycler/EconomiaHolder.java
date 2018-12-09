package com.trabalho.economia.dominio.economia.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.trabalho.economia.R;

public class EconomiaHolder extends RecyclerView.ViewHolder {

    public TextView nomeEconomia;
    public TextView porcentagemEconomia;
    public TextView saldoEconomia;
    public ImageView buttonExcluir;

    public EconomiaHolder(View itemView){
        super(itemView);
        nomeEconomia = itemView.findViewById(R.id.recycler_linha_economia_nome);
        porcentagemEconomia = itemView.findViewById(R.id.recycler_linha_economia_porcentagem);
        saldoEconomia = itemView.findViewById(R.id.recycler_linha_economia_saldo);
        buttonExcluir = itemView.findViewById(R.id.recycler_linha_economia_delete);
    }
}
