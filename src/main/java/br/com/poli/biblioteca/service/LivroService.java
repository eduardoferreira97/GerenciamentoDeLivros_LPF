package br.com.poli.biblioteca.service;

import br.com.poli.biblioteca.model.Livro;
import br.com.poli.biblioteca.repository.LivroRepository;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Random;

public class LivroService {
    private final LivroRepository livroRepository;

    public LivroService(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    private StringBuilder gerarIsbn(Random random, int digitosRestantes, StringBuilder isbnAtual) {
        if (digitosRestantes == 0) {
            return isbnAtual;
        }
        isbnAtual.append(random.nextInt(10));
        return gerarIsbn(random, digitosRestantes - 1, isbnAtual);
    }

    private String gerarIsbnUnico() {
        final String candidatoIsbn = gerarIsbn(new Random(), 13, new StringBuilder()).toString();
        final boolean existe = this.livroRepository.buscaPorISBN(candidatoIsbn).isPresent();

        if (!existe) {
            return candidatoIsbn;
        } else {
            return gerarIsbnUnico();
        }
    }

    public List<Livro> buscarLivrosDinamicamente(String termoDeBusca) {
        if (termoDeBusca == null || termoDeBusca.isBlank()) {
            return new ArrayList<>();
        }
        return this.livroRepository.buscarPorTermo(termoDeBusca);
    }

    public List<Livro> listarTodosLivros() {
        return this.livroRepository.buscarTodos();
    }

    public void removerLivro(String isbn) {
        if (isbn == null || isbn.isBlank()) throw new IllegalArgumentException("ISBN para remoção não pode ser vazio.");
        livroRepository.buscaPorISBN(isbn).orElseThrow(() -> new IllegalStateException("Livro com ISBN " + isbn + " não encontrado."));
        livroRepository.removerlivroPorIsbn(isbn);
    }

    public Livro cadastrarOuAdicionarEstoque(Livro dadosDoFormulario) {
        final Optional<Livro> livroExistenteOpt = this.livroRepository.buscarPorTituloEAutor(
                dadosDoFormulario.getTitulo(),
                dadosDoFormulario.getAutor()
        );

        if (livroExistenteOpt.isPresent()) {
            final Livro livroExistente = livroExistenteOpt.get();
            final int quantidadeAdicional = dadosDoFormulario.getQuantidadeTotal();
            final int novaQuantidadeTotal = livroExistente.getQuantidadeTotal() + quantidadeAdicional;
            final int novaQuantidadeDisponivel = livroExistente.getQuantidadeDisponivel() + quantidadeAdicional;
            this.livroRepository.atualizarEstoque(livroExistente.getIsbn(), novaQuantidadeTotal, novaQuantidadeDisponivel);
            System.out.println("-> Livro '" + livroExistente.getTitulo() + "' atualizado com sucesso!");
            return new Livro(livroExistente.getIsbn(), livroExistente.getTitulo(), livroExistente.getAutor(), livroExistente.getAnoPublicacao(), novaQuantidadeTotal, novaQuantidadeDisponivel);
        } else {
            final String novoIsbn = gerarIsbnUnico();
            final Livro livroParaSalvar = new Livro(novoIsbn, dadosDoFormulario.getTitulo(), dadosDoFormulario.getAutor(), dadosDoFormulario.getAnoPublicacao(), dadosDoFormulario.getQuantidadeTotal(), dadosDoFormulario.getQuantidadeTotal());
            this.livroRepository.adicionarLivro(livroParaSalvar);
            System.out.println("-> Novo livro '" + livroParaSalvar.getTitulo() + "' cadastrado com sucesso!");
            return livroParaSalvar;
        }
    }
}
