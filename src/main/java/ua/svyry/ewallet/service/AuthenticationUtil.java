package ua.svyry.ewallet.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ua.svyry.ewallet.entity.Customer;
import ua.svyry.ewallet.exception.EWalletAccessDeniedException;

@Component
public class AuthenticationUtil {

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
    public void validateCustomerIsCurrentUser(Customer cardOwner, Authentication currentUser) {
        if (!checkCustomerIsCurrentUser(cardOwner, currentUser)) {
            throw new EWalletAccessDeniedException("Access is denied");
        }
    }

    public boolean checkCustomerIsCurrentUser(Customer cardOwner, Authentication currentUser) {
        String currentUserEmail = (String) currentUser.getPrincipal();
        return currentUserEmail.equals(cardOwner.getEmail());
    }


}
