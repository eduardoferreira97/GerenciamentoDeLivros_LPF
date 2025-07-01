package br.com.poli.biblioteca;

import br.com.poli.biblioteca.model.*;
import br.com.poli.biblioteca.repository.*;
import br.com.poli.biblioteca.service.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final LivroService livroService;
    private static final UsuarioService usuarioService;
    private static final EmprestimoService emprestimoService;
    private static final LoginService loginService;

    static {
        DatabaseSetup.criarTabelas();
        UsuarioRepository usuarioRepository = new UsuarioRepository();
        LivroRepository livroRepository = new LivroRepository();
        EmprestimoRepository emprestimoRepository = new EmprestimoRepository();

        criarUsuariosPadraoSeNaoExistirem(usuarioRepository);

        livroService = new LivroService(livroRepository);
        usuarioService = new UsuarioService(usuarioRepository);
        emprestimoService = new EmprestimoService(emprestimoRepository, livroRepository, usuarioRepository);
        loginService = new LoginService(usuarioRepository);
    }

    // Seu método de criar usuários padrão, ajustado para o novo construtor
    private static void criarUsuariosPadraoSeNaoExistirem(UsuarioRepository repo) {
        if (repo.buscarPorEmail("admin@biblioteca.com").isEmpty()) {
            Funcionario admin = new Funcionario(0, "Admin", "admin@biblioteca.com", "admin123", "Administrador");
            repo.salvarUsuario(admin);
            System.out.println("-> Usuário 'Admin' padrão criado.");
        }
    }

    public static void main(String[] args) {
        System.out.println("--- BEM-VINDO AO SISTEMA DA BIBLIOTECA ---");
        Optional<Usuario> usuarioLogadoOpt = autenticarUsuario();

        usuarioLogadoOpt.ifPresentOrElse(
                Main::iniciarSessaoPrincipal, // Se o login for sucesso, inicia a sessão
                () -> System.out.println("\nFalha na autenticação. O sistema será encerrado.")
        );

        System.out.println("\nSistema finalizado.");
        scanner.close();
    }

    private static Optional<Usuario> autenticarUsuario() {
        System.out.print("Login (email): ");
        String login = scanner.nextLine();
        System.out.print("Senha (seu CPF): ");
        String senha = scanner.nextLine();
        return loginService.autenticar(login, senha);
    }

    private static void iniciarSessaoPrincipal(Usuario usuarioLogado) {
        System.out.println("\nLogin bem-sucedido! Bem-vindo(a), " + usuarioLogado.getNome() + ".");
        boolean rodando = true;

        while (rodando) {
            exibirMenu(usuarioLogado);
            int opcao = lerOpcao();

            // Roteador de opções geral
            if (usuarioLogado instanceof Funcionario) {
                rodando = executarAcaoFuncionario(opcao);
            } else if (usuarioLogado instanceof Aluno) {
                rodando = executarAcaoAluno(opcao, usuarioLogado.getId());
            }
        }
    }

    private static boolean executarAcaoFuncionario(int opcao) {
        switch (opcao) {
            case 1 -> listarTodosLivros();
            case 2 -> buscarLivroInterativo();
            case 3 -> adicionarOuAtualizarLivro();
            case 5 -> listarTodosUsuarios();
            case 6 -> cadastrarNovoUsuario();
            case 7 -> buscarUsuarioInterativo();
            case 8 -> removerUsuario();
            case 10 -> realizarNovoEmprestimo();
            case 11 -> registrarDevolucao();
            case 12 -> listarEmprestimosAtrasados();
            case 13 -> verHistoricoDeUsuario();
            case 0 -> { return false; } // Para sair do loop
            default -> System.out.println("Opção inválida. Tente novamente.");
        }
        return true; // Para continuar no loop
    }

    private static boolean executarAcaoAluno(int opcao, int usuarioId) {
        switch (opcao) {
            case 1 -> listarTodosLivros();
            case 2 -> buscarLivroInterativo();
            case 13 -> verMeuHistorico(usuarioId); // Aluno só pode ver o próprio histórico
            case 0 -> { return false; }
            default -> System.out.println("Opção inválida. Tente novamente.");
        }
        return true;
    }

    private static void exibirMenu(Usuario usuarioLogado) {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("Usuário: " + usuarioLogado.getNome() + " | Tipo: " + usuarioLogado.getTipo());
        System.out.println("--------------------");

        // Menu Geral
        System.out.println("1. Listar todos os livros");
        System.out.println("2. Busca dinâmica de livros");

        if (usuarioLogado instanceof Funcionario) {
            System.out.println("\n--- Gestão de Acervo (Funcionário) ---");
            System.out.println("3. Adicionar/Atualizar estoque de livro");
            System.out.println("\n--- Gestão de Usuários (Funcionário) ---");
            System.out.println("5. Listar todos os usuários");
            System.out.println("6. Cadastrar novo usuário");
            System.out.println("7. Busca dinâmica de usuários");
            System.out.println("8. Remover um usuário");
            System.out.println("\n--- Gestão de Empréstimos (Funcionário) ---");
            System.out.println("10. Realizar um Empréstimo");
            System.out.println("11. Registrar uma Devolução");
            System.out.println("12. Listar Empréstimos Atrasados");
            System.out.println("13. Ver Histórico de um Usuário");
        } else if (usuarioLogado instanceof Aluno) {
            System.out.println("\n--- Minha Conta (Aluno) ---");
            System.out.println("13. Ver meu histórico de empréstimos");
        }

        System.out.println("--------------------");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static int lerOpcao() { /* ... seu código existente ... */ return 0; }
    private static void listarTodosLivros() { /* ... seu código existente ... */ }
    private static void buscarLivroInterativo() { /* ... seu código existente ... */ }
    private static void adicionarOuAtualizarLivro() { /* ... seu código existente ... */ }
    private static void listarTodosUsuarios() { /* ... seu código existente ... */ }
    private static void cadastrarNovoUsuario() { /* ... seu código existente ... */ }
    private static void buscarUsuarioInterativo() { /* ... seu código existente ... */ }
    private static void removerUsuario() { /* ... seu código existente ... */ }

    // --- NOVOS MÉTODOS DE EMPRÉSTIMO ---

    private static void realizarNovoEmprestimo() {
        System.out.println("\n--- Realizar Novo Empréstimo ---");
        try {
            System.out.println("Usuários disponíveis:");
            listarTodosUsuarios();
            System.out.print("Digite o ID do usuário: ");
            int usuarioId = Integer.parseInt(scanner.nextLine());

            System.out.println("\nLivros disponíveis:");
            listarTodosLivros();
            System.out.print("Digite o ISBN do livro: ");
            String livroIsbn = scanner.nextLine();

            emprestimoService.realizarEmprestimo(usuarioId, livroIsbn);
        } catch (Exception e) {
            System.err.println("Não foi possível realizar o empréstimo: " + e.getMessage());
        }
    }

    private static void registrarDevolucao() {
        System.out.println("\n--- Registrar Devolução ---");
        try {
            // Seria ideal ter uma função para listar apenas os empréstimos ATIVOS
            System.out.print("Digite o ID do empréstimo a ser devolvido: ");
            int emprestimoId = Integer.parseInt(scanner.nextLine());
            emprestimoService.devolverEmprestimo(emprestimoId);
        } catch (Exception e) {
            System.err.println("Não foi possível registrar a devolução: " + e.getMessage());
        }
    }

    private static void listarEmprestimosAtrasados() {
        System.out.println("\n--- Empréstimos Atrasados ---");
        List<Emprestimo> atrasados = emprestimoService.listarEmprestimosAtrasados();
        if (atrasados.isEmpty()) {
            System.out.println("Não há empréstimos atrasados no momento.");
        } else {
            atrasados.forEach(e ->
                    System.out.println("  - ID Emp.: " + e.getId() + " | Usuário ID: " + e.getUsuarioId() + " | Livro ISBN: " + e.getLivroIsbn() + " | Previsto para: " + e.getDataPrevistaDevolucao())
            );
        }
    }

    private static void verHistoricoDeUsuario() {
        System.out.println("\n--- Histórico de Empréstimos por Usuário ---");
        try {
            System.out.print("Digite o ID do usuário para ver seu histórico: ");
            int usuarioId = Integer.parseInt(scanner.nextLine());
            listarHistorico(usuarioId);
        } catch (Exception e) {
            System.err.println("Erro ao buscar histórico: " + e.getMessage());
        }
    }

    private static void verMeuHistorico(int meuId) {
        System.out.println("\n--- Meu Histórico de Empréstimos ---");
        listarHistorico(meuId);
    }

    private static void listarHistorico(int usuarioId) {
        List<Emprestimo> historico = emprestimoService.listarHistoricoPorUsuario(usuarioId);
        if (historico.isEmpty()) {
            System.out.println("Este usuário não possui empréstimos.");
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            historico.forEach(e ->
                    System.out.println("  - Livro ISBN: " + e.getLivroIsbn() + " | Status: " + e.getStatus() + " | Data: " + e.getDataEmprestimo().format(formatter))
            );
        }
    }
}