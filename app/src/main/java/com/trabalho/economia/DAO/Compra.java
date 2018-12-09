package com.trabalho.economia.DAO;

public class Compra {


    private long ID;
    private String item;
    private String quantidade;

    public Compra(long ID, String item, String quantidade) {
        this.ID = ID;
        this.item = item;
        this.quantidade = quantidade;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }
}
