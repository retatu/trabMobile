package com.gbattisti.listadecompras.dominio.deposito;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.gbattisti.listadecompras.dominio.banco.Banco;
import com.gbattisti.listadecompras.dominio.banco.BancoBanco;
import com.gbattisti.listadecompras.dominio.ultis.banco.DB_Gateway;

import java.util.ArrayList;

public class DepositoBanco {
    private DB_Gateway gw;
    private Context ctx;

    public DepositoBanco(Context ctx){
        this.ctx = ctx;
        gw = DB_Gateway.getInstance(ctx);
    }

    public void incluir(Deposito deposito) throws Exception{
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO Deposito ");
        sql.append("(valor, id_banco)");
        sql.append("VALUES");
        sql.append("("+deposito.getValorDeposito()+",");
        sql.append(""+deposito.getBanco().getID()+")");

        Log.d("sql", sql.toString());
        gw.getDatabase().execSQL(sql.toString());

    }

    public void alterar(Deposito deposito) throws Exception{
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE Deposito SET ");
        sql.append("id_banco="+deposito.getBanco().getID()+", ");
        sql.append("valor="+deposito.getValorDeposito()+" ");
        sql.append("WHERE ID = "+deposito.getId());

        Log.d("sql", sql.toString());
        gw.getDatabase().execSQL(sql.toString());
    }

    public void excluir(Deposito deposito) throws Exception{
        gw.getDatabase().delete("Deposito", "ID=?", new String[]{deposito.getId() + ""});
    }

    public ArrayList<Deposito> listar(DepositoFiltro filtro) throws Exception{

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM Deposito ");
        sql.append("WHERE 1");
        if(filtro.getBanco() != null){
            sql.append("AND id_banco =  "+filtro.getBanco().getID());
        }
        sql.append(";");

        Cursor cursor = gw.getDatabase().rawQuery(sql.toString(), null);

        ArrayList<Deposito> lista = new ArrayList<>();
        while(cursor.moveToNext()){
            Deposito banco = new Deposito();
            carregar(cursor, banco);
            lista.add(banco);
        }
        return lista;
    }


    public void carregar(Cursor cursor, Deposito deposito) throws Exception{
        deposito.setId(cursor.getInt(cursor.getColumnIndex("ID")));
        deposito.setValorDeposito(cursor.getDouble(cursor.getColumnIndex("valor")));
        Banco banco = new Banco();
        banco.setID(cursor.getInt(cursor.getColumnIndex("id_banco")));
        BancoBanco bancoBanco = new BancoBanco(ctx);
        bancoBanco.carregar(banco);
        deposito.setBanco(banco);
    }
}
