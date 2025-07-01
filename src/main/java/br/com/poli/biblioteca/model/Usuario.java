package br.com.poli.biblioteca.model;

public abstract class Usuario {
    private final int id;
    private final String nome;
    private final String email;
    private final String cpf;

    public Usuario(int id, String nome, String email, String cpf) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
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

    public String getCPF() {
        return cpf;
    }

    public abstract String getTipo();
}
