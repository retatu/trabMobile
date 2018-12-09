package com.trabalho.economia.dominio.deposito;

import com.trabalho.economia.dominio.banco.Banco;

public class Deposito {
    private int id;
    private Double valorDeposito;
    private Banco banco;


    public Double getValorDeposito() {
        return valorDeposito;
    }

    public void setValorDeposito(Double valorDeposito) {
        this.valorDeposito = valorDeposito;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }
}
