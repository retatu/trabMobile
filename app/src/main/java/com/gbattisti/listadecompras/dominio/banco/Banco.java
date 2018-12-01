package com.gbattisti.listadecompras.dominio.banco;

import java.util.Objects;

public class Banco {
    private int ID;
    private String nome;

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
        return Objects.hash(ID, nome);
    }

    @Override
    public String toString() {
        return nome;
    }
}
