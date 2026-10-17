package br.com.fiap.mercado.config;

import br.com.fiap.mercado.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UsuarioRepository usuarioRepository) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // rotas publicas: landing page, login, cadastro e arquivos estaticos
                .requestMatchers("/", "/index", "/login", "/cadastro", "/css/**", "/img/**", "/error").permitAll()
                // rotas restritas ao ADMIN (gerente do mercado)
                .requestMatchers("/admin/**", "/produtos/novo", "/produtos/salvar",
                                 "/produtos/*/editar", "/produtos/*/excluir").hasRole("ADMIN")
                // demais rotas: qualquer usuario logado (ADMIN ou CLIENTE)
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")                 // tela de login propria (nao a padrao do Spring)
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("senha")
                .successHandler(redirecionarPorPerfil())
                .failureHandler(falhaNoLogin(usuarioRepository))
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .exceptionHandling(ex -> ex.accessDeniedPage("/acesso-negado"));
        return http.build();
    }

    /** Apos o login: ADMIN vai para o painel administrativo, CLIENTE vai para o catalogo. */
    private AuthenticationSuccessHandler redirecionarPorPerfil() {
        return (request, response, authentication) -> {
            boolean admin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            response.sendRedirect(request.getContextPath() + (admin ? "/admin" : "/produtos"));
        };
    }

    /** Se o e-mail nao possui cadastro, redireciona para a tela de Sign Up; senao, senha incorreta. */
    private AuthenticationFailureHandler falhaNoLogin(UsuarioRepository usuarioRepository) {
        return (request, response, exception) -> {
            String email = request.getParameter("email");
            if (email != null && !email.isBlank() && !usuarioRepository.existsByEmailIgnoreCase(email.trim())) {
                response.sendRedirect(request.getContextPath() + "/cadastro?semConta&email="
                        + URLEncoder.encode(email.trim(), StandardCharsets.UTF_8));
            } else {
                response.sendRedirect(request.getContextPath() + "/login?error");
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
