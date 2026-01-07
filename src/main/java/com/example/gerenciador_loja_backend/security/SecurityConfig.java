package com.example.gerenciador_loja_backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    public SecurityConfig(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        System.out.println("🛡️ SecurityConfig carregado");

        http
                // ❌ CSRF desativado (API stateless)
                .csrf(csrf -> csrf.disable())

                // ✅ CORS habilitado (usa seu CorsConfig)
                .cors(cors -> {})

                // ✅ API sem sessão
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // ✅ DIFERENCIAR 401 de 403
                .exceptionHandling(ex -> ex
                        // Token inválido / ausente → 401
                        .authenticationEntryPoint(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                        )
                        // Regra de negócio / permissão → 403
                        .accessDeniedHandler(accessDeniedHandler())
                )

                // ✅ REGRAS DE ACESSO
                .authorizeHttpRequests(auth -> auth

                        // 🔓 ROTAS PÚBLICAS
                        .requestMatchers(HttpMethod.POST,
                                "/auth/login",
                                "/auth/register"
                        ).permitAll()

                        // 🔐 ROTAS PROTEGIDAS (exigem token)
                        .requestMatchers("/clientes/**").authenticated()
                        .requestMatchers("/pedidos/**").authenticated()
                        .requestMatchers("/usuarios/**").authenticated()

                        // 🔐 QUALQUER OUTRA
                        .anyRequest().authenticated()
                )

                // ✅ FILTRO JWT ANTES DO SPRING
                .addFilterBefore(
                        securityFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * ⚠️ IMPORTANTE:
     * Este handler é acionado quando o usuário
     * ESTÁ autenticado, mas NÃO TEM permissão
     * (ex: tentar se deletar)
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) -> {
            System.out.println("🚫 AccessDeniedHandler acionado");
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"error\": \"Você não tem permissão para executar esta ação\"}"
            );
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}
