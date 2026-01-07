package com.example.gerenciador_loja_backend.security;

import com.example.gerenciador_loja_backend.models.Usuario;
import com.example.gerenciador_loja_backend.repositories.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;
    private final UserDetailsService userDetailsService;

    public SecurityFilter(
            TokenService tokenService,
            UsuarioRepository usuarioRepository,
            UserDetailsService userDetailsService
    ) {
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String token = recuperarToken(request);

            if (token != null && tokenService.isTokenValid(token)) {

                String username = tokenService.getUsername(token);

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                Optional<Usuario> usuarioOpt =
                        usuarioRepository.findByUsername(username);

                // ⚠️ NÃO lança exceção no filtro
                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();

                    var authentication =
                            new UsernamePasswordAuthenticationToken(
                                    usuario,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            // 🚫 NUNCA propagar exceção do filtro
            System.out.println("⚠️ Erro no SecurityFilter: " + ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null) return null;
        if (!authHeader.startsWith("Bearer ")) return null;

        return authHeader.substring(7);
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return true;
    }
}
