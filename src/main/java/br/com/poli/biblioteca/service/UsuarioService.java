package br.com.poli.biblioteca.service;

import br.com.poli.biblioteca.model.Aluno;
import br.com.poli.biblioteca.model.Usuario;
import br.com.poli.biblioteca.repository.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        if (usuario.getNome() == null || usuario.getNome().isBlank() || usuario.getEmail() == null || usuario.getCPF() == null || usuario.getCPF().isBlank() || usuario.getCPF().length() < 11 || usuario.getCPF().length() > 11  || !usuario.getEmail().contains("@")) {
            throw new IllegalArgumentException("Nome, Email e CPF são obrigatórios e o email deve ser válido.");
        }
        if (this.usuarioRepository.buscarPorEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalStateException("O email '" + usuario.getEmail() + "' já está em uso.");
        }
        if (this.usuarioRepository.buscarPorCPF(usuario.getCPF()).isPresent()) {
            throw new IllegalStateException("O CPF '" + usuario.getCPF() + "' já está em uso.");
        }
        
        if (usuario instanceof Aluno) {
            final String matriculaUnica = gerarMatriculaUnica();
            final Aluno alunoComMatricula = new Aluno(0, usuario.getNome(), usuario.getEmail(), usuario.getCPF(), matriculaUnica);
            this.usuarioRepository.salvarUsuario(alunoComMatricula);
            System.out.println("-> Aluno cadastrado com matrícula gerada: " + matriculaUnica);
        } else {
            this.usuarioRepository.salvarUsuario(usuario);
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

    public void removerUsuario(int id) {
        this.usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalStateException("Usuário com ID " + id + " não encontrado."));
        this.usuarioRepository.removerPorId(id);
    }
}