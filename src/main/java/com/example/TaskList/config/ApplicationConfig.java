package com.example.TaskList.config;

import com.example.TaskList.web.Security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class ApplicationConfig {

    private final JwtTokenProvider jwtTokenProvider;

    private final ApplicationContext applicationContext;

    // Создание бина для шифрования паролей с использованием BCryptPasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Создание бина для управления аутентификацией
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // Создание SecurityFilterChain для настройки безопасности HTTP
    @Bean
    public SecurityFilterChain filterChain (HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // Отключение защиты CSRF
                .csrf().disable()
                // Настройка CORS  (Cross-Origin Resource Sharing) для безопасности при работе с разными источниками данных
                .cors()
                .and()
                // Отключение HTTP Basic аутентификации
                .httpBasic().disable()
                // Настройка управления сессиями
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // Обработка исключений
                .exceptionHandling()
                // Установка обработчика начальной аутентификации
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.getWriter().write("Unauthorized.");
                })
                // Установка обработчика доступа запрещен
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.getWriter().write("Unauthorized.");
                })
                .and()
                // Настройка правил авторизации запросов
                .authorizeHttpRequests()
                // Разрешение доступа к определенному пути без аутентификации
                .requestMatchers("/api/v1/auth/**").permitAll()
                // Остальные запросы требуют аутентификации
                .anyRequest().authenticated()
                .and()
                // Отключение анонимной аутентификации
                .anonymous().disable()
                // Добавление JwtTokenFilter перед фильтром аутентификации
                .addFilterBefore(new JwtTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    //альтернативная от чата
/*
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint((request, response, authException) -> {
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        response.getWriter().write("Unauthorized.");
                    });
                    exception.accessDeniedHandler((request, response, accessDeniedException) -> {
                        response.setStatus(HttpStatus.FORBIDDEN.value());
                        response.getWriter().write("Unauthorized.");
                    });
                })
                .authorizeRequests(requests -> {
                    requests
                            .antMatchers("/api/v1/auth/**").permitAll()
                            .anyRequest().authenticated();
                })
                .anonymous(AbstractHttpConfigurer::disable)
                .addFilterBefore(new JwtTokenFilter(jwtTokenProvider), JwtTokenFilter.class);
        return httpSecurity.build();
    }

 */
}
