package br.com.fiap.mercado.service;

import br.com.fiap.mercado.model.Role;
import br.com.fiap.mercado.model.Usuario;
import br.com.fiap.mercado.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario cadastrar(String nome, String email, String senha, Role role) {
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Ja existe um usuario com este e-mail");
        }
        return repository.save(Usuario.builder()
                .nome(nome.trim())
                .email(email.trim().toLowerCase())
                .senha(passwordEncoder.encode(senha))
                .role(role)
                .dataCadastro(LocalDateTime.now())
                .build());
    }
}
