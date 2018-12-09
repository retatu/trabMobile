package com.trabalho.economia.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DB_Gateway {

    private static DB_Gateway gw;
    private SQLiteDatabase db;
    private DB_Conexao db_conexao;

    private DB_Gateway(Context ctx) {
        db_conexao = new DB_Conexao(ctx);
        db = db_conexao.getWritableDatabase();
    }

    public static DB_Gateway getInstance(Context ctx) {
        if (gw == null)
            gw = new DB_Gateway(ctx);
        return gw;
    }

    public SQLiteDatabase getDatabase() {
        return this.db;
    }

}