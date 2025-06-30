package br.com.poli.biblioteca.service;

import br.com.poli.biblioteca.model.Usuario;
import br.com.poli.biblioteca.repository.UsuarioRepository;

import java.util.Optional;

public class LoginService {

    private final UsuarioRepository usuarioRepository;

    public LoginService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Usuario> autenticar(String loginIdentifier, String senha) {
        if (loginIdentifier == null || loginIdentifier.isBlank() || senha == null || senha.isBlank()) {
            return Optional.empty();
        }

        Optional<Usuario> usuarioOptional = usuarioRepository.buscarPorEmail(loginIdentifier);
        if (usuarioOptional.isEmpty()) {
            usuarioOptional = usuarioRepository.buscarPorCPF(loginIdentifier);
        }

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            if (usuario.getCPF().equals(senha)) {
                return usuarioOptional;
            }
        }

        return Optional.empty();
    }
}