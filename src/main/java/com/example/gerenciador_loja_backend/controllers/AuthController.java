package com.example.gerenciador_loja_backend.controllers;

import com.example.gerenciador_loja_backend.dtos.LoginDto;
import com.example.gerenciador_loja_backend.dtos.LoginResponseDto;
import com.example.gerenciador_loja_backend.dtos.RegisterRequestDto;
import com.example.gerenciador_loja_backend.models.Usuario;
import com.example.gerenciador_loja_backend.repositories.UsuarioRepository;
import com.example.gerenciador_loja_backend.security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginDto loginDto) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(loginDto.username());

        if (usuarioOpt.isEmpty() || !passwordEncoder.matches(loginDto.password(), usuarioOpt.get().getPassword())) {
            return ResponseEntity.status(401).body("Credenciais inválidas");
        }

        Usuario usuario = usuarioOpt.get();
        String token = this.tokenService.generateToken(usuario);

        return ResponseEntity.ok(new LoginResponseDto(usuario.getUsername(), token));
    }


    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequestDto registerRequestDto){

        Optional<Usuario> usuario = usuarioRepository.findByUsername(registerRequestDto.username());
        if (usuario.isPresent()) {
            return ResponseEntity.badRequest().body("Usuário já existe");
        }

        Usuario newUser = new Usuario();
        newUser.setUsername(registerRequestDto.username());
        newUser.setPassword(passwordEncoder.encode(registerRequestDto.password()));

        usuarioRepository.save(newUser);

        // Gera o token JWT
        String token = tokenService.generateToken(newUser);

        // Retorna o token, NÃO a senha criptografada!
        return ResponseEntity.ok(new LoginResponseDto(newUser.getUsername(), token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        // Se usar blacklist de token, invalidar aqui
        return ResponseEntity.ok().build();
    }

}
