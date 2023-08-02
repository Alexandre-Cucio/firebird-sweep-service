package br.com.sweep.entity;

public class Destinatario {
    String endereco;
    String nome;
    
    public Destinatario(String endereco, String nome){
        this.endereco = endereco;
        this.nome = nome;
    }

    public String getEndereco(){
        return this.endereco;
    }

    public String getNome(){
        return this.nome;
    }
    
}
