package ua.svyry.ewallet.configuration.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ua.svyry.ewallet.service.CustomerService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity {
    private final CustomerService customerService;
    private final BCryptPasswordEncoder passwordEncoder;
    @Value("${token.secret}")
    private String jwtSecret;
    @Value("server.servlet.context-path")
    private String pathPrefix;

    @Bean
    protected SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {

//        AuthenticationManagerBuilder managerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
//        managerBuilder.userDetailsService(userService).passwordEncoder(passwordEncoder);
//
//        AuthenticationManager authenticationManager = managerBuilder.build();

        return httpSecurity.csrf().disable().authorizeHttpRequests()
                .requestMatchers(HttpMethod.POST,  "/customers").permitAll()
                .requestMatchers(HttpMethod.POST,  "/authenticate").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(new AuthenticationFilter(authenticationManager(httpSecurity), customerService, jwtSecret))
                .addFilterBefore(new AuthorizationHeaderFilter(authenticationManager(httpSecurity), customerService, jwtSecret), UsernamePasswordAuthenticationFilter.class)
                .authenticationManager(authenticationManager(httpSecurity))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity)
            throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        return authenticationManagerBuilder
                .userDetailsService(customerService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }

}
