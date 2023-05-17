package fr.miage.MIAGELand.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    private final Environment environment;

    public WebSecurityConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )
                .cors().disable() // Disable the default CORS configuration
                .csrf().disable(); // Disable CSRF protection for simplicity
        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                if (Arrays.asList(environment.getActiveProfiles()).contains("dev")) {
                    registry.addMapping("/api/**")
                            .allowedOrigins("http://127.0.0.1:5173")
                            .allowedMethods("*")
                            .allowCredentials(true); // Allow credentials (cookies) in the CORS response
                }
            }
        };
    }
}
