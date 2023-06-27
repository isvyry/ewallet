package ua.svyry.ewallet.service;

import org.springframework.security.core.Authentication;

public interface AuthenticationRetrieverFacade {
    Authentication getAuthentication();
}
