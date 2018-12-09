package com.trabalho.economia.dominio.economia;

import android.util.Log;

import com.trabalho.economia.dominio.banco.Banco;

import java.util.Objects;

public class Economia {
    private int ID;
    private Banco banco;
    private String nome;
    private Double porcentagemDeInvestimento;
    private Double meta;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPorcentagemDeInvestimento() {
        return porcentagemDeInvestimento;
    }

    public void setPorcentagemDeInvestimento(Double porcentagemDeInvestimento) {
        this.porcentagemDeInvestimento = porcentagemDeInvestimento;
    }

    public Double getMeta() {
        return meta;
    }

    public void setMeta(Double meta) {
        this.meta = meta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Economia economia = (Economia) o;
        return ID == economia.ID &&
                Objects.equals(banco, economia.banco) &&
                Objects.equals(nome, economia.nome) &&
                Objects.equals(porcentagemDeInvestimento, economia.porcentagemDeInvestimento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, banco, nome, porcentagemDeInvestimento);
    }

    public Double getSaldoEconomia(){
        if(getBanco() == null){
            Log.d("banco", "null");
        }
        Double valorBanco = getBanco().getSaldo();
        Double porcentagem = getPorcentagemDeInvestimento();

        return valorBanco == null ? 0.0 : porcentagem*valorBanco/100.0;
    }


    @Override
    public String toString() {
        return nome;
    }
}