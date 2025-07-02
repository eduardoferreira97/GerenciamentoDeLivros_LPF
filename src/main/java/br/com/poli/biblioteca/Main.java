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

    private static void criarUsuariosPadraoSeNaoExistirem(UsuarioRepository repo) {
        if (repo.buscarPorEmail("admin@biblioteca.com").isEmpty()) {
            // Construtor corrigido: id, nome, email, cargo, cpf
            Funcionario admin = new Funcionario(0, "Admin", "admin@biblioteca.com", "Administrador", "00000000000");
            repo.salvarUsuario(admin);
            System.out.println("-> Usuário 'Admin' padrão criado.");
        }
    }

    public static void main(String[] args) {
        System.out.println("--- BEM-VINDO AO SISTEMA DA BIBLIOTECA ---");
        Optional<Usuario> usuarioLogadoOpt = autenticarUsuario();
        usuarioLogadoOpt.ifPresentOrElse(
                Main::iniciarSessaoPrincipal,
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
            if (usuarioLogado instanceof Funcionario) {
                rodando = executarAcaoFuncionario(opcao);
            } else if (usuarioLogado instanceof Aluno) {
                rodando = executarAcaoAluno(opcao, usuarioLogado);
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
            case 0 -> { return false; }
            default -> System.out.println("Opção inválida.");
        }
        return true;
    }

    private static boolean executarAcaoAluno(int opcao, Usuario usuarioLogado) {
        switch (opcao) {
            case 1 -> listarTodosLivros();
            case 2 -> buscarLivroInterativo();
            case 13 -> verMeuHistorico(usuarioLogado);
            case 0 -> { return false; }
            default -> System.out.println("Opção inválida.");
        }
        return true;
    }

    private static void exibirMenu(Usuario usuarioLogado) {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("Usuário: " + usuarioLogado.getNome() + " | Tipo: " + usuarioLogado.getTipo());
        System.out.println("--------------------");
        System.out.println("1. Listar todos os livros");
        System.out.println("2. Busca dinâmica de livros");

        if (usuarioLogado instanceof Funcionario) {
            System.out.println("\n--- Gestão (Funcionário) ---");
            System.out.println("3. Adicionar/Atualizar estoque de livro");
            System.out.println("5. Listar todos os usuários");
            System.out.println("6. Cadastrar novo usuário");
            System.out.println("7. Busca dinâmica de usuários");
            System.out.println("8. Remover um usuário");
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

    private static int lerOpcao() {
        try { return Integer.parseInt(scanner.nextLine()); }
        catch (NumberFormatException e) { return -1; }
    }

    private static void cadastrarNovoUsuario() {
        System.out.println("\n--- Cadastrar Novo Usuário ---");
        try {
            System.out.print("Tipo (1 para Aluno, 2 para Funcionário): "); int tipo = Integer.parseInt(scanner.nextLine());
            System.out.print("Nome: "); String nome = scanner.nextLine();
            System.out.print("Email: "); String email = scanner.nextLine();
            System.out.print("CPF (será a senha): "); String cpf = scanner.nextLine();

            Usuario novoUsuario;
            if (tipo == 1) {
                // Construtor corrigido: id, nome, email, matricula (null), cpf
                novoUsuario = new Aluno(0, nome, email, null, cpf);
            } else if (tipo == 2) {
                System.out.print("Cargo: "); String cargo = scanner.nextLine();
                // Construtor corrigido: id, nome, email, cargo, cpf
                novoUsuario = new Funcionario(0, nome, email, cargo, cpf);
            } else {
                System.out.println("Tipo inválido."); return;
            }
            usuarioService.cadastrarUsuario(novoUsuario);
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar usuário: " + e.getMessage());
        }
    }

    private static void removerUsuario() {
        System.out.println("\n--- Remover Usuário ---");
        try {
            System.out.print("Digite o CPF do usuário a ser removido: ");
            String cpf = scanner.nextLine();
            if (cpf.isBlank()) {
                System.out.println("CPF não pode ser vazio.");
                return;
            }
            Optional<Usuario> usuarioParaRemoverOpt = usuarioService.buscarUsuarioPorCpf(cpf);
            if (usuarioParaRemoverOpt.isEmpty()) {
                System.out.println("Nenhum usuário encontrado com o CPF informado.");
                return;
            }
            final Usuario usuarioParaRemover = usuarioParaRemoverOpt.get();
            System.out.println("Usuário Encontrado: " + usuarioParaRemover.getNome() + " (" + usuarioParaRemover.getEmail() + ")");
            System.out.print("Deseja realmente remover este usuário? (S/N): ");
            if (scanner.nextLine().equalsIgnoreCase("S")) {
                usuarioService.removerUsuarioPorCpf(cpf);
                System.out.println("-> Usuário removido com sucesso!");
            } else {
                System.out.println("Remoção cancelada.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao remover usuário: " + e.getMessage());
        }
    }

    private static void realizarNovoEmprestimo() {
        System.out.println("\n--- Realizar Novo Empréstimo ---");
        try {
            System.out.print("Digite o CPF do usuário: ");
            String cpfUsuario = scanner.nextLine();
            System.out.print("Digite o ISBN do livro: ");
            String livroIsbn = scanner.nextLine();
            emprestimoService.realizarEmprestimo(cpfUsuario, livroIsbn);
        } catch (Exception e) {
            System.err.println("Não foi possível realizar o empréstimo: " + e.getMessage());
        }
    }

    private static void registrarDevolucao() {
        System.out.println("\n--- Registrar Devolução ---");
        try {
            System.out.print("Digite o CPF do usuário que está devolvendo: ");
            String cpfUsuario = scanner.nextLine();
            System.out.print("Digite o ISBN do livro a ser devolvido: ");
            String livroIsbn = scanner.nextLine();
            emprestimoService.devolverEmprestimo(cpfUsuario, livroIsbn);
        } catch (Exception e) {
            System.err.println("Não foi possível registrar a devolução: " + e.getMessage());
        }
    }

    private static void verHistoricoDeUsuario() {
        System.out.println("\n--- Histórico de Empréstimos por Usuário ---");
        try {
            System.out.print("Digite o CPF do usuário para ver seu histórico: ");
            String cpf = scanner.nextLine();
            listarHistorico(cpf);
        } catch (Exception e) {
            System.err.println("Erro ao buscar histórico: " + e.getMessage());
        }
    }

    private static void verMeuHistorico(Usuario usuarioLogado) {
        System.out.println("\n--- Meu Histórico de Empréstimos ---");
        listarHistorico(usuarioLogado.getCPF());
    }

    private static void listarHistorico(String cpf) {
        List<Emprestimo> historico = emprestimoService.listarHistoricoPorUsuario(cpf);
        if (historico.isEmpty()) {
            System.out.println("Este usuário não possui empréstimos.");
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            historico.forEach(e -> {
                String dataLabel;
                if (e.getStatus() == StatusEmprestimo.DEVOLVIDO && e.getDataDevolucaoReal() != null) {
                    dataLabel = "Devolvido em: " + e.getDataDevolucaoReal().format(formatter);
                } else {
                    dataLabel = "Emprestado em: " + e.getDataEmprestimo().format(formatter);
                }
                System.out.println("  - Livro ISBN: " + e.getLivroIsbn() + " | Status: " + e.getStatus() + " | " + dataLabel);
            });
        }
    }

    // Métodos que não precisaram de alteração na lógica final
    private static void listarTodosLivros() {
        System.out.println("\n--- Lista de Todos os Livros ---");
        List<Livro> livros = livroService.listarTodosLivros();
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro cadastrado.");
        } else {
            livros.forEach(livro -> System.out.println("  - " + livro.getTitulo() + " (ISBN: " + livro.getIsbn() + ", Disponíveis: " + livro.getQuantidadeDisponivel() + ")"));
        }
    }

    private static void buscarLivroInterativo() {
        System.out.println("\n--- Busca Dinâmica de Livros ---");
        System.out.print("Buscar por: ");
        String termo = scanner.nextLine();
        if (termo.isBlank()) return;
        List<Livro> livrosEncontrados = livroService.buscarLivrosDinamicamente(termo);
        if (livrosEncontrados.isEmpty()) {
            System.out.println("Nenhum livro encontrado para o termo '" + termo + "'.");
        } else {
            livrosEncontrados.forEach(livro -> System.out.println("  - Título: " + livro.getTitulo() + " | Autor: " + livro.getAutor() + " | ISBN: " + livro.getIsbn()));
        }
    }

    private static void adicionarOuAtualizarLivro() {
        System.out.println("\n--- Adicionar/Atualizar Livro ---");
        try {
            System.out.print("Título: "); String titulo = scanner.nextLine();
            System.out.print("Autor: "); String autor = scanner.nextLine();
            System.out.print("Ano de Publicação: "); int ano = Integer.parseInt(scanner.nextLine());
            System.out.print("Quantidade de cópias a adicionar: "); int qtd = Integer.parseInt(scanner.nextLine());
            Livro livroInfo = new Livro(null, titulo, autor, ano, qtd, qtd);
            livroService.cadastrarOuAdicionarEstoque(livroInfo);
        } catch (Exception e) {
            System.err.println("Erro na operação: " + e.getMessage());
        }
    }

    private static void listarTodosUsuarios() {
        System.out.println("\n--- Lista de Todos os Usuários ---");
        List<Usuario> usuarios = usuarioService.listarTodosUsuarios();
        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
        } else {
            usuarios.forEach(usuario -> {
                System.out.print("  - ID: " + usuario.getId() + " | [" + usuario.getTipo() + "] " + usuario.getNome() + " (" + usuario.getEmail() + ")");
                if (usuario instanceof Aluno) System.out.print(" | Matrícula: " + ((Aluno) usuario).getMatricula());
                if (usuario instanceof Funcionario) System.out.print(" | Cargo: " + ((Funcionario) usuario).getCargo());
                System.out.println();
            });
        }
    }

    private static void buscarUsuarioInterativo() {
        System.out.println("\n--- Busca Dinâmica de Usuários ---");
        System.out.print("Buscar por (nome, email, cpf ou matrícula): ");
        String termo = scanner.nextLine();
        if (termo.isBlank()) return;
        List<Usuario> usuariosEncontrados = usuarioService.buscarUsuariosDinamicamente(termo, null);
        if (usuariosEncontrados.isEmpty()) {
            System.out.println("Nenhum usuário encontrado.");
        } else {
            // Apenas lista os resultados da busca
            usuariosEncontrados.forEach(usuario -> {
                System.out.print("  - ID: " + usuario.getId() + " | [" + usuario.getTipo() + "] " + usuario.getNome() + " (" + usuario.getEmail() + ")");
                if (usuario instanceof Aluno) System.out.print(" | Matrícula: " + ((Aluno) usuario).getMatricula());
                if (usuario instanceof Funcionario) System.out.print(" | Cargo: " + ((Funcionario) usuario).getCargo());
                System.out.println();
            });
        }
    }

    private static void listarEmprestimosAtrasados() {
        System.out.println("\n--- Empréstimos Atrasados ---");
        List<Emprestimo> atrasados = emprestimoService.listarEmprestimosAtrasados();
        if (atrasados.isEmpty()) {
            System.out.println("Não há empréstimos atrasados no momento.");
        } else {
            atrasados.forEach(e -> System.out.println("  - ID Emp.: " + e.getId() + " | Usuário ID: " + e.getUsuarioId() + " | Livro ISBN: " + e.getLivroIsbn() + " | Previsto para: " + e.getDataPrevistaDevolucao()));
        }
    }
}