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
        sql.append("(nome, saldo)");
        sql.append("VALUES");
        sql.append("('"+banco.getNome()+"',");
        sql.append(""+banco.getSaldo()+")");

        Log.d("sql", sql.toString());
        gw.getDatabase().execSQL(sql.toString());

    }

    public void alterar(Banco banco) throws Exception{
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE Banco SET ");
        sql.append("nome='"+banco.getNome()+"', ");
        sql.append("saldo="+banco.getSaldo()+" ");
        sql.append("WHERE ID = "+banco.getID());

        Log.d("sql", sql.toString());
        gw.getDatabase().execSQL(sql.toString());
    }

    public void excluir(Banco banco) throws Exception{
        int deletados = gw.getDatabase().delete("Banco", "ID=?", new String[]{banco.getID() + ""});
        if(deletados == 0){
            throw new Exception("Nenhum registro deletado.");
        }
//        StringBuilder sql = new StringBuilder();
//        sql.append("DELETE FROM Banco ");
//        sql.append("WHERE ID="+banco.getID());
//
//        gw.getDatabase().execSQL(sql.toString());
//        Log.d("SQL EXCLUIR", sql.toString());
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


    public void carregar(Cursor cursor, Banco banco){
        banco.setID(cursor.getInt(cursor.getColumnIndex("ID")));
        banco.setNome(cursor.getString(cursor.getColumnIndex("nome")));
        banco.setSaldo(cursor.getDouble(cursor.getColumnIndex("saldo")));
    }

    public void carregar(Banco banco) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM Banco ");
        sql.append("WHERE ID = "+banco.getID());
        Cursor cursorBanco = gw.getDatabase().rawQuery(sql.toString(), null);
        if(cursorBanco.moveToNext()) {
            carregar(cursorBanco, banco);
        }
    }

    public void recriarTabela(){
        gw.getDatabase().execSQL("DROP TABLE Banco");
        gw.getDatabase().execSQL("CREATE TABLE Banco (ID INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT NOT NULL, saldo double)");
    }

}
