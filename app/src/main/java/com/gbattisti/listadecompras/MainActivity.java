package com.gbattisti.listadecompras;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gbattisti.listadecompras.DAO.Compra;
import com.gbattisti.listadecompras.DAO.CompraDAO;
import com.gbattisti.listadecompras.dominio.banco.Banco;
import com.gbattisti.listadecompras.dominio.banco.BancoBanco;
import com.gbattisti.listadecompras.dominio.banco.BancoFiltro;
import com.gbattisti.listadecompras.dominio.banco.recycler.BancoAdapter;
import com.gbattisti.listadecompras.dominio.deposito.Deposito;
import com.gbattisti.listadecompras.dominio.deposito.DepositoBanco;
import com.gbattisti.listadecompras.dominio.economia.Economia;
import com.gbattisti.listadecompras.dominio.economia.EconomiaBanco;
import com.gbattisti.listadecompras.dominio.economia.EconomiaFiltro;
import com.gbattisti.listadecompras.dominio.economia.recycler.EconomiaAdapter;
import com.gbattisti.listadecompras.dominio.ultis.banco.DB_Conexao;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final String NOME_ARQUIVO = "arquivo_listadecompras.txt";
    private static final int Activity_DADOS_PESSOAIS = 10;

    private RecyclerView recyclerViewBanco;
    private RecyclerView recyclerViewEconomia;
    private RecyclerView recyclerView;
    private BancoAdapter adapterBanco;
    private EconomiaAdapter adapterEconomia;
    private ComprasAdapter adapter;

    private TextView seu_nome;
    private TextView seu_email;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private ConstraintLayout main_layout;

    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DB_Conexao bd = new DB_Conexao(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main_layout = findViewById(R.id.main_layoutID);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.include_main).setVisibility(View.INVISIBLE);
                findViewById(R.id.include_cadastro).setVisibility(View.VISIBLE);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Obtem a referência do layout de navegação
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Obtem a referência da view de cabeçalho (importante para achar os componentes)
        View headerView = navigationView.getHeaderView(0);
        seu_nome = headerView.findViewById(R.id.seuNomeID);
        seu_email = headerView.findViewById(R.id.seuEmailID);

        // Inicialização para gravar as preferencias
        pref = getSharedPreferences("ListaComprasPrefArq", MODE_PRIVATE);
        editor = pref.edit();

        // Verifica se já foi gravado valores
        if (pref.contains("Nome")) {
            seu_nome.setText(pref.getString("Nome", "sem nome"));
            seu_email.setText(pref.getString("Email", "sem email"));
        } else {
            Snackbar.make(main_layout, "Por favor configure seu nome e email", Snackbar.LENGTH_LONG).show();
            seu_nome.setText("sem nome");
            seu_email.setText("sem email");
        }


        //        Botões de Incluir/Cancelar/Limpar
        Button btnCancelar = (Button) findViewById(R.id.btn_cancelarID);
        btnCancelar.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.include_main).setVisibility(View.VISIBLE);
                findViewById(R.id.include_cadastro).setVisibility(View.INVISIBLE);
            }
        });


        Button btnLimpar = (Button) findViewById(R.id.btn_limparID);
        btnLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Button btnSalvar = (Button) findViewById(R.id.btn_salvarID);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText txtItem = findViewById(R.id.et_itemID);
                EditText txtQuantidade = findViewById(R.id.et_quantidadeID);

                //pegando os valores
                String item = txtItem.getText().toString();
                String quantidade = txtQuantidade.getText().toString();
                if (item.equals("")) {
                    Snackbar.make(view, "Preencha o item!", Snackbar.LENGTH_SHORT).show();
                } else {
                    //salvando os dados
                    Compra compra = new Compra(0, item, quantidade);
                    CompraDAO dao = new CompraDAO(getBaseContext());
                    long salvoID = dao.salvarItem(compra);
                    if (salvoID != -1) {
                        //limpa os campos
                        txtQuantidade.setText("");
                        txtItem.setText("");

                        //adiciona no recyclerView
                        compra.setID(salvoID);
                        adapter.adicionarCompra(compra);

                        Snackbar.make(view, "Salvou!", Snackbar.LENGTH_LONG).show();
                        findViewById(R.id.include_main).setVisibility(View.VISIBLE);
                        findViewById(R.id.include_cadastro).setVisibility(View.INVISIBLE);
                    } else {
                        Snackbar.make(view, "Erro ao salvarItem, consulte os logs!", Snackbar.LENGTH_LONG).show();
                        findViewById(R.id.include_main).setVisibility(View.VISIBLE);
                        findViewById(R.id.include_cadastro).setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        configurarRecyclerBanco();
        configurarRecyclerEconomia();
        criarCadastroBanco();
        criarCadastroEconomia();
        criarDepositar();
        criarGraficoPrincipal();
        //configurarRecycler();


    }


    private void criarGraficoPrincipal(){
        try {
            pieChart = (PieChart) findViewById(R.id.grafico_Bancos);

            pieChart.setUsePercentValues(true);
            pieChart.getDescription().setEnabled(false);
            pieChart.setExtraOffsets(5, 10, 5, 5);
            pieChart.setHoleRadius(0);
            pieChart.setDragDecelerationFrictionCoef(0.95F);
            pieChart.setHoleColor(Color.WHITE);
            pieChart.setTransparentCircleAlpha(61);
            pieChart.animateY(1500, Easing.EaseInOutCubic);
            pieChart.setDrawHoleEnabled(false);

            ArrayList<PieEntry> dadosGrafico = new ArrayList<>();

            ArrayList<Banco> listaBancos = new BancoBanco(getBaseContext()).listar(new BancoFiltro());
            for (Banco banco : listaBancos){
                dadosGrafico.add(new PieEntry(banco.getSaldo().floatValue(), banco.getNome()));
            }

//            dadosGrafico.add(new PieEntry(34, "sdf"));
//            dadosGrafico.add(new PieEntry(23, "vbncv"));
//            dadosGrafico.add(new PieEntry(14, "uioyui"));
//            dadosGrafico.add(new PieEntry(53, " mcvbn"));
//            dadosGrafico.add(new PieEntry(40, "asdf"));

            PieDataSet dataSet = new PieDataSet(dadosGrafico, "TEste");
            //dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);
            dataSet.setColors(ColorTemplate.JOYFUL_COLORS);


            PieData data = new PieData(dataSet);
            data.setValueTextSize(10);
            data.setValueTextColor(Color.YELLOW);

            //data.setValueFormatter();
            pieChart.setData(data);
        }catch(Exception ex){
            //Snackbar.make(pieChart, "Erro ao gerar gráfico: "+ex.getMessage(), Snackbar.LENGTH_LONG).show();
        }

    }

    private void criarDepositar(){
        final Button buttonDepositar = (Button) findViewById(R.id.buttonDepositar);
        buttonDepositar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Spinner banco = findViewById(R.id.spinner_depositar_bancos);;
                    EditText valorDeposito = findViewById(R.id.editText_ValorDepositar);
                    Banco bancoSelecionado = (Banco) banco.getSelectedItem();

                    Deposito deposito = new Deposito();
                    deposito.setValorDeposito(Double.valueOf(valorDeposito.getText().toString()));
                    deposito.setBanco(bancoSelecionado);

                    DepositoBanco depositoBanco = new DepositoBanco(getBaseContext());
                    depositoBanco.incluir(deposito);

                    bancoSelecionado.setSaldo(bancoSelecionado.getSaldo()+deposito.getValorDeposito());
                    BancoBanco bancoBanco = new BancoBanco(getBaseContext());
                    bancoBanco.alterar(bancoSelecionado);

                    valorDeposito.setText("");

                    Snackbar.make(buttonDepositar, "Depositado com sucesso.", Snackbar.LENGTH_LONG).show();
                }catch(Exception ex){
                    Log.d("Erro", ex.getMessage());
                    Snackbar.make(buttonDepositar, "Erro ao depositar: "+ex.getMessage(), Snackbar.LENGTH_LONG).show();
                }

            }
        });
    }

    private void criarCadastroBanco(){
        final Button buttonCadastrarBanco = (Button) findViewById(R.id.buttonCadastrarBanco);
        buttonCadastrarBanco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Banco banco = new Banco();
                    EditText nomeBanco = (EditText) findViewById(R.id.editTextNomeBanco);
                    String nomeBancoString = nomeBanco.getText().toString();
                    banco.setNome(nomeBancoString);
                    BancoBanco bancoBanco = new BancoBanco(getBaseContext());
                    bancoBanco.incluir(banco);
                    adapterBanco.adicionarBanco(banco);
                    nomeBanco.setText("");
                    Snackbar.make(buttonCadastrarBanco, "Cadastrado com sucesso.", Snackbar.LENGTH_LONG).show();
                } catch (Exception ex) {
                    Snackbar.make(buttonCadastrarBanco, ex.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
    private void criarCadastroEconomia(){
       final Button buttonCadastrarEconomia = (Button) findViewById(R.id.buttonCadastrarEconomia);
        buttonCadastrarEconomia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Spinner banco = findViewById(R.id.spinner_economia_bancos);
                    EditText porcentagem = findViewById(R.id.editText_Porcentagem);
                    EditText nome = (EditText)findViewById(R.id.editText_NomeEconomia);
                    EditText meta = (EditText)findViewById(R.id.editText_Meta);

                    Banco bancoSelecionado = (Banco) banco.getSelectedItem();
                    BancoBanco bancoBanco = new BancoBanco(getBaseContext());

                    double porcentagemAtual = bancoBanco.getPorcentagemAtualNoBanco(bancoSelecionado);

                    if(porcentagemAtual > 100){
                        Snackbar.make(buttonCadastrarEconomia, "A porcentagem atual é igual a: "+porcentagemAtual, Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    Economia economia = new Economia();
                    economia.setBanco(bancoSelecionado);
                    economia.setPorcentagemDeInvestimento(Double.valueOf(porcentagem.getText().toString()));
                    economia.setNome(nome.getText().toString());
                    economia.setMeta(Double.valueOf(meta.getText().toString()));
                    //bancoBanco.getPorcentagemAtualNoBanco(bancoSelecionado)
                    if(porcentagemAtual + economia.getPorcentagemDeInvestimento() > 100){
                        Snackbar.make(buttonCadastrarEconomia, "Porcentagem máxima disponível é de: "+(100 - porcentagemAtual), Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    EconomiaBanco economiaBanco = new EconomiaBanco(getBaseContext());
                    economiaBanco.incluir(economia);

                    Snackbar.make(buttonCadastrarEconomia, "Cadastrado com sucesso.", Snackbar.LENGTH_LONG).show();

                    porcentagem.setText("");
                    nome.setText("");
                    meta.setText("");
                }catch(Exception ex){
                    Log.d("Erro", ex.getMessage());
                    Snackbar.make(buttonCadastrarEconomia, ex.getMessage(), Snackbar.LENGTH_LONG).show();
                }

            }
        });
    }

//    private void configurarRecycler() {
//        // Configurando o gerenciador de layout para ser uma lista.
//        recyclerView = (RecyclerView) findViewById(R.id.main_recyclerViewID);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//
//        // Adiciona o adapter que irá anexar os objetos à lista.
//        CompraDAO dao = new CompraDAO(this);
//        adapter = new ComprasAdapter(dao.retornarTodos());
//        recyclerView.setAdapter(adapter);
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
//
//        // Adicionar o arrastar para direita para excluir item
//
//
//    }

    private void configurarRecyclerBanco(){
        try {
            recyclerViewBanco = (RecyclerView) findViewById(R.id.recyclerView_Bancos);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerViewBanco.setLayoutManager(layoutManager);

            // Adiciona o adapter que irá anexar os objetos à lista.
            ArrayList<Banco> listaBanco = new BancoBanco(this).listar(new BancoFiltro());
            adapterBanco = new BancoAdapter(listaBanco);
            recyclerViewBanco.setAdapter(adapterBanco);
            recyclerViewBanco.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

//            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(addArrastarItemBanco());
//            itemTouchHelper.attachToRecyclerView(recyclerViewBanco);
        } catch (Exception ex) {
            Snackbar.make(recyclerViewBanco, "Erro ao configurar recycler do banco."+ex.getCause(), Snackbar.LENGTH_LONG).show();
        }

    }

    private void configurarRecyclerEconomia(){
        try {
            recyclerViewEconomia = (RecyclerView) findViewById(R.id.recyclerView_economias);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerViewEconomia.setLayoutManager(layoutManager);

            ArrayList<Economia> listaEconomia = new EconomiaBanco(this).listar(new EconomiaFiltro());
            adapterEconomia = new EconomiaAdapter(listaEconomia);
            recyclerViewEconomia.setAdapter(adapterEconomia);
            recyclerViewEconomia.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

//            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(addArrastarItemEconomia());
//            itemTouchHelper.attachToRecyclerView(recyclerViewEconomia);
        } catch (Exception ex) {
            Log.d("Erro", ex.getMessage());
            Snackbar.make(recyclerViewEconomia, "Erro ao configurar recycler da Economia."+ex.getMessage(), Snackbar.LENGTH_LONG).show();
        }

    }




    // Recebendo retorno de activity chamadas
    protected void onActivityResult(int codigo, int resultado, Intent i) {
        // se o resultado de uma Activity for da Activity_DADOS_PESSOIS
        if (codigo == Activity_DADOS_PESSOAIS) {
            // se o "i" (Intent) estiver preenchido, pega os seus dados (getExtras())
            Bundle params = i != null ? i.getExtras() : null;
            if (params != null) {
                seu_nome.setText(params.getString("Nome"));
                seu_email.setText(params.getString("Email"));
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, DadosPessoais.class);
            startActivityForResult(intent, Activity_DADOS_PESSOAIS);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_cadastrarBanco){
            cadastrarBanco();
        }else if(id == R.id.nav_cadastrarEconomia){
            cadastrarEconomia();
        }else if(id == R.id.nav_depositar){
            depositar();
        }else if(id == R.id.nav_pagina_inicial){
            paginaInicial();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void registrarSpinner(@android.support.annotation.IdRes int id_spinner){
        Spinner spinnerBancos = findViewById(id_spinner);

        BancoBanco bancoBanco = new BancoBanco(getBaseContext());
        ArrayList<Banco> listaBancos = null;
        try {
            listaBancos = bancoBanco.listar(new BancoFiltro());
            if(listaBancos.size() < 1){
                spinnerBancos.setAdapter(null);
                Toast.makeText(getBaseContext(), "Nenhum banco cadastrado, por favor cadastre um banco.", Toast.LENGTH_LONG).show();
                return;
            }
            ArrayAdapter<Banco> itensSpinnerAdapter = new ArrayAdapter<>(getBaseContext(),
                    android.R.layout.simple_spinner_item);
            for (Banco banco : listaBancos){
                itensSpinnerAdapter.add(banco);

            }
            itensSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerBancos.setAdapter(itensSpinnerAdapter);
        }
        catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void paginaInicial(){
        findViewById(R.id.include_cadastroEconomia).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_cadastroBanco).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_depositar).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_grafico).setVisibility(View.VISIBLE);

        try{
            ArrayList<PieEntry> dadosGrafico = new ArrayList<>();

            ArrayList<Banco> listaBancos = new BancoBanco(getBaseContext()).listar(new BancoFiltro());
            for (Banco banco : listaBancos){
                dadosGrafico.add(new PieEntry(banco.getSaldo().floatValue(), banco.getNome()));
            }

    //            dadosGrafico.add(new PieEntry(34, "sdf"));
    //            dadosGrafico.add(new PieEntry(23, "vbncv"));
    //            dadosGrafico.add(new PieEntry(14, "uioyui"));
    //            dadosGrafico.add(new PieEntry(53, " mcvbn"));
    //            dadosGrafico.add(new PieEntry(40, "asdf"));

            PieDataSet dataSet = new PieDataSet(dadosGrafico, "TEste");
            //dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);
            dataSet.setColors(ColorTemplate.JOYFUL_COLORS);


            PieData data = new PieData(dataSet);
            data.setValueTextSize(10);
            data.setValueTextColor(Color.YELLOW);

            pieChart.setData(data);

        }catch(Exception e){
            Toast.makeText(getBaseContext(), "Erro ao voltar para a página inicial: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void depositar(){
        findViewById(R.id.include_cadastroEconomia).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_cadastroBanco).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_depositar).setVisibility(View.VISIBLE);
        findViewById(R.id.include_grafico).setVisibility(View.INVISIBLE);

        registrarSpinner(R.id.spinner_depositar_bancos);
    }

    private void cadastrarEconomia(){
        findViewById(R.id.include_cadastroEconomia).setVisibility(View.VISIBLE);
        findViewById(R.id.include_cadastroBanco).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_depositar).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_grafico).setVisibility(View.INVISIBLE);
        try {
            ArrayList<Economia> listaEconomia = new EconomiaBanco(getBaseContext()).listar(new EconomiaFiltro());
            if(listaEconomia.size() > 0){
                RecyclerView recyclerView = findViewById(R.id.recyclerView_economias);
                recyclerView.setVisibility(View.VISIBLE);
                EconomiaAdapter economiaAdapter = new EconomiaAdapter(listaEconomia);
                recyclerView.setAdapter(economiaAdapter);
            }else{
                recyclerViewEconomia.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        registrarSpinner(R.id.spinner_economia_bancos);
//        Spinner spinnerBancos = findViewById(R.id.spinner_economia_bancos);
//
//        BancoBanco bancoBanco = new BancoBanco(getBaseContext());
//        ArrayList<Banco> listaBancos = null;
//        try {
//            listaBancos = bancoBanco.listar(new BancoFiltro());
//            if(listaBancos.size() < 1){
//                spinnerBancos.setAdapter(null);
//                Toast.makeText(getBaseContext(), "Nenhum banco cadastrado, por favor cadastre um banco.", Toast.LENGTH_LONG).show();
//                return;
//            }
//            ArrayAdapter<Banco> itensSpinnerAdapter = new ArrayAdapter<>(getBaseContext(),
//                    android.R.layout.simple_spinner_item);
//            for (Banco banco : listaBancos){
//                itensSpinnerAdapter.add(banco);
//
//            }
//            itensSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            spinnerBancos.setAdapter(itensSpinnerAdapter);
//        }
//        catch (Exception e) {
//            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//        }
    }

    private void cadastrarBanco(){
        findViewById(R.id.include_cadastroBanco).setVisibility(View.VISIBLE);
        findViewById(R.id.include_cadastroEconomia).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_depositar).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_grafico).setVisibility(View.INVISIBLE);

        try {
            ArrayList<Banco> listaBancos = new BancoBanco(getBaseContext()).listar(new BancoFiltro());
            if(listaBancos.size() > 0){
                RecyclerView recyclerView = findViewById(R.id.recyclerView_Bancos);
                recyclerView.setVisibility(View.VISIBLE);
                BancoAdapter bancoAdapter = new BancoAdapter(listaBancos);
                recyclerView.setAdapter(bancoAdapter);
            }else{
                recyclerViewBanco.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }



    public void compartilhar() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        CompraDAO dao = new CompraDAO(this);
        List<Compra> compras = dao.retornarTodos();
        String texto = "";
        for (Compra compra : compras) {
            texto = texto + compra.getItem() + "\n";
        }
        sendIntent.putExtra(Intent.EXTRA_TEXT, texto);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public ItemTouchHelper.SimpleCallback addArrastarItemBanco() {
        ItemTouchHelper.SimpleCallback deslizarItem = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(getBaseContext(), "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int deleteViewID = viewHolder.getAdapterPosition();
                AlertDialog.Builder builder = new AlertDialog.Builder(viewHolder.itemView.getContext());
                builder.setTitle("Confirmação")
                        .setMessage("Tem certeza que deseja excluir este item? " + deleteViewID)
                        .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    BancoBanco bancoBanco = new BancoBanco(getBaseContext());
                                    bancoBanco.excluir(adapterBanco.getBanco(deleteViewID));
                                    adapterBanco.removerBanco(deleteViewID);

                                }catch (Exception ex) {
                                        Snackbar.make(main_layout, "Erro ao excluir: " + ex.getCause(), Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Snackbar.make(main_layout, "Cancelando...", Snackbar.LENGTH_SHORT)
                                        .setAction("Action", null).show();
                                adapterBanco.cancelarRemocao(deleteViewID);
                            }
                        })
                        .create()
                        .show();
            }
        };
        return deslizarItem;
    }

    private void exportarLista() {

        String texto = "";
        CompraDAO dao = new CompraDAO(this);
        List<Compra> compras = dao.retornarTodos();
        for (Compra item : compras) {
            texto = texto + item.getItem() + ":" + item.getQuantidade() + "\n";

        }
        Log.w("Texto",texto);
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(NOME_ARQUIVO, Context.MODE_PRIVATE));
            outputStreamWriter.write(texto);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("MainActivity", e.toString());
        }

    }

    private void importarLista() {

        try {
            //Abrir o arquivo
            InputStream arquivo = openFileInput(NOME_ARQUIVO);
            if (arquivo != null) {
                // Conecta ao banco para remoção do  conexão
//                if (!db_conexao.onDelete()){
//                    Log.e("ListadeCompras:","Banco não removido");
//                }
                // Instancia o dao para criar o banco e a tabela
                CompraDAO dao = new CompraDAO(this);
                dao.recriarTabela();
                //ler o arquivo
                InputStreamReader inputStreamReader = new InputStreamReader(arquivo);
                //Gerar Buffer do arquivo lido
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                //Recuperar textos do arquivo
                String linhaArquivo = "";
                while ((linhaArquivo = bufferedReader.readLine()) != null) {
                    Log.i("ListadeCompras",linhaArquivo);
                    String info[] = linhaArquivo.split(":");
                    Compra item = new Compra(0, info[0], info[1]);
                    dao.salvarItem(item);
                }
                arquivo.close();

                adapter = new ComprasAdapter(dao.retornarTodos());
                recyclerView.setAdapter(adapter);
            }
        } catch (IOException e) {
            Log.e("MainActivity", e.toString());
        }
    }

}
