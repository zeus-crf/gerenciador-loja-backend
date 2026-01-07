package com.example.gerenciador_loja_backend.config;

import com.example.gerenciador_loja_backend.models.Usuario;
import com.example.gerenciador_loja_backend.repositories.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UsuarioRepository usuarioRepository,
                      PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setUsername("Miguel");
            admin.setPassword(passwordEncoder.encode("2007"));

            usuarioRepository.save(admin);

            System.out.println("✅ Usuário admin criado com sucesso!");
            System.out.println("👤 login: admin");
            System.out.println("🔑 senha: admin123");
        }
    }
}
