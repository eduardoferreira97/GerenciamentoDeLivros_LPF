package br.com.poli.biblioteca.model;

public final class Aluno extends Usuario {
    private final String matricula;

    public Aluno(int id, String nome, String email, String matricula, String cpf){
        super(id, nome, email, cpf);
        this.matricula = matricula;
    }

    public String getMatricula() {
        return matricula;
    }

    @Override
    public String getTipo() {
        return "Aluno";
    }
}
