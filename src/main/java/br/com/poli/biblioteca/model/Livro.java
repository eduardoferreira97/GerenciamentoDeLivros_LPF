package br.com.poli.biblioteca.model;

public final class Livro {
    private final String isbn;
    private final String titulo;
    private final String autor;
    private final int anoPublicacao;
    private final int quantidadeTotal;
    private final int quantidadeDisponivel;
    
    public Livro(String isbn, String titulo, String autor, int anoPublicacao, int quantidadeTotal, int quantidadeDisponivel){
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.anoPublicacao = anoPublicacao;
        this.quantidadeTotal = quantidadeTotal;
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public int getAnoPublicacao() {
        return anoPublicacao;
    }

    public int getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }
}
