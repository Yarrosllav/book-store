package com.epam.rd.autocode.spring.project.conf;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.net.URLEncoder;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig{

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/books/**", "/login", "/register", "/error").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/process_login")
                        .defaultSuccessUrl("/books", true)
                        .failureHandler((request, response, exception) -> {
                            String error;
                            if (exception instanceof LockedException) {
                                error = "locked";
                            }else{
                                error = "true";
                            }
                            response.sendRedirect("/login?error=" + error);
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        http.headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));

        return http.build();
    }

}
