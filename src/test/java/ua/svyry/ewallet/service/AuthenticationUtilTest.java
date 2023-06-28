package ua.svyry.ewallet.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import ua.svyry.ewallet.entity.Customer;
import ua.svyry.ewallet.exception.EWalletAccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class AuthenticationUtilTest {

    AuthenticationUtil authenticationUtil = new AuthenticationUtil();

    @Test
    public void testCheckCustomerIsCurrentUserTrue() {
        Customer customer = Customer.builder().email("email@gmail.com").build();
        Authentication auth = new UsernamePasswordAuthenticationToken("email@gmail.com", "");

        boolean result = authenticationUtil.checkCustomerIsCurrentUser(customer, auth);

        assertTrue(result);
    }

    @Test
    public void testCheckCustomerIsCurrentUserFalse() {
        Customer customer = Customer.builder().email("email1@gmail.com").build();
        Authentication auth = new UsernamePasswordAuthenticationToken("email2@gmail.com", "");

        boolean result = authenticationUtil.checkCustomerIsCurrentUser(customer, auth);

        assertFalse(result);
    }

    @Test
    public void testValidateCustomerIsCurrentUserSuccess() {
        Customer customer = Customer.builder().email("email@gmail.com").build();
        Authentication auth = new UsernamePasswordAuthenticationToken("email@gmail.com", "");

        Executable exec = () -> authenticationUtil.validateCustomerIsCurrentUser(customer, auth);

        assertDoesNotThrow(exec);
    }

    @Test
    public void testValidateCustomerIsCurrentUserException() {
        Customer customer = Customer.builder().email("email1@gmail.com").build();
        Authentication auth = new UsernamePasswordAuthenticationToken("email2@gmail.com", "");

        Executable exec = () -> authenticationUtil.validateCustomerIsCurrentUser(customer, auth);

       assertThrows(EWalletAccessDeniedException.class, exec);
    }
}
