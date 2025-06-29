package br.com.poli.biblioteca.model;

public abstract class Usuario {
    private final int id;
    private final String nome;
    private final String email;

    public Usuario(int id, String nome, String email){
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public abstract String getTipo();
}
