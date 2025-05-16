package utcapitole.miage.projetDevG3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) 
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Autoriser globalement toutes les requêtes (aucune authentification requise par défaut)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            // login
            .formLogin(form -> form
                .loginPage("/login")          // Page de login personnalisée
                .loginProcessingUrl("/login") // URL de traitement par Spring
                .defaultSuccessUrl("/index")  // Redirection après succès
                .failureUrl("/login?error")   // Redirection en cas d'erreur
                .permitAll()
            )
            // logout
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}