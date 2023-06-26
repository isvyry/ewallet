package ua.svyry.ewallet.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;
import ua.svyry.ewallet.entity.Card;
import ua.svyry.ewallet.entity.Deposit;
import ua.svyry.ewallet.entity.Withdrawal;
import ua.svyry.ewallet.entity.Transfer;
import ua.svyry.ewallet.entity.Wallet;
import ua.svyry.ewallet.entity.Customer;
import ua.svyry.ewallet.repository.DepositRepository;
import ua.svyry.ewallet.repository.TransactionRepository;
import ua.svyry.ewallet.repository.TransferRepository;
import ua.svyry.ewallet.repository.WithdrawalRepository;
import ua.svyry.ewallet.shared.TransactionDto;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    CardService cardService = mock(CardService.class);
    ConversionService conversionService = mock(ConversionService.class);
    SuspiciousActivityService suspiciousActivityService = mock(SuspiciousActivityService.class);
    DepositRepository depositRepository = mock(DepositRepository.class);
    WithdrawalRepository withdrawalRepository = mock(WithdrawalRepository.class);
    TransferRepository transferRepository = mock(TransferRepository.class);
    TransactionRepository transactionRepository = mock(TransactionRepository.class);

    TransactionService service = new TransactionService(cardService, conversionService,
            suspiciousActivityService, depositRepository, withdrawalRepository, transferRepository,
            transactionRepository);

    @Captor
    ArgumentCaptor<Deposit> depositCaptor;

    @Captor
    ArgumentCaptor<Withdrawal> withdrawalCaptor;

    @Captor
    ArgumentCaptor<Transfer> transferCaptor;

    @Test
    @DisplayName("depositFunds() should save deposit")
    public void testDepositFunds() {
        Long cardId = 1l;
        Card card = Card.builder()
                .id(cardId)
                .balance(BigDecimal.valueOf(1000))
                .wallet(Wallet.builder().id(1l).owner(new Customer()).build())
                .build();

        TransactionDto transactionDetails = TransactionDto.builder()
                .cardId(cardId)
                .amount(BigDecimal.valueOf(500))
                .build();

        when(cardService.getById(cardId)).thenReturn(card);
        when(suspiciousActivityService.isCustomerAllowedForTransactions(any())).thenReturn(true);

        service.depositFunds(transactionDetails);

        verify(cardService, times(1)).depositFunds(card, BigDecimal.valueOf(500));
        verify(suspiciousActivityService, times(0)).checkSuspiciousActivity(card);
        verify(depositRepository, times(1)).save(depositCaptor.capture());
        verify(conversionService, times(1)).convert(any(), eq(TransactionDto.class));

        Deposit savedResult = depositCaptor.getValue();

        assertEquals(card, savedResult.getCard());
        assertEquals(BigDecimal.valueOf(500), savedResult.getAmount());
        assertTrue(savedResult.isSuccessful());
        assertFalse(savedResult.isSuspicious());
    }

    @Test
    @DisplayName("depositFunds() should not save deposit above 2000")
    public void testDepositFundsAboveLimit() {
        Long cardId = 1l;
        Card card = Card.builder()
                .id(cardId)
                .balance(BigDecimal.valueOf(1000))
                .wallet(Wallet.builder().id(1l).owner(new Customer()).build())
                .build();

        TransactionDto transactionDetails = TransactionDto.builder()
                .cardId(cardId)
                .amount(BigDecimal.valueOf(2500))
                .build();

        when(cardService.getById(cardId)).thenReturn(card);
        when(suspiciousActivityService.isCustomerAllowedForTransactions(any())).thenReturn(true);

        service.depositFunds(transactionDetails);

        verify(cardService, times(0)).depositFunds(card, BigDecimal.valueOf(500));
        verify(suspiciousActivityService, times(0)).checkSuspiciousActivity(card);
        verify(depositRepository, times(1)).save(depositCaptor.capture());
        verify(conversionService, times(1)).convert(any(), eq(TransactionDto.class));

        Deposit savedResult = depositCaptor.getValue();

        assertEquals(card, savedResult.getCard());
        assertEquals(BigDecimal.valueOf(2500), savedResult.getAmount());
        assertFalse(savedResult.isSuccessful());
        assertFalse(savedResult.isSuspicious());
    }

    @Test
    @DisplayName("depositFunds() should mark transactions as suspicious above 10000")
    public void testDepositFundsSuspicious() {
        Long cardId = 1l;
        Card card = Card.builder()
                .id(cardId)
                .balance(BigDecimal.valueOf(1000))
                .wallet(Wallet.builder().id(1l).owner(new Customer()).build())
                .build();

        TransactionDto transactionDetails = TransactionDto.builder()
                .cardId(cardId)
                .amount(BigDecimal.valueOf(15000))
                .build();

        when(cardService.getById(cardId)).thenReturn(card);
        when(suspiciousActivityService.isCustomerAllowedForTransactions(any())).thenReturn(true);

        service.depositFunds(transactionDetails);

        verify(cardService, times(0)).depositFunds(card, BigDecimal.valueOf(500));
        verify(suspiciousActivityService, times(1)).checkSuspiciousActivity(card);
        verify(depositRepository, times(1)).save(depositCaptor.capture());
        verify(conversionService, times(1)).convert(any(), eq(TransactionDto.class));

        Deposit savedResult = depositCaptor.getValue();

        assertEquals(card, savedResult.getCard());
        assertEquals(BigDecimal.valueOf(15000), savedResult.getAmount());
        assertFalse(savedResult.isSuccessful());
        assertTrue(savedResult.isSuspicious());
    }

    @Test
    @DisplayName("depositFunds() should not save deposit for blocked customer")
    public void testDepositFundsForBlockedCustomer() {
        Long cardId = 1l;
        Card card = Card.builder()
                .id(cardId)
                .balance(BigDecimal.valueOf(1000))
                .wallet(Wallet.builder().id(1l).owner(new Customer()).build())
                .build();

        TransactionDto transactionDetails = TransactionDto.builder()
                .cardId(cardId)
                .amount(BigDecimal.valueOf(500))
                .build();

        when(cardService.getById(cardId)).thenReturn(card);
        when(suspiciousActivityService.isCustomerAllowedForTransactions(any())).thenReturn(false);

        service.depositFunds(transactionDetails);

        verify(cardService, times(0)).depositFunds(card, BigDecimal.valueOf(500));
        verify(suspiciousActivityService, times(0)).checkSuspiciousActivity(card);
        verify(depositRepository, times(1)).save(depositCaptor.capture());
        verify(conversionService, times(1)).convert(any(), eq(TransactionDto.class));

        Deposit savedResult = depositCaptor.getValue();

        assertEquals(card, savedResult.getCard());
        assertEquals(BigDecimal.valueOf(500), savedResult.getAmount());
        assertFalse(savedResult.isSuccessful());
        assertFalse(savedResult.isSuspicious());
    }

    @Test
    @DisplayName("withdrawFunds() should save withdrawal")
    public void testWithdrawFunds() {
        Long cardId = 1l;
        Card card = Card.builder()
                .id(cardId)
                .balance(BigDecimal.valueOf(1000))
                .wallet(Wallet.builder().id(1l).owner(new Customer()).build())
                .build();

        TransactionDto transactionDetails = TransactionDto.builder()
                .cardId(cardId)
                .amount(BigDecimal.valueOf(500))
                .build();

        when(cardService.getById(cardId)).thenReturn(card);
        when(suspiciousActivityService.isCustomerAllowedForTransactions(any())).thenReturn(true);
        when(cardService.withdrawFunds(card, BigDecimal.valueOf(500))).thenReturn(true);

        service.withdrawFunds(transactionDetails);

        verify(cardService, times(1)).withdrawFunds(card, BigDecimal.valueOf(500));
        verify(suspiciousActivityService, times(0)).checkSuspiciousActivity(card);
        verify(withdrawalRepository, times(1)).save(withdrawalCaptor.capture());
        verify(conversionService, times(1)).convert(any(), eq(TransactionDto.class));

        Withdrawal savedResult = withdrawalCaptor.getValue();

        assertEquals(card, savedResult.getCard());
        assertEquals(BigDecimal.valueOf(500), savedResult.getAmount());
        assertTrue(savedResult.isSuccessful());
        assertFalse(savedResult.isSuspicious());
    }

    @Test
    @DisplayName("withdrawFunds() should not save withdrawal above 2000")
    public void testWithdrawalFundsAboveLimit() {
        Long cardId = 1l;
        Card card = Card.builder()
                .id(cardId)
                .balance(BigDecimal.valueOf(1000))
                .wallet(Wallet.builder().id(1l).owner(new Customer()).build())
                .build();

        TransactionDto transactionDetails = TransactionDto.builder()
                .cardId(cardId)
                .amount(BigDecimal.valueOf(2500))
                .build();

        when(cardService.getById(cardId)).thenReturn(card);
        when(suspiciousActivityService.isCustomerAllowedForTransactions(any())).thenReturn(true);

        service.withdrawFunds(transactionDetails);

        verify(cardService, times(0)).withdrawFunds(card, BigDecimal.valueOf(2500));
        verify(suspiciousActivityService, times(0)).checkSuspiciousActivity(card);
        verify(withdrawalRepository, times(1)).save(withdrawalCaptor.capture());
        verify(conversionService, times(1)).convert(any(), eq(TransactionDto.class));

        Withdrawal savedResult = withdrawalCaptor.getValue();

        assertEquals(card, savedResult.getCard());
        assertEquals(BigDecimal.valueOf(2500), savedResult.getAmount());
        assertFalse(savedResult.isSuccessful());
        assertFalse(savedResult.isSuspicious());
    }

    @Test
    @DisplayName("withdrawFunds() should mark transactions as suspicious above 10000")
    public void testWithdrawalFundsSuspicious() {
        Long cardId = 1l;
        Card card = Card.builder()
                .id(cardId)
                .balance(BigDecimal.valueOf(1000))
                .wallet(Wallet.builder().id(1l).owner(new Customer()).build())
                .build();

        TransactionDto transactionDetails = TransactionDto.builder()
                .cardId(cardId)
                .amount(BigDecimal.valueOf(15000))
                .build();

        when(cardService.getById(cardId)).thenReturn(card);
        when(suspiciousActivityService.isCustomerAllowedForTransactions(any())).thenReturn(true);

        service.withdrawFunds(transactionDetails);

        verify(cardService, times(0)).withdrawFunds(card, BigDecimal.valueOf(15000));
        verify(suspiciousActivityService, times(1)).checkSuspiciousActivity(card);
        verify(withdrawalRepository, times(1)).save(withdrawalCaptor.capture());
        verify(conversionService, times(1)).convert(any(), eq(TransactionDto.class));

        Withdrawal savedResult = withdrawalCaptor.getValue();

        assertEquals(card, savedResult.getCard());
        assertEquals(BigDecimal.valueOf(15000), savedResult.getAmount());
        assertFalse(savedResult.isSuccessful());
        assertTrue(savedResult.isSuspicious());
    }

    @Test
    @DisplayName("withdrawFunds() should not save withdraw for blocked customer")
    public void testWithdrawFundsForBlockedCustomer() {
        Long cardId = 1l;
        Card card = Card.builder()
                .id(cardId)
                .balance(BigDecimal.valueOf(1000))
                .wallet(Wallet.builder().id(1l).owner(new Customer()).build())
                .build();

        TransactionDto transactionDetails = TransactionDto.builder()
                .cardId(cardId)
                .amount(BigDecimal.valueOf(500))
                .build();

        when(cardService.getById(cardId)).thenReturn(card);
        when(suspiciousActivityService.isCustomerAllowedForTransactions(any())).thenReturn(false);

        service.withdrawFunds(transactionDetails);

        verify(cardService, times(0)).withdrawFunds(card, BigDecimal.valueOf(500));
        verify(suspiciousActivityService, times(0)).checkSuspiciousActivity(card);
        verify(withdrawalRepository, times(1)).save(withdrawalCaptor.capture());
        verify(conversionService, times(1)).convert(any(), eq(TransactionDto.class));

        Withdrawal savedResult = withdrawalCaptor.getValue();

        assertEquals(card, savedResult.getCard());
        assertEquals(BigDecimal.valueOf(500), savedResult.getAmount());
        assertFalse(savedResult.isSuccessful());
        assertFalse(savedResult.isSuspicious());
    }

    @Test
    @DisplayName("transferFunds() should save transfer")
    public void testTransferFunds() {
        Long card1Id = 1l;
        Long card2Id = 2l;
        Card card1 = Card.builder()
                .id(card1Id)
                .balance(BigDecimal.valueOf(1000))
                .wallet(Wallet.builder().id(1l).owner(new Customer()).build())
                .build();

        Card card2 = Card.builder()
                .id(card2Id)
                .balance(BigDecimal.valueOf(1000))
                .wallet(Wallet.builder().id(2l).owner(new Customer()).build())
                .build();

        TransactionDto transactionDetails = TransactionDto.builder()
                .cardId(card1Id)
                .receiverCardId(card2Id)
                .amount(BigDecimal.valueOf(500))
                .build();

        when(cardService.getById(card1Id)).thenReturn(card1);
        when(cardService.getById(card2Id)).thenReturn(card2);
        when(suspiciousActivityService.isCustomerAllowedForTransactions(any())).thenReturn(true);
        when(cardService.transferFunds(card1, card2, BigDecimal.valueOf(500))).thenReturn(true);

        service.transferFunds(transactionDetails);

        verify(cardService, times(1)).transferFunds(card1, card2, BigDecimal.valueOf(500));
        verify(suspiciousActivityService, times(0)).checkSuspiciousActivity(card1);
        verify(suspiciousActivityService, times(0)).checkSuspiciousActivity(card2);
        verify(transferRepository, times(1)).save(transferCaptor.capture());
        verify(conversionService, times(1)).convert(any(), eq(TransactionDto.class));

        Transfer savedResult = transferCaptor.getValue();

        assertEquals(card1, savedResult.getCard());
        assertEquals(card2, savedResult.getReceiverCard());
        assertEquals(BigDecimal.valueOf(500), savedResult.getAmount());
        assertTrue(savedResult.isSuccessful());
        assertFalse(savedResult.isSuspicious());
    }

    @Test
    @DisplayName("transferFunds() should not save transfer above 2000")
    public void testTransferFundsAboveLimit() {
        Long card1Id = 1l;
        Long card2Id = 2l;
        Card card1 = Card.builder()
                .id(card1Id)
                .balance(BigDecimal.valueOf(1000))
                .wallet(Wallet.builder().id(1l).owner(new Customer()).build())
                .build();

        Card card2 = Card.builder()
                .id(card2Id)
                .balance(BigDecimal.valueOf(1000))
                .wallet(Wallet.builder().id(2l).owner(new Customer()).build())
                .build();

        TransactionDto transactionDetails = TransactionDto.builder()
                .cardId(card1Id)
                .receiverCardId(card2Id)
                .amount(BigDecimal.valueOf(2500))
                .build();

        when(cardService.getById(card1Id)).thenReturn(card1);
        when(cardService.getById(card2Id)).thenReturn(card2);
        when(suspiciousActivityService.isCustomerAllowedForTransactions(any())).thenReturn(true);

        service.transferFunds(transactionDetails);

        verify(cardService, times(0)).transferFunds(card1, card2, BigDecimal.valueOf(2500));
        verify(suspiciousActivityService, times(0)).checkSuspiciousActivity(card1);
        verify(suspiciousActivityService, times(0)).checkSuspiciousActivity(card2);
        verify(transferRepository, times(1)).save(transferCaptor.capture());
        verify(conversionService, times(1)).convert(any(), eq(TransactionDto.class));

        Transfer savedResult = transferCaptor.getValue();

        assertEquals(card1, savedResult.getCard());
        assertEquals(card2, savedResult.getReceiverCard());
        assertEquals(BigDecimal.valueOf(2500), savedResult.getAmount());
        assertFalse(savedResult.isSuccessful());
        assertFalse(savedResult.isSuspicious());
    }

    @Test
    @DisplayName("transferFunds() should mark transactions as suspicious above 10000")
    public void testTransferFundsSuspicious() {
        Long card1Id = 1l;
        Long card2Id = 2l;
        Card card1 = Card.builder()
                .id(card1Id)
                .balance(BigDecimal.valueOf(1000))
                .wallet(Wallet.builder().id(1l).owner(new Customer()).build())
                .build();

        Card card2 = Card.builder()
                .id(card2Id)
                .balance(BigDecimal.valueOf(1000))
                .wallet(Wallet.builder().id(2l).owner(new Customer()).build())
                .build();

        TransactionDto transactionDetails = TransactionDto.builder()
                .cardId(card1Id)
                .receiverCardId(card2Id)
                .amount(BigDecimal.valueOf(15000))
                .build();

        when(cardService.getById(card1Id)).thenReturn(card1);
        when(cardService.getById(card2Id)).thenReturn(card2);
        when(suspiciousActivityService.isCustomerAllowedForTransactions(any())).thenReturn(true);

        service.transferFunds(transactionDetails);

        verify(cardService, times(0)).transferFunds(card1, card2, BigDecimal.valueOf(15000));
        verify(suspiciousActivityService, times(1)).checkSuspiciousActivity(card1);
        verify(suspiciousActivityService, times(0)).checkSuspiciousActivity(card2);
        verify(transferRepository, times(1)).save(transferCaptor.capture());
        verify(conversionService, times(1)).convert(any(), eq(TransactionDto.class));

        Transfer savedResult = transferCaptor.getValue();

        assertEquals(card1, savedResult.getCard());
        assertEquals(card2, savedResult.getReceiverCard());
        assertEquals(BigDecimal.valueOf(15000), savedResult.getAmount());
        assertFalse(savedResult.isSuccessful());
        assertTrue(savedResult.isSuspicious());
    }

    @Test
    @DisplayName("transferFunds() should not save transfer for blocked customer")
    public void testTransferFundsForBlockedCustomer() {
        Long card1Id = 1l;
        Long card2Id = 2l;
        Card card1 = Card.builder()
                .id(card1Id)
                .balance(BigDecimal.valueOf(1000))
                .wallet(Wallet.builder().id(1l).owner(new Customer()).build())
                .build();

        Card card2 = Card.builder()
                .id(card2Id)
                .balance(BigDecimal.valueOf(1000))
                .wallet(Wallet.builder().id(2l).owner(new Customer()).build())
                .build();

        TransactionDto transactionDetails = TransactionDto.builder()
                .cardId(card1Id)
                .receiverCardId(card2Id)
                .amount(BigDecimal.valueOf(500))
                .build();

        when(cardService.getById(card1Id)).thenReturn(card1);
        when(cardService.getById(card2Id)).thenReturn(card2);
        when(suspiciousActivityService.isCustomerAllowedForTransactions(any())).thenReturn(false);

        service.transferFunds(transactionDetails);

        verify(cardService, times(0)).transferFunds(card1, card2, BigDecimal.valueOf(500));
        verify(suspiciousActivityService, times(0)).checkSuspiciousActivity(card1);
        verify(suspiciousActivityService, times(0)).checkSuspiciousActivity(card2);
        verify(transferRepository, times(1)).save(transferCaptor.capture());
        verify(conversionService, times(1)).convert(any(), eq(TransactionDto.class));

        Transfer savedResult = transferCaptor.getValue();

        assertEquals(card1, savedResult.getCard());
        assertEquals(card2, savedResult.getReceiverCard());
        assertEquals(BigDecimal.valueOf(500), savedResult.getAmount());
        assertFalse(savedResult.isSuccessful());
        assertFalse(savedResult.isSuspicious());
    }

    @Test
    @DisplayName("getLastHourSuspiciousTransactionsByCustomer() should call corresponding method from TransactionRepository")
    public void testGetLastHourSuspiciousTransactionsByCustomer() {
        Customer customer = Customer.builder().id(1l).build();

        when(transactionRepository.countAllSuspiciousByCustomerForTheLastHour(1l)).thenReturn(5);

        int result = service.getLastHourSuspiciousTransactionsByCustomer(customer);

        verify(transactionRepository, times(1)).countAllSuspiciousByCustomerForTheLastHour(1l);
        assertEquals(5, result);
    }
}
