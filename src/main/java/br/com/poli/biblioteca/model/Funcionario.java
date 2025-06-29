package br.com.poli.biblioteca.model;

public final class Funcionario extends Usuario {
    private final String cargo;

    public Funcionario(int id, String nome, String email, String cargo){
        super(id, nome, email);
        this.cargo = cargo;
    }

    public String getCargo() {
        return cargo;
    }

    @Override
    public String getTipo(){
        return "Funcionario";
    }
}
