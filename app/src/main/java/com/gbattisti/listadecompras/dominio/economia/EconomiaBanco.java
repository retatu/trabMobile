package com.gbattisti.listadecompras.dominio.economia;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.gbattisti.listadecompras.dominio.banco.Banco;
import com.gbattisti.listadecompras.dominio.banco.BancoBanco;
import com.gbattisti.listadecompras.dominio.ultis.banco.DB_Gateway;

import java.util.ArrayList;

public class EconomiaBanco {

    private DB_Gateway gw;
    private Context ctx;

    public EconomiaBanco(Context ctx){
        this.ctx = ctx;
        gw = DB_Gateway.getInstance(ctx);
    }

    public void incluir(Economia economia){
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO Economia ");
        sql.append("(nome, saldo, porcentagem_de_investimento, id_banco) ");
        sql.append("VALUES");
        sql.append("(");
        sql.append("'"+economia.getNome() + "', ");
        sql.append(economia.getSaldo() + ", ");
        sql.append(economia.getPorcentagemDeInvestimento() + ", ");
        sql.append(economia.getBanco().getID());
        sql.append(");");

        Log.d("sql", sql.toString());
        gw.getDatabase().execSQL(sql.toString());
    }

    public void alterar(Economia economia){
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE Banco SET");
        sql.append("nome='"+economia.getNome()+"'");
        sql.append("porcentagem_de_investimento="+economia.getPorcentagemDeInvestimento());
        sql.append("saldo="+economia.getSaldo());
        sql.append("WHERE ID = "+economia.getID());

        gw.getDatabase().rawQuery(sql.toString(), null);
    }

    public void excluir(Economia economia){
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM Economia");
        sql.append("WHERE ID = "+economia.getID());

        gw.getDatabase().rawQuery(sql.toString(), null);
    }

    public ArrayList<Economia> listar(EconomiaFiltro filtro) throws Exception{
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM Economia");
        sql.append("WHERE TRUE");
        if(filtro.getBanco() != null){
            sql.append("AND id_banco = "+filtro.getBanco().getID());
        }

        Cursor cursor = gw.getDatabase().rawQuery(sql.toString(), null);

        ArrayList<Economia> lista = new ArrayList<>();
        while(cursor.moveToNext()){
            Economia economia = new Economia();
            carregar(cursor, economia);
            lista.add(economia);
        }

        return lista;
    }

    public void carregar(Cursor cursor, Economia economia) throws Exception{
        Banco banco = new Banco();
        banco.setID(cursor.getInt(cursor.getColumnIndex("id_banco")));
        BancoBanco bancoBanco = new BancoBanco(ctx);
        bancoBanco.carregar(banco);

        economia.setID(cursor.getInt(cursor.getColumnIndex("id")));
        economia.setSaldo(cursor.getDouble(cursor.getColumnIndex("saldo")));
        economia.setPorcentagemDeInvestimento(cursor.getDouble(cursor.getColumnIndex("porcentagem_de_desenvolvimento")));
        economia.setNome(cursor.getString(cursor.getColumnIndex("nome")));
    }


    public void recriarTabela(){
        gw.getDatabase().execSQL("DROP TABLE Economia");
        gw.getDatabase().execSQL("CREATE TABLE Economia (ID INTEGER PRIMARY KEY AUTOINCREMENT, saldo double NOT NULL, porcentagem_de_desenvolvimento double not null," +
                "nome text not null, id_banco int not null, " +
                "FOREIGN KEY(id_banco) REFERENCES Banco(id));");
    }
}