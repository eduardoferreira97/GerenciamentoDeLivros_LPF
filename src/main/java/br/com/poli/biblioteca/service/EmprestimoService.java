package br.com.poli.biblioteca.service;

import br.com.poli.biblioteca.model.Emprestimo;
import br.com.poli.biblioteca.model.Livro;
import br.com.poli.biblioteca.model.StatusEmprestimo;
import br.com.poli.biblioteca.repository.EmprestimoRepository;
import br.com.poli.biblioteca.repository.LivroRepository;
import br.com.poli.biblioteca.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.List;

public class EmprestimoService {
    private final EmprestimoRepository emprestimoRepository;
    private final LivroRepository livroRepository;
    private final UsuarioRepository usuarioRepository;

    public EmprestimoService(EmprestimoRepository emprestimoRepository, LivroRepository livroRepository, UsuarioRepository usuarioRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.livroRepository = livroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private boolean contemLivroComIsbn(final List<Emprestimo> emprestimos, final String isbn) {
        if (emprestimos.isEmpty()) return false;
        if (emprestimos.get(0).getLivroIsbn().equals(isbn)) return true;
        return contemLivroComIsbn(emprestimos.subList(1, emprestimos.size()), isbn);
    }

    private void validarUsuario(int usuarioId) {
        usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new IllegalStateException("Usuário com ID " + usuarioId + " não existe."));
    }

    private Livro validarLivroDisponivel(String livroIsbn) {
        Livro livro = livroRepository.buscaPorISBN(livroIsbn)
                .orElseThrow(() -> new IllegalStateException("Livro com ISBN " + livroIsbn + " não existe."));
        if (livro.getQuantidadeDisponivel() <= 0) {
            throw new IllegalStateException("Não há cópias disponíveis do livro '" + livro.getTitulo() + "'.");
        }
        return livro;
    }

    private void validarRegrasDeEmprestimo(int usuarioId, String livroIsbn) {
        List<Emprestimo> emprestimosAtivos = emprestimoRepository.buscarAtivosPorUsuarioId(usuarioId);
        if (emprestimosAtivos.size() >= 4) {
            throw new IllegalStateException("Usuário já atingiu o limite de 4 empréstimos ativos.");
        }
        if (contemLivroComIsbn(emprestimosAtivos, livroIsbn)) {
            throw new IllegalStateException("Usuário já possui um empréstimo ativo para este livro.");
        }
    }

    public void realizarEmprestimo(int usuarioId, String livroIsbn) {
        validarUsuario(usuarioId);
        final Livro livro = validarLivroDisponivel(livroIsbn);
        validarRegrasDeEmprestimo(usuarioId, livroIsbn);

        final LocalDate hoje = LocalDate.now();
        final Emprestimo novoEmprestimo = new Emprestimo(0, livroIsbn, usuarioId, hoje, hoje.plusWeeks(1), null, StatusEmprestimo.ATIVO);

        emprestimoRepository.salvar(novoEmprestimo);
        livroRepository.atualizarEstoque(livroIsbn, livro.getQuantidadeTotal(), livro.getQuantidadeDisponivel() - 1);

        System.out.println("-> Empréstimo realizado com sucesso!");
    }

    public void devolverEmprestimo(int emprestimoId) {
        final Emprestimo emprestimo = emprestimoRepository.buscarPorId(emprestimoId)
                .orElseThrow(() -> new IllegalStateException("Empréstimo com ID " + emprestimoId + " não encontrado."));
        if (emprestimo.getStatus() == StatusEmprestimo.DEVOLVIDO) {
            throw new IllegalStateException("Este livro já foi devolvido.");
        }
        final Livro livro = livroRepository.buscaPorISBN(emprestimo.getLivroIsbn()).get();
        final Emprestimo emprestimoDevolvido = emprestimo.comStatus(StatusEmprestimo.DEVOLVIDO, LocalDate.now());
        emprestimoRepository.atualizar(emprestimoDevolvido);
        livroRepository.atualizarEstoque(livro.getIsbn(), livro.getQuantidadeTotal(), livro.getQuantidadeDisponivel() + 1);
        System.out.println("-> Devolução registrada com sucesso!");
    }

    public List<Emprestimo> listarHistoricoPorUsuario(int usuarioId) {
        return emprestimoRepository.buscarTodosPorUsuarioId(usuarioId);
    }

    public List<Emprestimo> listarEmprestimosAtrasados() {
        return emprestimoRepository.buscarTodosAtrasados();
    }
}