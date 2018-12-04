package com.gbattisti.listadecompras.dominio.ultis.banco;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB_Conexao extends SQLiteOpenHelper {

    // Nome do banco de dados
    private static final String DATABASE_NAME = "Investimento.db";
    // Versão do banco - incrementar a cada atualização do banco
    private static final int DATABASE_VERSION = 12;
    private final String CREATE_TABLE_BANCO = "CREATE TABLE IF NOT EXISTS Banco (ID INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT NOT NULL, saldo double);";
    private final String CREATE_TABLE_DEPOSITO = "CREATE TABLE IF NOT EXISTS Deposito (ID INTEGER PRIMARY KEY AUTOINCREMENT, valor double NOT NULL, id_banco int NOT NULL," +
                                                 "FOREIGN KEY(id_banco) REFERENCES Banco(ID));";
    private final String CREATE_TABLE_ECONOMIA = "CREATE TABLE IF NOT EXISTS Economia (ID INTEGER PRIMARY KEY AUTOINCREMENT, porcentagem_de_investimento double NOT NULL," +
                            "nome text not null, id_banco int not null, meta double not null," +
                            "FOREIGN KEY(id_banco) REFERENCES Banco(ID));";


    private Context context;

    public DB_Conexao(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("PRAGMA foreing_keys = ON");
        db.execSQL(CREATE_TABLE_BANCO);
        db.execSQL(CREATE_TABLE_ECONOMIA);
        db.execSQL(CREATE_TABLE_DEPOSITO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE Economia");
        //db.execSQL("DROP TABLE Deposito");
        db.execSQL("DROP TABLE Banco");
        db.execSQL(CREATE_TABLE_ECONOMIA);
        db.execSQL(CREATE_TABLE_BANCO);
        db.execSQL(CREATE_TABLE_DEPOSITO);
    }

}
