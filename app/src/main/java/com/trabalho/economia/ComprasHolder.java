package com.trabalho.economia;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ComprasHolder extends RecyclerView.ViewHolder {


    public TextView item;
    public TextView quantidade;
    public ImageView btnExcluir;

    public ComprasHolder(View itemView) {
        super(itemView);
        item = itemView.findViewById(R.id.tv_linha_itemID);
        quantidade = itemView.findViewById(R.id.tv_linha_quantidadeID);
        btnExcluir = itemView.findViewById(R.id.iv_linha_deleteID);
    }
}