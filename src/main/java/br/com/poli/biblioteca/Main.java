package br.com.poli.biblioteca;

import br.com.poli.biblioteca.model.Aluno;
import br.com.poli.biblioteca.model.Funcionario;
import br.com.poli.biblioteca.model.Livro;
import br.com.poli.biblioteca.model.Usuario;
import br.com.poli.biblioteca.repository.DatabaseSetup;
import br.com.poli.biblioteca.repository.LivroRepository;
import br.com.poli.biblioteca.repository.UsuarioRepository;
import br.com.poli.biblioteca.service.LivroService;
import br.com.poli.biblioteca.service.LoginService;
import br.com.poli.biblioteca.service.UsuarioService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final LivroService livroService;
    private static final UsuarioService usuarioService;
    private static final LoginService loginService;

    static {
        DatabaseSetup.criarTabelas();
        UsuarioRepository usuarioRepository = new UsuarioRepository();
        criarUsuariosPadraoSeNaoExistirem(usuarioRepository);
        LivroRepository livroRepository = new LivroRepository();
        livroService = new LivroService(livroRepository);
        usuarioService = new UsuarioService(usuarioRepository);
        loginService = new LoginService(usuarioRepository);
    }

    private static void criarUsuariosPadraoSeNaoExistirem(UsuarioRepository repo) {
        final String adminEmail = "Admin@";
        if (repo.buscarPorEmail(adminEmail).isEmpty()) {
            System.out.println("Usuário mestre 'Admin' não encontrado. Criando...");
            Funcionario admin = new Funcionario(
                    0,
                    "Administrador",
                    adminEmail,
                    "Admin",
                    "Administrador do Sistema"
            );
            repo.salvarUsuario(admin);
            System.out.println("-> Usuário mestre 'Admin' criado com sucesso!");
        }

        final String alunoEmail = "batman@poli.br";
        if (repo.buscarPorEmail(alunoEmail).isEmpty()) {
            System.out.println("Usuário mestre 'Aluno' não encontrado. Criando...");
            Aluno aluno = new Aluno(
                    0,
                    "Evandson",
                    alunoEmail,
                    "12345678910",
                    "202511223"
            );
            repo.salvarUsuario(aluno);
            System.out.println("-> Usuário mestre 'Aluno' criado com sucesso!");
        }
    }

    public static void main(String[] args) {
        System.out.println("--- BEM-VINDO AO SISTEMA DA BIBLIOTECA ---");
        Optional<Usuario> usuarioLogadoOpt = autenticarUsuario();

        usuarioLogadoOpt.ifPresentOrElse(
                usuario -> {
                    System.out.println("\nLogin bem-sucedido! Bem-vindo(a), " + usuario.getNome() + ".");
                    iniciarSessaoPrincipal(usuario);
                },
                () -> System.out.println("\nFalha na autenticação. O sistema será encerrado.")
        );

        System.out.println("\nSistema finalizado.");
        scanner.close();
    }

    private static Optional<Usuario> autenticarUsuario() {
        System.out.print("Login: ");
        String login = scanner.nextLine();
        System.out.print("Senha (seu CPF): ");
        String senha = scanner.nextLine();
        return loginService.autenticar(login, senha);
    }

    private static void iniciarSessaoPrincipal(Usuario usuarioLogado) {
        boolean rodando = true;
        final boolean isFuncionario = usuarioLogado instanceof Funcionario;

        while (rodando) {
            exibirMenu(usuarioLogado);
            int opcao = lerOpcao();

            switch (opcao) {
                case 1:
                    listarTodosLivros();
                    break;
                case 2:
                    buscarLivroInterativo();
                    break;
                case 3:
                    if (isFuncionario) adicionarOuAtualizarLivro();
                    else System.out.println("Opção inválida. Tente novamente.");
                    break;
                case 5:
                    if (isFuncionario) listarTodosUsuarios();
                    else System.out.println("Opção inválida. Tente novamente.");
                    break;
                case 6:
                    if (isFuncionario) cadastrarNovoUsuario();
                    else System.out.println("Opção inválida. Tente novamente.");
                    break;
                case 7:
                    if (isFuncionario) buscarUsuarioInterativo();
                    else System.out.println("Opção inválida. Tente novamente.");
                    break;
                case 8:
                    if (isFuncionario) removerUsuario();
                    else System.out.println("Opção inválida. Tente novamente.");
                    break;
                case 0:
                    rodando = false;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private static void exibirMenu(Usuario usuarioLogado) {
        System.out.println("\n--- SISTEMA DE BIBLIOTECA ---");
        System.out.println("Usuário Logado: " + usuarioLogado.getNome() + " | Tipo: " + usuarioLogado.getTipo());

        if (usuarioLogado instanceof Funcionario) {
            System.out.println("--- Livros ---");
            System.out.println("1. Listar todos os livros");
            System.out.println("2. Busca dinâmica de livros");
            System.out.println("3. Adicionar/Atualizar estoque de livro");
            System.out.println("--- Usuários ---");
            System.out.println("5. Listar todos os usuários");
            System.out.println("6. Cadastrar novo usuário");
            System.out.println("7. Busca dinâmica de usuários");
            System.out.println("8. Remover um usuário");
        }
        else if (usuarioLogado instanceof Aluno) {
            System.out.println("--- Livros ---");
            System.out.println("1. Listar todos os livros");
            System.out.println("2. Busca dinâmica de livros");
        }

        System.out.println("--------------------");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static int lerOpcao() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void listarTodosLivros() {
        System.out.println("\n--- Lista de Todos os Livros ---");
        List<Livro> livros = livroService.listarTodosLivros();
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro cadastrado.");
        } else {
            livros.forEach(livro -> System.out.println("  - " + livro.getTitulo() + " (ISBN: " + livro.getIsbn() + ")"));
        }
    }

    private static void buscarLivroInterativo() {
        System.out.println("\n--- Busca Dinâmica de Livros ---");
        System.out.println("Digite parte do ISBN, título ou autor. Deixe em branco para voltar.");
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
        System.out.println("\n--- Adicionar/Atualizar Livro no Catálogo ---");
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

    private static void cadastrarNovoUsuario() {
        System.out.println("\n--- Cadastrar Novo Usuário ---");
        try {
            System.out.print("Tipo (1 para Aluno, 2 para Funcionário): ");
            int tipo = Integer.parseInt(scanner.nextLine());
            System.out.print("Nome: "); String nome = scanner.nextLine();
            System.out.print("Email: "); String email = scanner.nextLine();
            System.out.print("CPF: "); String cpf = scanner.nextLine();

            Usuario novoUsuario;
            if (tipo == 1) {
                novoUsuario = new Aluno(0, nome, email, cpf, null);
            } else if (tipo == 2) {
                System.out.print("Cargo: "); String cargo = scanner.nextLine();
                novoUsuario = new Funcionario(0, nome, email, cpf, cargo);
            } else {
                System.out.println("Tipo inválido."); return;
            }
            usuarioService.cadastrarUsuario(novoUsuario);
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar usuário: " + e.getMessage());
        }
    }

    private static void buscarUsuarioInterativo() {
        System.out.println("\n--- Busca Dinâmica de Usuários ---");
        System.out.print("Filtrar por tipo? (1=Aluno, 2=Funcionário, Enter=Todos): ");
        String tipoInput = scanner.nextLine();
        String tipoFiltro = null;
        if ("1".equals(tipoInput)) tipoFiltro = "Aluno";
        if ("2".equals(tipoInput)) tipoFiltro = "Funcionario";

        System.out.println("Digite parte do nome, CPF, email ou matrícula. Deixe em branco para voltar.");
        System.out.print("Buscar por: ");
        String termo = scanner.nextLine();
        if (termo.isBlank()) return;

        List<Usuario> usuariosEncontrados = usuarioService.buscarUsuariosDinamicamente(termo, tipoFiltro);
        if (usuariosEncontrados.isEmpty()) {
            System.out.println("Nenhum usuário encontrado.");
        } else {
            System.out.println("--- Resultados ---");
            usuariosEncontrados.forEach(usuario -> {
                System.out.print(" [" + usuario.getTipo() + "] " + usuario.getNome() + " (" + usuario.getEmail() + ")" + " (" + usuario.getCPF() + ")");
                if (usuario instanceof Aluno) System.out.print(" | Matrícula: " + ((Aluno) usuario).getMatricula());
                if (usuario instanceof Funcionario) System.out.print(" | Cargo: " + ((Funcionario) usuario).getCargo());
                System.out.println();
            });
        }
    }

    private static void removerUsuario() {
        System.out.println("\n--- Remover Usuário ---");
        try {
            System.out.println("Primeiro, vamos encontrar o usuário que você deseja remover.");
            System.out.print("Digite o nome, email ou matrícula para buscar: ");
            String termo = scanner.nextLine();

            if (termo.isBlank()) {
                System.out.println("Busca cancelada. Voltando ao menu.");
                return;
            }

            List<Usuario> usuariosEncontrados = usuarioService.buscarUsuariosDinamicamente(termo, null);

            if (usuariosEncontrados.isEmpty()) {
                System.out.println("Nenhum usuário encontrado para o termo '" + termo + "'. Operação cancelada.");

            } else if (usuariosEncontrados.size() == 1) {
                final Usuario usuarioParaRemover = usuariosEncontrados.get(0);
                System.out.println("\n--- Usuário Encontrado ---");
                System.out.println("  -> ID: " + usuarioParaRemover.getId() + " | Nome: " + usuarioParaRemover.getNome() + " | Email: " + usuarioParaRemover.getEmail());

                System.out.print("Deseja remover este usuário? (S/N): ");
                String confirmacao = scanner.nextLine();

                if (confirmacao.equalsIgnoreCase("S")) {
                    usuarioService.removerUsuario(usuarioParaRemover.getId());
                    System.out.println("-> Usuário removido com sucesso!");
                } else {
                    System.out.println("Remoção cancelada pelo operador.");
                }

            } else {
                System.out.println("\n--- Vários usuários encontrados. Especifique qual remover ---");
                usuariosEncontrados.forEach(usuario -> {
                    System.out.print("  -> ID: " + usuario.getId() + " | [" + usuario.getTipo() + "] " + usuario.getNome() + " (" + usuario.getEmail() + ")");
                    if (usuario instanceof Aluno) System.out.print(" | Matrícula: " + ((Aluno) usuario).getMatricula());
                    if (usuario instanceof Funcionario) System.out.print(" | Cargo: " + ((Funcionario) usuario).getCargo());
                    System.out.println();
                });

                System.out.print("\nPor favor, digite o ID exato do usuário que deseja remover para confirmar: ");
                int idParaRemover = Integer.parseInt(scanner.nextLine());
                usuarioService.removerUsuario(idParaRemover);
                System.out.println("-> Usuário com ID " + idParaRemover + " removido com sucesso!");
            }

        } catch (NumberFormatException e) {
            System.err.println("Erro: O ID deve ser um número válido.");
        } catch (Exception e) {
            System.err.println("Erro ao remover usuário: " + e.getMessage());
        }
    }
}