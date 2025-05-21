package utcapitole.miage.projetdevg3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import utcapitole.miage.projetdevg3.service.CustomUserDetailsService;
import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http
                                // Autoriser globalement toutes les requêtes (aucune authentification requise
                                // par défaut)
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/api/utilisateurs/login",
                                                                "/api/utilisateurs/verifierlogin",
                                                                "/css/**", "/js/**")
                                                .permitAll()
                                                .requestMatchers("/groupes/**", "/demandes/**").authenticated()
                                                .anyRequest().permitAll())
                                // login
                                .formLogin(form -> form
                                                .loginPage("/login") // Page de login personnalisée
                                                .loginProcessingUrl("/api/utilisateurs/verifierlogin") // URL de
                                                                                                       // traitement par
                                                                                                       // Spring
                                                .defaultSuccessUrl("/accueil", true) // Redirection après succès
                                                .failureUrl("/api/utilisateurs/login?error") // Redirection en cas
                                                                                             // d'erreur
                                                .usernameParameter("username")

                                                .passwordParameter("password")
                                                .permitAll())
                                // logout
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/accueil")
                                                .permitAll());

                return http.build();
        }

        @Bean
        public UserDetailsService userDetailsService(UtilisateurRepository utilisateurRepository) {
                return new CustomUserDetailsService(utilisateurRepository);
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(HttpSecurity http, UserDetailsService userDetailsService)
                        throws Exception {
                return http.getSharedObject(AuthenticationManagerBuilder.class)
                                .userDetailsService(userDetailsService)
                                .passwordEncoder(passwordEncoder())
                                .and()
                                .build();
        }
}