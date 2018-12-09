package com.trabalho.economia.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB_Conexao extends SQLiteOpenHelper {

    // Nome do banco de dados
    private static final String DATABASE_NAME = "ListadeCompras.db";
    // Versão do banco - incrementar a cada atualização do banco
    private static final int DATABASE_VERSION = 1;
    private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS Compras (ID INTEGER PRIMARY KEY AUTOINCREMENT, Item TEXT NOT NULL, Quantidade TEXT);";

    private Context context;

    public DB_Conexao(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
