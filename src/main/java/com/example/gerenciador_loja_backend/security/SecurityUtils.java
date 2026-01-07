package com.example.gerenciador_loja_backend.security;

import com.example.gerenciador_loja_backend.models.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

public class SecurityUtils {

    private SecurityUtils() {
        // impede instanciação
    }

    public static UUID getUsuarioLogadoId() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado"
            );
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof Usuario usuario)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário inválido"
            );
        }

        return usuario.getId();
    }
}
