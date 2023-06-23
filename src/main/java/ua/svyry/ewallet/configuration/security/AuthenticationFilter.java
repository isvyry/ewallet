package ua.svyry.ewallet.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ua.svyry.ewallet.service.CustomerService;
import ua.svyry.ewallet.shared.CustomerDto;
import ua.svyry.ewallet.ui.model.LoginRequestModel;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final CustomerService customerService;

    private final String jwtSecret;

    public AuthenticationFilter(AuthenticationManager authenticationManager, CustomerService customerService,
                                String jwtSecret) {
        super(authenticationManager);
        this.jwtSecret = jwtSecret;
        this.customerService = customerService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        try {
            LoginRequestModel loginRequestModel = new ObjectMapper()
                    .readValue(request.getInputStream(), LoginRequestModel.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestModel.getUsername(),
                            loginRequestModel.getPassword(), new ArrayList<>()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        String username = ((User) authResult.getPrincipal()).getUsername();
        CustomerDto customer = customerService.getUserDetailsByUsername(username);
        Instant now = Instant.now();
        SecretKey secretKey = new SecretKeySpec(Base64.getEncoder().encode(jwtSecret.getBytes()),
                SignatureAlgorithm.HS512.getJcaName());

        String jwt = Jwts.builder()
                .setSubject(customer.getUserId().toString())
                .setExpiration(Date.from(now.plusMillis(7200000)))
                .setIssuedAt(Date.from(now))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();

        response.addHeader("token", jwt);
        response.addHeader("userId", customer.getUserId().toString());
    }
}
