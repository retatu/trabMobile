package com.gbattisti.listadecompras.dominio.economia;

import com.gbattisti.listadecompras.dominio.banco.Banco;

import java.util.Objects;

public class Economia {
    private int ID;
    private Banco banco;
    private String nome;
    private Double saldo;
    private Double porcentagemDeInvestimento;

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

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public Double getPorcentagemDeInvestimento() {
        return porcentagemDeInvestimento;
    }

    public void setPorcentagemDeInvestimento(Double porcentagemDeInvestimento) {
        this.porcentagemDeInvestimento = porcentagemDeInvestimento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Economia economia = (Economia) o;
        return ID == economia.ID &&
                Objects.equals(banco, economia.banco) &&
                Objects.equals(nome, economia.nome) &&
                Objects.equals(saldo, economia.saldo) &&
                Objects.equals(porcentagemDeInvestimento, economia.porcentagemDeInvestimento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, banco, nome, saldo, porcentagemDeInvestimento);
    }

}