package com.gbattisti.listadecompras.dominio.banco;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.gbattisti.listadecompras.dominio.ultis.banco.DB_Gateway;

import java.util.ArrayList;

public class BancoBanco {
    private DB_Gateway gw;

    public BancoBanco(Context ctx){
        gw = DB_Gateway.getInstance(ctx);
    }

    public void incluir(Banco banco) throws Exception{
        if(banco.getNome().trim().isEmpty()){
            throw new Exception("Nome não informado.");
        }
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO Banco ");
        sql.append("(nome)");
        sql.append("VALUES");
        sql.append("('"+banco.getNome()+"')");

        gw.getDatabase().execSQL(sql.toString());

        //Log.d("myTag", String.valueOf(cursor.getCount()));
    }

    public void alterar(Banco banco) throws Exception{
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE Banco SET ");
        sql.append("nome="+banco.getNome()+", ");
        sql.append("WHERE ID = "+banco.getID());

        gw.getDatabase().execSQL(sql.toString());
    }

    public void excluir(Banco banco) throws Exception{
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM Banco ");
        sql.append("WHERE ID = "+banco.getID());

        gw.getDatabase().execSQL(sql.toString());
    }

    public ArrayList<Banco> listar(BancoFiltro filtro) throws Exception{

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM Banco ");
        sql.append("WHERE 1");
        if(filtro.getNome() != null && !filtro.getNome().isEmpty()){
            sql.append("AND LIKE '"+filtro.getNome()+"'");
        }
        sql.append(";");

        Cursor cursor = gw.getDatabase().rawQuery(sql.toString(), null);

        ArrayList<Banco> lista = new ArrayList<>();

        while(cursor.moveToNext()){
            Banco banco = new Banco();
            carregar(cursor, banco);
            lista.add(banco);
        }

        return lista;
    }


    public void carregar(Cursor cursor, Banco banco) throws Exception{
        banco.setID(cursor.getInt(cursor.getColumnIndex("ID")));
        banco.setNome(cursor.getString(cursor.getColumnIndex("nome")));

    }

    public void carregar(Banco banco) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM Banco ");
        sql.append("WHERE ID = "+banco.getID());

        Cursor cursor = gw.getDatabase().rawQuery(sql.toString(), null);
        carregar(cursor, banco);
    }

    public void recriarTabela(){
        gw.getDatabase().execSQL("DROP TABLE Banco");
        gw.getDatabase().execSQL("CREATE TABLE Banco (ID INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT NOT NULL)");
    }

}
