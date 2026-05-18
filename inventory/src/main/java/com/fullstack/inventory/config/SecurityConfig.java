package com.fullstack.inventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Desactivamos CSRF porque esta API será consumida por el API Gateway / frontend,
                // no por formularios HTML tradicionales.
                .csrf(csrf -> csrf.disable())

                // Permitimos todas las rutas dentro de inventory-ms.
                // La validación de JWT y roles se hará en el API Gateway.
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )

                // Desactivamos el formulario de login automático de Spring Security.
                .formLogin(form -> form.disable())

                // Desactivamos Basic Auth para que no pida usuario/contraseña en navegador o Thunder.
                .httpBasic(basic -> basic.disable())

                // Desactivamos logout porque este microservicio no maneja sesión.
                .logout(logout -> logout.disable());

        return http.build();
    }
}