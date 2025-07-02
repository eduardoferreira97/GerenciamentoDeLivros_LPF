package br.com.poli.biblioteca.service;

import br.com.poli.biblioteca.model.Emprestimo;
import br.com.poli.biblioteca.model.Livro;
import br.com.poli.biblioteca.model.Usuario;
import br.com.poli.biblioteca.model.StatusEmprestimo;
import br.com.poli.biblioteca.repository.EmprestimoRepository;
import br.com.poli.biblioteca.repository.LivroRepository;
import br.com.poli.biblioteca.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

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

    public void realizarEmprestimo(String cpfUsuario, String livroIsbn) {
        // 1. Busca e valida o usuário pelo CPF
        Usuario usuario = usuarioRepository.buscarPorCPF(cpfUsuario)
                .orElseThrow(() -> new IllegalStateException("Usuário com CPF " + cpfUsuario + " não existe."));

        // 2. Valida o livro e as regras de empréstimo (reutilizando a lógica existente)
        final Livro livro = validarLivroDisponivel(livroIsbn);
        validarRegrasDeEmprestimo(usuario.getId(), livroIsbn);

        // 3. Procede com o empréstimo
        final LocalDate hoje = LocalDate.now();
        final Emprestimo novoEmprestimo = new Emprestimo(0, livroIsbn, usuario.getId(), hoje, hoje.plusWeeks(1), null, StatusEmprestimo.ATIVO);

        emprestimoRepository.salvar(novoEmprestimo);
        livroRepository.atualizarEstoque(livroIsbn, livro.getQuantidadeTotal(), livro.getQuantidadeDisponivel() - 1);

        System.out.println("-> Empréstimo realizado com sucesso para o usuário '" + usuario.getNome() + "'!");
    }

    public void devolverEmprestimo(String cpfUsuario, String livroIsbn) {
        // 1. Busca e valida o usuário pelo CPF
        Usuario usuario = usuarioRepository.buscarPorCPF(cpfUsuario)
                .orElseThrow(() -> new IllegalStateException("Usuário com CPF " + cpfUsuario + " não existe."));

        // 2. Busca o empréstimo específico que está ativo para este usuário e livro
        Emprestimo emprestimo = emprestimoRepository.buscarAtivoPorUsuarioIdELivroIsbn(usuario.getId(), livroIsbn)
                .orElseThrow(() -> new IllegalStateException("Não foi encontrado um empréstimo ativo para o livro com ISBN " + livroIsbn + " para este usuário."));

        // 3. Procede com a devolução
        final Livro livro = livroRepository.buscaPorISBN(emprestimo.getLivroIsbn()).get(); // Sabemos que o livro existe
        final Emprestimo emprestimoDevolvido = emprestimo.comStatus(StatusEmprestimo.DEVOLVIDO, LocalDate.now());

        emprestimoRepository.atualizar(emprestimoDevolvido);
        livroRepository.atualizarEstoque(livro.getIsbn(), livro.getQuantidadeTotal(), livro.getQuantidadeDisponivel() + 1);

        System.out.println("-> Devolução do livro '" + livro.getTitulo() + "' registrada com sucesso!");
    }

    public List<Emprestimo> listarHistoricoPorUsuario(String cpf) {
        // Busca o usuário pelo CPF para depois buscar o histórico pelo ID
        return usuarioRepository.buscarPorCPF(cpf)
                .map(usuario -> emprestimoRepository.buscarTodosPorUsuarioId(usuario.getId()))
                .orElse(new ArrayList<>()); // Retorna lista vazia se o usuário não for encontrado
    }

    public List<Emprestimo> listarEmprestimosAtrasados() {
        return emprestimoRepository.buscarTodosAtrasados();
    }
}