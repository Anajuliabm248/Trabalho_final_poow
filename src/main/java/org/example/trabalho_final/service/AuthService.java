package org.example.trabalho_final.service;

import org.example.trabalho_final.model.Usuario;
import org.example.trabalho_final.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario login(String email, String senha) {

        Usuario usuario = usuarioRepository.buscarPorEmail(email);

        if (usuario == null || !usuario.isAtivo()) {
            return null;
        }

        if (!usuario.getSenha().equals(senha)) {
            return null;
        }

        return usuario;
    }

    public boolean emailJaCadastrado(String email) {
        return usuarioRepository.existeEmail(email);
    }

    public boolean cpfJaCadastrado(String cpf) {
        return usuarioRepository.existeCpf(cpf);
    }
}