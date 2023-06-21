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
import ua.svyry.ewallet.entity.Card;
import ua.svyry.ewallet.entity.Customer;
import ua.svyry.ewallet.entity.Wallet;
import ua.svyry.ewallet.exception.DeletedEntityException;
import ua.svyry.ewallet.repository.CardRepository;
import ua.svyry.ewallet.shared.CardDto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Period;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    CardRepository cardRepository = mock(CardRepository.class);
    WalletService walletService = mock(WalletService.class);
    ConversionService conversionService = mock(ConversionService.class);
    CardService service = new CardService(cardRepository, walletService, conversionService);

    @Captor
    ArgumentCaptor<Card> cardCaptor;

    @Test
    @DisplayName("createCard() should create card successfully")
    public void testCreateCard() {

        CardDto cardDetails = CardDto.builder()
                .balance(new BigDecimal(500))
                .expirationDate(new Date(Instant.now().plus(Period.ofWeeks(10)).toEpochMilli()))
                .cardNumber("1111222233334444")
                .walletId(1l)
                .build();

        Card card = Card.builder().build();

        when(conversionService.convert(cardDetails, Card.class)).thenReturn(card);
        when(cardRepository.save(card)).thenReturn(card);

        service.createCard(cardDetails);

        verify(cardRepository, times(1)).save(any());
        verify(walletService, times(1)).getById(1l);
        verify(conversionService, times(1)).convert(cardDetails, Card.class);
        verify(conversionService, times(1)).convert(card, CardDto.class);
    }

    @Test
    @DisplayName("test getById() happy flow")
    public void testGetById() {
        Long cardId = 1l;

        Card card = Card.builder()
                .id(cardId)
                .build();

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        service.getById(cardId);

        verify(cardRepository, times(1)).findById(cardId);
    }

    @Test
    @DisplayName("test getById() when card doesn't exists")
    public void testGetByIdNotExistsException() {
        Long cardId = 1l;

        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        Executable exec = () -> service.getById(cardId);

        assertThrows(EntityNotFoundException.class, exec);
        verify(cardRepository, times(1)).findById(cardId);
    }

    @Test
    @DisplayName("test getById() when card is deleted")
    public void testGetByIdDeletedException() {
        Long cardId = 1l;

        Card card = Card.builder()
                .id(cardId)
                .isDeleted(true)
                .build();

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        Executable exec = () -> service.getById(cardId);

        assertThrows(DeletedEntityException.class, exec);
        verify(cardRepository, times(1)).findById(cardId);
    }

    @Test
    @DisplayName("deleteCard() should set isDeleted to true")
    public void testDeleteCard() {
        Long cardId = 1l;

        Card card = Card.builder()
                .id(cardId)
                .isDeleted(false)
                .build();

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        service.deleteCard(cardId);

        verify(cardRepository, times(1)).save(cardCaptor.capture());

        Card savedCard = cardCaptor.getValue();

        assertTrue(savedCard.isDeleted());
    }

    @Test
    @DisplayName("depositFunds() should add funds to the card balance")
    public void testDepositFunds() {

        Card card = Card.builder()
                .id(1l)
                .balance(new BigDecimal(0))
                .build();

        BigDecimal amount = new BigDecimal(500);

        service.depositFunds(card, amount);

        verify(cardRepository, times(1)).save(cardCaptor.capture());

        Card savedCard = cardCaptor.getValue();

        assertEquals(savedCard.getBalance(), BigDecimal.valueOf(500));
    }

    @Test
    @DisplayName("withdrawFunds() should deduct funds from the card balance and return true")
    public void testWithdrawFundsSuccessful() {

        Card card = Card.builder()
                .id(1l)
                .balance(new BigDecimal(5000))
                .build();

        BigDecimal amount = new BigDecimal(500);

        boolean result = service.withdrawFunds(card, amount);

        verify(cardRepository, times(1)).save(cardCaptor.capture());

        Card savedCard = cardCaptor.getValue();

        assertEquals(savedCard.getBalance(), BigDecimal.valueOf(4500));
        assertTrue(result);
    }

    @Test
    @DisplayName("withdrawFunds() should return false if there are not enough money")
    public void testWithdrawFundsUnsuccessful() {

        Card card = Card.builder()
                .id(1l)
                .balance(new BigDecimal(1000))
                .build();

        BigDecimal amount = new BigDecimal(1500);

        boolean result = service.withdrawFunds(card, amount);

        assertFalse(result);
    }

    @Test
    @DisplayName("transferFunds() should transfer funds from the card to another and return true")
    public void testTransferFundsSuccessful() {

        Card from = Card.builder()
                .id(1l)
                .balance(new BigDecimal(1000))
                .build();
        Card to = Card.builder()
                .id(2l)
                .balance(new BigDecimal(1000))
                .build();

        BigDecimal amount = new BigDecimal(500);

        boolean result = service.transferFunds(from, to, amount);

        verify(cardRepository, times(2)).save(cardCaptor.capture());

        List<Card> savedCards = cardCaptor.getAllValues();

        assertTrue(result);
        assertEquals(savedCards.size(), 2);

        Card first = savedCards.stream().filter(c -> c.getId().equals(1l)).findFirst().get();
        Card second = savedCards.stream().filter(c -> c.getId().equals(2l)).findFirst().get();
        assertEquals(first.getBalance(), BigDecimal.valueOf(500));
        assertEquals(second.getBalance(), BigDecimal.valueOf(1500));
    }

    @Test
    @DisplayName("transferFunds() should return false if there are not enough money")
    public void testTransferFundsUnsuccessful() {

        Card from = Card.builder()
                .id(1l)
                .balance(new BigDecimal(1000))
                .build();
        Card to = Card.builder()
                .id(2l)
                .balance(new BigDecimal(1000))
                .build();

        BigDecimal amount = new BigDecimal(1500);

        boolean result = service.transferFunds(from, to, amount);

        assertFalse(result);
    }

    @Test
    @DisplayName("getCardDetailsById() should invoke conversionService and return CardDto object")
    public void testGetCardDetailsById() {
        Long cardId = 1l;
        Card card = Card.builder()
                .id(cardId)
                .balance(new BigDecimal(1000))
                .build();

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        service.getCardDetailsById(cardId);

        verify(conversionService, times(1)).convert(card, CardDto.class);
    }

    @Test
    @DisplayName("getCardDetailsByCustomerId() should return cards belonged to the provided customer")
    public void testGetCardDetailsByCustomerId() {
        Long customerId = 1l;
        Customer owner = Customer.builder()
                .id(customerId)
                .build();

        Wallet wallet = Wallet.builder()
                .id(1l)
                .owner(owner)
                .walletNumber(UUID.randomUUID())
                .build();

        Card card1 = Card.builder()
                .id(1l)
                .wallet(wallet)
                .balance(new BigDecimal(2000))
                .build();

        Card card2 = Card.builder()
                .id(2l)
                .wallet(wallet)
                .balance(new BigDecimal(1000))
                .build();

        when(walletService.getByCustomerId(customerId)).thenReturn(wallet);
        when(cardRepository.findAllByWallet(eq(wallet), any())).thenReturn(new PageImpl<>(List.of(card1, card2)));

        service.getCardDetailsByCustomerId(customerId, PageRequest.of(1, 20));

        verify(conversionService, times(2)).convert(any(), eq(CardDto.class));
        verify(cardRepository, times(1)).findAllByWallet(eq(wallet), any());
        verify(walletService, times(1)).getByCustomerId(customerId);
    }

    @Test
    @DisplayName("getCardDetailsByWallet() should return cards belonged to the provided wallet")
    public void testGetCardDetailsByWalletId() {

        Long walletId = 1l;

        Wallet wallet = Wallet.builder()
                .id(walletId)
                .walletNumber(UUID.randomUUID())
                .build();

        Card card1 = Card.builder()
                .id(1l)
                .wallet(wallet)
                .balance(new BigDecimal(2000))
                .build();

        Card card2 = Card.builder()
                .id(2l)
                .wallet(wallet)
                .balance(new BigDecimal(1000))
                .build();

        when(walletService.getById(walletId)).thenReturn(wallet);
        when(cardRepository.findAllByWallet(eq(wallet), any())).thenReturn(new PageImpl<>(List.of(card1, card2)));

        service.getCardDetailsByWalletId(walletId, PageRequest.of(1, 20));

        verify(conversionService, times(2)).convert(any(), eq(CardDto.class));
        verify(cardRepository, times(1)).findAllByWallet(eq(wallet), any());
        verify(walletService, times(1)).getById(walletId);
    }
}
