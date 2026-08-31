package com.sistemapdv.backend.config;

import com.sistemapdv.backend.security.CustomUserDetailsService;
import com.sistemapdv.backend.security.JwtAuthenticationEntryPoint;
import com.sistemapdv.backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;

/**
 * Configuración de seguridad con JWT para la aplicación.
 * 
 * Esta configuración:
 * - Habilita autenticación basada en JWT
 * - Permite endpoints públicos para login
 * - Protege otros endpoints requiriendo token válido
 * - Usa sesiones stateless (sin sesiones en servidor)
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    /**
     * Encriptador de contraseñas usando BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Proveedor de autenticación DAO
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Configura la cadena de filtros de seguridad HTTP
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    // Endpoint público
                    .requestMatchers("/api/auth/login").permitAll()
                    .requestMatchers("/api/usuarios/**").permitAll()
                    .requestMatchers("/api/categorias/**").permitAll()
                    .requestMatchers("/api/productos/**").permitAll()
                    .requestMatchers("/api/variantes/**").permitAll()
                    .requestMatchers("/api/canales-venta/**").permitAll()
                    .requestMatchers("/api/stock/**").permitAll()
                    .requestMatchers("/api/productos-canales/**").permitAll()
                    .requestMatchers("/api/email/**").permitAll()
                    // Toda request que no haya sido permitida, requiere un usuario autenticado
                .anyRequest().authenticated()
            )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint))
                    // No guardar sesiones, cada request debe traer nuevamente el JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    // Encargado de autenticar usuarios
                .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }
}
