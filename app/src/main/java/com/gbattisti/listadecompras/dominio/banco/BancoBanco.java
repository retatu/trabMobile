package com.gbattisti.listadecompras.dominio.banco;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.gbattisti.listadecompras.dominio.economia.Economia;
import com.gbattisti.listadecompras.dominio.economia.EconomiaBanco;
import com.gbattisti.listadecompras.dominio.economia.EconomiaFiltro;
import com.gbattisti.listadecompras.dominio.ultis.banco.DB_Gateway;

import java.util.ArrayList;

public class BancoBanco {
    private DB_Gateway gw;
    private Context ctx;

    public BancoBanco(Context ctx){
        this.ctx = ctx;
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
        EconomiaFiltro filtroEconomia = new EconomiaFiltro();
        filtroEconomia.setBanco(banco);
        ArrayList<Economia> lista = new EconomiaBanco(ctx).listar(filtroEconomia);
        if(lista.size() > 0){
            throw new Exception("Impossível deletar, há economias relacionadas.");
        }
        gw.getDatabase().delete("Banco", "ID=?", new String[]{banco.getID() + ""});
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

    public double getPorcentagemAtualNoBanco(Banco banco) throws Exception{
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT sum(porcentagem_de_investimento) as porcentagem_total FROM Economia where id_banco = "+banco.getID());

        Cursor cursorBanco = gw.getDatabase().rawQuery(sql.toString(), null);
        if(cursorBanco.moveToNext()){
            int valor = cursorBanco.getInt(cursorBanco.getColumnIndex("porcentagem_total"));
            return valor;
        }

        return 0.0;
    }

    public void recriarTabela(){
        gw.getDatabase().execSQL("DROP TABLE Banco");
        gw.getDatabase().execSQL("CREATE TABLE Banco (ID INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT NOT NULL, saldo double)");
    }

}
