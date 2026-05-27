package dev.genesshoan.cinema_rest_api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user1 = User
                .withUsername("admin")
                .password(this.passwordEncoder().encode("admin"))
                .roles("ADMIN")
                .build();

        UserDetails user2 = User
                .withUsername("user")
                .password(this.passwordEncoder().encode("user"))
                .roles("CUSTOMER")
                .build();

        return new InMemoryUserDetailsManager(user1, user2);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        // Movies

                        .requestMatchers(HttpMethod.POST, "/movies").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/movies/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/movies/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/movies", "/movies/**").permitAll()

                        // Rooms

                        .requestMatchers(HttpMethod.POST, "/rooms").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/rooms/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/rooms/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/rooms/**").hasAnyRole("ADMIN", "STAFF")

                        // Seats

                        .requestMatchers(HttpMethod.GET, "/seats/**").permitAll()

                        // Showtimes

                        .requestMatchers(HttpMethod.POST, "/showtimes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/showtimes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/showtimes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/showtimes/**").permitAll()

                        // Tickets

                        .requestMatchers(HttpMethod.POST, "/tickets").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.PUT, "/tickets/*/cancel").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.PUT, "/tickets/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/tickets/**").hasAnyRole("ADMIN", "STAFF", "CUSTOMER")

                        .anyRequest().authenticated()
                ).httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
