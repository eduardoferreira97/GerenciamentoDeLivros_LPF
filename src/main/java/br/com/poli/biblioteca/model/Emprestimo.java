package br.com.poli.biblioteca.model;

import java.time.LocalDate;

public final class Emprestimo {
    private final int id;
    private final String livroIsbn;
    private final int usuarioId;
    private final LocalDate dataEmprestimo;
    private final LocalDate dataPrevistaDevolucao;
    private final LocalDate dataDevolucaoReal;
    private final StatusEmprestimo status;

    public Emprestimo(int id, String livroIsbn, int usuarioId, LocalDate dataEmprestimo, LocalDate dataPrevistaDevolucao, LocalDate dataDevolucaoReal, StatusEmprestimo status) {
        this.id = id;
        this.livroIsbn = livroIsbn;
        this.usuarioId = usuarioId;
        this.dataEmprestimo = dataEmprestimo;
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
        this.dataDevolucaoReal = dataDevolucaoReal;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getLivroIsbn() {
        return livroIsbn;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public LocalDate getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }

    public LocalDate getDataDevolucaoReal() {
        return dataDevolucaoReal;
    }

    public StatusEmprestimo getStatus() {
        return status;
    }

    // Função funcional para criar uma cópia imutável com status atualizado
    public Emprestimo comStatus(StatusEmprestimo novoStatus, LocalDate dataDevolucao) {
        return new Emprestimo(this.id, this.livroIsbn, this.usuarioId, this.dataEmprestimo, this.dataPrevistaDevolucao, dataDevolucao, novoStatus);
    }
}