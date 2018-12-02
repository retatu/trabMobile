package com.gbattisti.listadecompras.dominio.banco.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gbattisti.listadecompras.R;

public class BancoHolder extends RecyclerView.ViewHolder {

    public TextView nomeBanco;
    public TextView valorNoBanco;
    public ImageView buttonExcluir;

    public BancoHolder(View itemView){
        super(itemView);
        nomeBanco = itemView.findViewById(R.id.recycler_linha_banco_nome);
        valorNoBanco = itemView.findViewById(R.id.recycler_linha_banco_valor);
        buttonExcluir = itemView.findViewById(R.id.recycler_linha_banco_delete);
    }
}
