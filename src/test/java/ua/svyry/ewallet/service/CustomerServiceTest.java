package ua.svyry.ewallet.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ua.svyry.ewallet.entity.Customer;
import ua.svyry.ewallet.entity.Wallet;
import ua.svyry.ewallet.repository.CustomerRepository;
import ua.svyry.ewallet.shared.CustomerDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    CustomerRepository customerRepository = mock(CustomerRepository.class);
    BCryptPasswordEncoder passwordEncoder = mock(BCryptPasswordEncoder.class);
    ConversionService conversionService = mock(ConversionService.class);
    WalletService walletService = mock(WalletService.class);
    CustomerService service = new CustomerService(customerRepository, walletService, passwordEncoder, conversionService);

    @Captor
    ArgumentCaptor<Customer> customerCaptor;
    @Test
    @DisplayName("createCustomer() should create customer and wallet for him")
    public void testCreateCustomer() {

        Long walletId = 1l;
        UUID walletNumber = UUID.randomUUID();

        CustomerDto customerDetails = CustomerDto.builder()
                .firstName("name")
                .lastName("test")
                .password("password")
                .email("email@gmail.com")
                .build();

        Customer savedCustomer = Customer.builder()
                .id(1l)
                .build();

        Wallet createdWallet = Wallet.builder()
                .walletNumber(walletNumber)
                .id(walletId)
                .build();

        when(walletService.createWallet(savedCustomer)).thenReturn(createdWallet);
        when(conversionService.convert(customerDetails, Customer.class)).thenReturn(savedCustomer);
        when(conversionService.convert(savedCustomer, CustomerDto.class)).thenReturn(customerDetails);
        when(customerRepository.save(savedCustomer)).thenReturn(savedCustomer);

        CustomerDto result = service.createCustomer(customerDetails);

        verify(passwordEncoder, times(1)).encode(any());
        verify(conversionService, times(2)).convert(any(), any());
        verify(customerRepository, times(1)).save(any());
        assertEquals(walletNumber.toString(), result.getWalletNumber());
        assertEquals(walletId, result.getWalletId());
    }

    @Test
    @DisplayName("getById() should get customer")
    public void testGetById() {

        Long customerId = 1l;

        Customer customer = Customer.builder()
                .id(customerId)
                .build();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        Customer result = service.getCustomerById(customerId);

        verify(customerRepository, times(1)).findById(customerId);

        assertEquals(customer, result);
    }

    @Test
    @DisplayName("getById() should throw exception if customer not exists")
    public void testGetByIdException() {

        Long customerId = 1l;

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        Executable exec = () -> service.getCustomerById(customerId);

        assertThrows(EntityNotFoundException.class, exec);
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    @DisplayName("loadUserByUsername() should get customer by username")
    public void testLoadUserByUsername() {

        String username = "email";

        Customer customer = Customer.builder()
                .id(1l)
                .email(username)
                .encryptedPassword("password")
                .build();

        when(customerRepository.findByEmail(username)).thenReturn(Optional.of(customer));

        UserDetails result = service.loadUserByUsername(username);

        verify(customerRepository, times(1)).findByEmail(username);

        assertEquals(username, result.getUsername());
    }

    @Test
    @DisplayName("loadUserByUsername() should throw exception if customer not exists")
    public void testLoadUserByUsernameException() {

        String username = "email";

        when(customerRepository.findByEmail(username)).thenReturn(Optional.empty());

        Executable exec = () -> service.loadUserByUsername(username);

        assertThrows(UsernameNotFoundException.class, exec);
        verify(customerRepository, times(1)).findByEmail(username);
    }

    @Test
    @DisplayName("unblockCustomer() should set blocked for transaction flag to false")
    public void testUnblockCustomer() {

        Long customerId = 1l;

        Customer customer = Customer.builder()
                .id(customerId)
                .wallet(new Wallet())
                .isBlockedForTransactions(true)
                .build();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        service.unblockCustomer(customerId);

        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, times(1)).save(customerCaptor.capture());

        Customer savedCustomer = customerCaptor.getValue();

        assertFalse(savedCustomer.isBlockedForTransactions());
    }

    @Test
    @DisplayName("unblockAllCustomers() call corresponding query from the repo and return amount of updated values")
    public void testUnblockAllCustomers() {

        when(customerRepository.updateAllBlockedForTransactionsTrue()).thenReturn(5);

        int result = service.unblockAllCustomers();

        verify(customerRepository, times(1)).updateAllBlockedForTransactionsTrue();
        assertEquals(5 , result);
    }

    @Test
    @DisplayName("getCustomerDetailsByUsername() should find customer by username and convert it to dto")
    public void testGetCustomerDetailsByUsername() {

        String username = "email";

        Customer customer = Customer.builder()
                .id(1l)
                .email(username)
                .build();

        when(customerRepository.findByEmail(username)).thenReturn(Optional.of(customer));

        service.getUserDetailsByUsername(username);

        verify(customerRepository, times(1)).findByEmail(username);
        verify(conversionService, times(1)).convert(customer, CustomerDto.class);
    }

    @Test
    @DisplayName("getCustomerDetailsById() should find customer by id and convert it to dto")
    public void testGetCustomerDetailsById() {

        Long customerId = 1l;

        Customer customer = Customer.builder()
                .id(customerId)
                .build();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        service.getCustomerDetailsById(customerId);

        verify(customerRepository, times(1)).findById(customerId);
        verify(conversionService, times(1)).convert(customer, CustomerDto.class);
    }

    @Test
    @DisplayName("getAllCustomers() should find all customers and convert each to dto")
    public void testGetAllCustomerDetails() {

        Customer customer1 = Customer.builder()
                .id(1l)
                .build();

        Customer customer2 = Customer.builder()
                .id(2l)
                .build();

        Customer customer3 = Customer.builder()
                .id(2l)
                .build();

        Pageable pageable = PageRequest.of(1, 10);
        when(customerRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(customer1, customer2, customer3)));

        service.getAllCustomers(pageable);

        verify(customerRepository, times(1)).findAll(pageable);
        verify(conversionService, times(3)).convert(any(), eq(CustomerDto.class));
    }
}
