package ua.svyry.ewallet.configuration.security;

import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.server.ServerWebExchange;
import ua.svyry.ewallet.service.CustomerService;
import ua.svyry.ewallet.shared.CustomerDto;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

public class AuthorizationHeaderFilter extends BasicAuthenticationFilter {

    @Value("${token.secret}")
    private String secret;
    private final CustomerService customerService;
    private static final String HEADER_STRING = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    public AuthorizationHeaderFilter(AuthenticationManager authenticationManager,
                                     CustomerService customerService, String secret) {
        super(authenticationManager);
        this.secret = secret;
        this.customerService = customerService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(HEADER_STRING);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }


    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        CustomerDto parsedCustomer = getCustomer(token);
        if (parsedCustomer != null) {
            return new UsernamePasswordAuthenticationToken(parsedCustomer.getEmail(), null, new ArrayList<>());
        }

        return null;
    }
    private CustomerDto getCustomer(String jwt) {
        SecretKey secretKey = new SecretKeySpec(Base64.getEncoder().encode(secret.getBytes()),
                SignatureAlgorithm.HS512.getJcaName());

        JwtParser jwtParser = Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build();

        Jwt<Header, Claims> parsedToken = jwtParser.parse(jwt.replace("Bearer ", ""));
        var userId = parsedToken.getBody().getSubject();
        Date now = Date.from(Instant.now());
        var date = parsedToken.getBody().getExpiration();
        if (userId == null || userId.isEmpty() || date.before(now)) {
            logger.error("JWT is not valid!");
            return null;
        }

        return customerService.getCustomerDetailsById(Long.valueOf(userId));
    }
}
