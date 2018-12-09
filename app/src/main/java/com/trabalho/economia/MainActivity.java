package com.trabalho.economia;


import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.trabalho.economia.dominio.banco.BancoBanco;
import com.trabalho.economia.dominio.banco.recycler.BancoAdapter;
import com.trabalho.economia.dominio.economia.EconomiaBanco;
import com.trabalho.economia.dominio.economia.recycler.EconomiaAdapter;
import com.trabalho.economia.dominio.ultis.banco.DB_Conexao;
import com.trabalho.economia.dominio.banco.Banco;
import com.trabalho.economia.dominio.banco.BancoFiltro;
import com.trabalho.economia.dominio.deposito.Deposito;
import com.trabalho.economia.dominio.deposito.DepositoBanco;
import com.trabalho.economia.dominio.economia.Economia;
import com.trabalho.economia.dominio.economia.EconomiaFiltro;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerViewBanco;
    private RecyclerView recyclerViewEconomia;
    private BancoAdapter adapterBanco;
    private EconomiaAdapter adapterEconomia;

    private ConstraintLayout main_layout;

    private PieChart pieChartBancos;
    private PieChart pieChartEconomias;
    private BarChart barChartEconomia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DB_Conexao bd = new DB_Conexao(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main_layout = findViewById(R.id.main_layoutID);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Obtem a referência do layout de navegação
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        configurarRecyclerBanco();
        configurarRecyclerEconomia();
        criarCadastroBanco();
        criarCadastroEconomia();
        criarDepositar();
        criarGraficoPrincipal();
        criarVerificarEconomia();
        criarVerificarEconomiaPorBanco();

    }

    private void criarVerificarEconomiaPorBanco(){
            final Spinner spinnerBancos = (Spinner) findViewById(R.id.spinner_verificar_economias_por_banco);
            registrarSpinner(R.id.spinner_verificar_economias_por_banco);
            Log.d("TEste", "teste");
            spinnerBancos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        Banco bancoSelecionado = (Banco) spinnerBancos.getSelectedItem();

                        pieChartEconomias = (PieChart) findViewById(R.id.grafico_economias_por_banco);

                        pieChartEconomias.setUsePercentValues(true);
                        pieChartEconomias.getDescription().setEnabled(false);
                        pieChartEconomias.setExtraOffsets(5, 10, 5, 5);
                        pieChartEconomias.setHoleRadius(0);
                        pieChartEconomias.setDragDecelerationFrictionCoef(0.95F);
                        pieChartEconomias.setHoleColor(Color.BLACK);
                        pieChartEconomias.setTransparentCircleAlpha(61);
                        pieChartEconomias.animateY(1500, Easing.EaseInOutCubic);
                        pieChartEconomias.setDrawHoleEnabled(false);

                        ArrayList<PieEntry> dadosGrafico = new ArrayList<>();

                        EconomiaFiltro economiaFiltro = new EconomiaFiltro();
                        economiaFiltro.setBanco(bancoSelecionado);
                        ArrayList<Economia> listaEconomias = new EconomiaBanco(getBaseContext()).listar(economiaFiltro);
                        Double porcentagemUtilizada = 0.0;
                        for (Economia economia : listaEconomias){
                            porcentagemUtilizada += economia.getPorcentagemDeInvestimento();
                            dadosGrafico.add(new PieEntry(economia.getPorcentagemDeInvestimento().floatValue(), economia.getNome()+"-"+economia.getSaldoEconomia()+""));
                        }

                        if(porcentagemUtilizada != 100){
                            dadosGrafico.add(new PieEntry(new Float(100 - porcentagemUtilizada), "Disponível"));
                        }
                        PieDataSet dataSet = new PieDataSet(dadosGrafico, "Bancos");
                        dataSet.setSelectionShift(5f);
                        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);


                        PieData data = new PieData(dataSet);
                        data.setDrawValues(false);

                        pieChartEconomias.setData(data);
                    }catch(Exception ex){
                        Snackbar.make(pieChartEconomias, "Erro ao gerar gráfico: "+ex.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


    }

    private void criarVerificarEconomia(){
        Button botao_DobrarMeta = findViewById(R.id.button_dobrarMeta);
        final Button botao_ConcluirEconomia = findViewById(R.id.button_concluirEconomia);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner_verificar_economias);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Economia economia = (Economia) spinner.getSelectedItem();
                criarGraficoEconomia(economia);
                if(economia.getSaldoEconomia() >= economia.getMeta()){
                    botao_ConcluirEconomia.setVisibility(View.VISIBLE);
                }else{
                    botao_ConcluirEconomia.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        botao_DobrarMeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Economia economia = (Economia) spinner.getSelectedItem();
                economia.setMeta(economia.getMeta()*2);
                EconomiaBanco economiaBanco = new EconomiaBanco(getBaseContext());
                economiaBanco.alterar(economia);
                criarGraficoEconomia(economia);
                try {
                    ArrayList<Economia> listar = economiaBanco.listar(new EconomiaFiltro());
                    for (Economia e : listar){
                        Log.d("a", String.valueOf(e.getMeta()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(economia.getSaldoEconomia() >= economia.getMeta()){
                    botao_ConcluirEconomia.setVisibility(View.VISIBLE);
                }else{
                    botao_ConcluirEconomia.setVisibility(View.INVISIBLE);
                }
            }
        });

        botao_ConcluirEconomia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Economia economia = (Economia) spinner.getSelectedItem();
                    Banco banco = economia.getBanco();
                    banco.setSaldo(banco.getSaldo() - economia.getSaldoEconomia());
                    EconomiaBanco economiaBanco = new EconomiaBanco(getBaseContext());
                    economiaBanco.excluir(economia);
                    BancoBanco bancoBanco = new BancoBanco(getBaseContext());
                    bancoBanco.alterar(banco);
                    verificarEconomias();
                }catch(Exception ex){
                    Snackbar.make(pieChartBancos, "Erro ao finalizar a economia: "+ex.getMessage(), Snackbar.LENGTH_LONG).show();
                }

            }
        });
    }

    private void criarGraficoEconomia(Economia economia){
        barChartEconomia = (BarChart) findViewById(R.id.grafico_EconomiaSelecionada);
        if(economia == null){
            barChartEconomia.setVisibility(View.INVISIBLE);
            return;
        }
        barChartEconomia.setVisibility(View.VISIBLE);
        barChartEconomia.animateX(1500);
        barChartEconomia.setFitBars(true);
        barChartEconomia.getDescription().setEnabled(false);

        ArrayList<BarEntry> dadosGrafico = new ArrayList<>();
        dadosGrafico.add(new BarEntry(2, economia.getSaldoEconomia().floatValue(), "Economia"));
        dadosGrafico.add(new BarEntry(3, economia.getMeta().floatValue(), "Meta"));
        dadosGrafico.add(new BarEntry(1, 0, ""));



        BarDataSet barDataSet = new BarDataSet(dadosGrafico, "Economia");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setDrawValues(true);
        barDataSet.setBarBorderWidth(0.2f);

        BarData barData = new BarData(barDataSet);
        barData.setDrawValues(false);


        barChartEconomia.setData(barData);
        barChartEconomia.invalidate();
    }

    private void criarGraficoPrincipal(){
        try {
            pieChartBancos = (PieChart) findViewById(R.id.grafico_Bancos);

            pieChartBancos.setUsePercentValues(true);
            pieChartBancos.getDescription().setEnabled(false);
            pieChartBancos.setExtraOffsets(5, 10, 5, 5);
            pieChartBancos.setHoleRadius(0);
            pieChartBancos.setDragDecelerationFrictionCoef(0.95F);
            pieChartBancos.setHoleColor(Color.BLACK);
            pieChartBancos.setTransparentCircleAlpha(61);
            pieChartBancos.animateY(1500, Easing.EaseInOutCubic);
            pieChartBancos.setDrawHoleEnabled(false);

            ArrayList<PieEntry> dadosGrafico = new ArrayList<>();

            ArrayList<Banco> listaBancos = new BancoBanco(getBaseContext()).listar(new BancoFiltro());
            for (Banco banco : listaBancos){
                if(banco.getSaldo() < 1){
                    continue;
                }
                dadosGrafico.add(new PieEntry(banco.getSaldo().floatValue(), banco.getNome()+" - "+banco.getSaldo()));
            }

            PieDataSet dataSet = new PieDataSet(dadosGrafico, "Bancos");
            //dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);


            PieData data = new PieData(dataSet);
            data.setDrawValues(false);

            pieChartBancos.setData(data);
        }catch(Exception ex){
            Snackbar.make(pieChartBancos, "Erro ao gerar gráfico: "+ex.getMessage(), Snackbar.LENGTH_LONG).show();
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

                    EconomiaFiltro economiaFiltro = new EconomiaFiltro();
                    economiaFiltro.setBanco(bancoSelecionado);
                    final EconomiaBanco economiaBanco = new EconomiaBanco(getBaseContext());
                    ArrayList<Economia> listaEconomia = economiaBanco.listar(economiaFiltro);
                    int qtdMetasBatidas = 0;
                    for (Economia economia : listaEconomia){
                        if(economia.getSaldoEconomia() >= economia.getMeta()){
                            qtdMetasBatidas++;
                        }
                    }

                    if(qtdMetasBatidas > 0){
                        String string = "Você bateu ";
                        string = string.concat(qtdMetasBatidas == 1 ? 1 + " meta. Parabéns!" :
                                qtdMetasBatidas + "metas. Parabéns!");
                        Snackbar.make(buttonDepositar, string, Snackbar.LENGTH_LONG).show();
                    }

                    valorDeposito.setText("");


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
                    banco.setSaldo(0d);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
        }else if(id == R.id.nav_verificarEconomias){
            verificarEconomias();
        }else if(id == R.id.nav_VerificarEconomiasPorBanco){
            verificarEconomiasPorBanco();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void verificarEconomiasPorBanco(){
        findViewById(R.id.include_cadastroEconomia).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_cadastroBanco).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_depositar).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_grafico).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_verificar_economias).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_verificar_economias_por_banco).setVisibility(View.VISIBLE);

        registrarSpinner(R.id.spinner_verificar_economias_por_banco);
        BancoBanco bancoBanco = new BancoBanco(getBaseContext());
        try {
            ArrayList<Banco> lista = bancoBanco.listar(new BancoFiltro());
            if(lista.isEmpty()){
                pieChartEconomias.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void verificarEconomias(){
        findViewById(R.id.include_cadastroEconomia).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_cadastroBanco).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_depositar).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_grafico).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_verificar_economias).setVisibility(View.VISIBLE);
        findViewById(R.id.include_verificar_economias_por_banco).setVisibility(View.INVISIBLE);

        Spinner spinnerEconomias = findViewById(R.id.spinner_verificar_economias);

        EconomiaBanco economiaBanco = new EconomiaBanco(getBaseContext());
        ArrayList<Economia> listaEconomia = null;
        try {
            listaEconomia = economiaBanco.listar(new EconomiaFiltro());
            if(listaEconomia.size() < 1){
                spinnerEconomias.setAdapter(null);
                Toast.makeText(getBaseContext(), "Nenhuma economia cadastrada, por favor cadastre uma.", Toast.LENGTH_LONG).show();
                criarGraficoEconomia(null);
                return;
            }
            ArrayAdapter<Economia> itensSpinnerAdapter = new ArrayAdapter<>(getBaseContext(),
                    android.R.layout.simple_spinner_item);
            for (Economia economia : listaEconomia){
                itensSpinnerAdapter.add(economia);
            }
            itensSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerEconomias.setAdapter(itensSpinnerAdapter);
        }
        catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
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
        findViewById(R.id.include_verificar_economias).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_verificar_economias_por_banco).setVisibility(View.INVISIBLE);

        try{
            ArrayList<PieEntry> dadosGrafico = new ArrayList<>();

            ArrayList<Banco> listaBancos = new BancoBanco(getBaseContext()).listar(new BancoFiltro());
            for (Banco banco : listaBancos){
                if(banco.getSaldo() < 1){
                    continue;
                }
                dadosGrafico.add(new PieEntry(banco.getSaldo().floatValue(), banco.getNome()+" - "+banco.getSaldo()));
            }

            PieDataSet dataSet = new PieDataSet(dadosGrafico, "Bancos");

            //dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);


            PieData data = new PieData(dataSet);
            data.setValueTextSize(10);
            data.setValueTextColor(Color.BLACK);
            data.setDrawValues(false);

            pieChartBancos.setData(data);
            pieChartBancos.invalidate();
        }catch(Exception e){
            Toast.makeText(getBaseContext(), "Erro ao voltar para a página inicial: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void depositar(){
        findViewById(R.id.include_cadastroEconomia).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_cadastroBanco).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_depositar).setVisibility(View.VISIBLE);
        findViewById(R.id.include_grafico).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_verificar_economias).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_verificar_economias_por_banco).setVisibility(View.INVISIBLE);

        registrarSpinner(R.id.spinner_depositar_bancos);
    }

    private void cadastrarEconomia(){
        findViewById(R.id.include_cadastroEconomia).setVisibility(View.VISIBLE);
        findViewById(R.id.include_cadastroBanco).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_depositar).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_grafico).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_verificar_economias).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_verificar_economias_por_banco).setVisibility(View.INVISIBLE);

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
    }

    private void cadastrarBanco(){
        findViewById(R.id.include_cadastroBanco).setVisibility(View.VISIBLE);
        findViewById(R.id.include_cadastroEconomia).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_depositar).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_grafico).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_verificar_economias).setVisibility(View.INVISIBLE);
        findViewById(R.id.include_verificar_economias_por_banco).setVisibility(View.INVISIBLE);

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





}
