package com.trabalho.economia.dominio.banco;

import android.util.Log;

import java.util.Objects;

public class Banco {
    private int ID;
    private String nome;
    private Double saldo;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getSaldo() {
        Log.d("SADO", saldo.toString());
        return saldo == null ? 0.0 : saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Banco banco = (Banco) o;
        return ID == banco.ID &&
                Objects.equals(nome, banco.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, nome, saldo);
    }

    @Override
    public String toString() {
        return nome;
    }


}
