package com.gbattisti.listadecompras;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DadosPessoais extends AppCompatActivity {

    private Button btn_salvar;
    private Button btn_limpar;
    private EditText et_nome;
    private EditText et_email;
    private ConstraintLayout ml_dados_pessoais;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dados_pessoais);

        btn_salvar = findViewById(R.id.btn_salvarID);
        btn_limpar = findViewById(R.id.btn_limparID);
        et_nome = findViewById(R.id.et_nomeID);
        et_email = findViewById(R.id.et_emailID);
        ml_dados_pessoais = findViewById(R.id.ml_dados_pessoaisID);

        // Inicialização para gravar as preferencias
        pref = getSharedPreferences("ListaComprasPrefArq",  MODE_PRIVATE);
        editor = pref.edit();

        btn_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("Nome", et_nome.getText().toString()); //Grava o nome do usuário
                editor.putString("Email", et_email.getText().toString()); // Grava o email
                editor.commit(); // commit changes
                Snackbar.make(ml_dados_pessoais, "Informações Salvas.", Snackbar.LENGTH_LONG).show();

                // Cria uma Intent para retornar os valores
                Intent i = new Intent();
                i.putExtra("Nome", et_nome.getText().toString());
                i.putExtra("Email", et_email.getText().toString());
                setResult(RESULT_OK, i);
                finish();
            }
        });


        btn_limpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clear();
                editor.commit(); // commit changes
                Snackbar.make(ml_dados_pessoais, "Informações Apagadas.", Snackbar.LENGTH_LONG).show();
                // Cria uma Intent para retornar os valores
                Intent i = new Intent();
                i.putExtra("Nome", "sem nome");
                i.putExtra("Email", "sem email");
                setResult(RESULT_OK, i);
                finish();
            }
        });
    }
}
