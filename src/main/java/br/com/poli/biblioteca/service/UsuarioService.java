package br.com.poli.biblioteca.service;

import br.com.poli.biblioteca.model.Aluno;
import br.com.poli.biblioteca.model.Usuario;
import br.com.poli.biblioteca.repository.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Optional;

public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    private String gerarMatriculaRecursivamente(Random random, int digitosRestantes, StringBuilder matriculaAtual) {
        if (digitosRestantes == 0) return matriculaAtual.toString();
        matriculaAtual.append(random.nextInt(10));
        return gerarMatriculaRecursivamente(random, digitosRestantes - 1, matriculaAtual);
    }

    private String gerarMatriculaUnica() {
        final String matriculaCandidata = gerarMatriculaRecursivamente(new Random(), 8, new StringBuilder());
        final boolean existe = this.usuarioRepository.buscarPorMatricula(matriculaCandidata).isPresent();
        return existe ? gerarMatriculaUnica() : matriculaCandidata;
    }

    public void cadastrarUsuario(Usuario usuario) {
        // Validação 1: Campos obrigatórios e formato do email
        if (usuario.getNome() == null || usuario.getNome().isBlank() ||
                usuario.getEmail() == null || !usuario.getEmail().contains("@")) {
            throw new IllegalArgumentException("Nome e Email são obrigatórios e o email deve ser válido.");
        }

        // Validação 2: Formato do CPF
        if (usuario.getCPF() == null || usuario.getCPF().isBlank() || usuario.getCPF().length() == 10) {
            throw new IllegalArgumentException("CPF é obrigatório e deve conter 11 dígitos.");
        }

        // Validação 3: Duplicidade de Email
        if (this.usuarioRepository.buscarPorEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalStateException("O email '" + usuario.getEmail() + "' já está em uso.");
        }

        // Validação 4: Duplicidade de CPF
        if (this.usuarioRepository.buscarPorCPF(usuario.getCPF()).isPresent()) {
            throw new IllegalStateException("O CPF '" + usuario.getCPF() + "' já está em uso.");
        }

        // Se todas as validações passaram, procede com a lógica de cadastro
        if (usuario instanceof Aluno) {
            final String matriculaUnica = gerarMatriculaUnica();
            // Corrigindo a ordem dos parâmetros no construtor
            final Aluno alunoComMatricula = new Aluno(0, usuario.getNome(), usuario.getEmail(), matriculaUnica, usuario.getCPF());
            this.usuarioRepository.salvarUsuario(alunoComMatricula); // Supondo que o método se chama salvar()
            System.out.println("-> Aluno cadastrado com matrícula gerada: " + matriculaUnica);
        } else {
            this.usuarioRepository.salvarUsuario(usuario); // Supondo que o método se chama salvar()
            System.out.println("-> Funcionário cadastrado com sucesso!");
        }
    }

    public List<Usuario> listarTodosUsuarios() {
        return this.usuarioRepository.buscarTodos();
    }

    public List<Usuario> buscarUsuariosDinamicamente(String termoDeBusca, String tipoUsuario) {
        if (termoDeBusca == null || termoDeBusca.isBlank()) {
            return new ArrayList<>();
        }
        return this.usuarioRepository.buscarPorTermo(termoDeBusca, tipoUsuario);
    }

    public Optional<Usuario> buscarUsuarioPorCpf(String cpf) {
        return this.usuarioRepository.buscarPorCPF(cpf);
    }

    public void removerUsuarioPorCpf(String cpf) {
        // Garante que o usuário existe antes de tentar remover
        usuarioRepository.buscarPorCPF(cpf)
                .orElseThrow(() -> new IllegalStateException("Usuário com CPF " + cpf + " não encontrado."));
        // Chama o novo método do repositório
        this.usuarioRepository.removerPorCPF(cpf);
    }
}