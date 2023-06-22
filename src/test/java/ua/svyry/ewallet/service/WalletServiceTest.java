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
import ua.svyry.ewallet.entity.Customer;
import ua.svyry.ewallet.entity.Wallet;
import ua.svyry.ewallet.repository.WalletRepository;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    WalletRepository walletRepository = mock(WalletRepository.class);
    ConversionService conversionService = mock(ConversionService.class);
    WalletService service = new WalletService(walletRepository, conversionService);

    @Captor
    ArgumentCaptor<Wallet> walletCaptor;

    @Test
    @DisplayName("createWallet() should create wallet and save it to Repository")
    public void testCreateWallet() {

        Customer customer = Customer.builder().id(1l).build();

        service.createWallet(customer);

        verify(walletRepository, times(1)).save(walletCaptor.capture());

        Wallet wallet = walletCaptor.getValue();

        assertEquals(customer, wallet.getOwner());
    }

    @Test
    @DisplayName("getById() should get wallet")
    public void testGetById() {

        Long walletId = 1l;

        Wallet wallet = Wallet.builder().id(walletId).build();

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        Wallet result = service.getById(walletId);

        verify(walletRepository, times(1)).findById(walletId);

        assertEquals(wallet, result);
    }

    @Test
    @DisplayName("getById() should throw exception if wallet not exists")
    public void testGetByIdException() {

        Long walletId = 1l;

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        Executable exec = () -> service.getById(walletId);

        assertThrows(EntityNotFoundException.class, exec);
        verify(walletRepository, times(1)).findById(walletId);
    }

    @Test
    @DisplayName("getById() should get wallet by customer")
    public void testGetByCustomerId() {

        Long customerId = 1l;

        Wallet wallet = Wallet.builder()
                .id(1l)
                .owner(Customer.builder().id(customerId).build())
                .build();

        when(walletRepository.findByOwner_Id(customerId)).thenReturn(Optional.of(wallet));

        Wallet result = service.getByCustomerId(customerId);

        verify(walletRepository, times(1)).findByOwner_Id(customerId);

        assertEquals(wallet, result);
    }

    @Test
    @DisplayName("testGetByCustomerId() should throw exception if wallet not exists")
    public void testGetByCustomerIdException() {

        Long customerId = 1l;

        when(walletRepository.findByOwner_Id(customerId)).thenReturn(Optional.empty());

        Executable exec = () -> service.getByCustomerId(customerId);

        assertThrows(EntityNotFoundException.class, exec);
        verify(walletRepository, times(1)).findByOwner_Id(customerId);
    }
}
