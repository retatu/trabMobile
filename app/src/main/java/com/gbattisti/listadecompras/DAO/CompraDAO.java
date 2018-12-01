package com.gbattisti.listadecompras.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class CompraDAO {
    private final String TABLE_COMPRAS = "Compras";
    private DB_Gateway gw;

    public CompraDAO(Context ctx){
        gw = DB_Gateway.getInstance(ctx);
    }


    // retorna o ID da inserção
    public long salvarItem(Compra compra){
        ContentValues cv = new ContentValues();
        cv.put("Item", compra.getItem());
        cv.put("Quantidade", compra.getQuantidade());
        return gw.getDatabase().insert(TABLE_COMPRAS, null, cv);
    }

    // retorna a quantidade de linhas afetadas
    public int excluirItem(long id){
        return gw.getDatabase().delete(TABLE_COMPRAS, "ID=?", new String[]{ id + "" });
    }

    public List<Compra> retornarTodos(){
        List<Compra> compras = new ArrayList<>();
        Cursor cursor = gw.getDatabase().rawQuery("SELECT * FROM Compras", null);
        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex("ID"));
            String item = cursor.getString(cursor.getColumnIndex("Item"));
            String quantidade = cursor.getString(cursor.getColumnIndex("Quantidade"));
            compras.add(new Compra(id, item, quantidade));
        }
        cursor.close();
        return compras;
    }

    public void recriarTabela(){
        gw.getDatabase().execSQL("DROP TABLE Compras");
        gw.getDatabase().execSQL("CREATE TABLE Compras (ID INTEGER PRIMARY KEY AUTOINCREMENT, Item TEXT NOT NULL, Quantidade TEXT)");
    }

}