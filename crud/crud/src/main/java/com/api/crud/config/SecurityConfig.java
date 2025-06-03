package com.api.crud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer; // Importar para Customizer
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilita CSRF (común en APIs REST)
                .cors(Customizer.withDefaults()) // Asegura que tu CorsConfig se aplique

                .authorizeHttpRequests(auth -> auth
                        // Permite acceso sin autenticación a /api/auth/login
                        .requestMatchers("/api/auth/login").permitAll()
                        // Permite acceso sin autenticación a /api/usuarios (si es para registro)
                        .requestMatchers("/api/usuarios/**").permitAll()

                        // =========================================================================================
                        // !!! IMPORTANTE: Permite acceso sin autenticación a /api/compras para propósitos temporales !!!
                        // =========================================================================================
                        .requestMatchers("/api/compras/**").permitAll() // <<--- ¡LÍNEA CLAVE AÑADIDA/MODIFICADA!
                        .requestMatchers("api/productos/**").permitAll()
                        .requestMatchers("/api/proveedores/**").permitAll()
                        .requestMatchers("api/ventas/**").permitAll()
                        .requestMatchers("/api/roles/**").permitAll()
                        .requestMatchers("/api/clientes/**").permitAll()
                        .requestMatchers("/api/dashboard/**").permitAll()

                        // TODAS LAS DEMÁS RUTAS /api/** DEBEN ESTAR AUTENTICADAS (excepto las anteriores)
                        .requestMatchers("/api/**").authenticated()

                        // Cualquier otra solicitud que no sea /api/ también requiere autenticación
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults()) // Habilita autenticación básica HTTP

        // Si vas a usar JWT, quita el comentario de la siguiente línea y asegúrate de no usar sesiones:
        // .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        ;

        return http.build();
    }
}