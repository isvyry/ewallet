package ua.svyry.ewallet.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.svyry.ewallet.entity.Card;
import ua.svyry.ewallet.exception.DeletedEntityException;
import ua.svyry.ewallet.repository.CardRepository;
import ua.svyry.ewallet.shared.CardDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CardService {

    private final CardRepository cardRepository;
    private final WalletService walletService;
    private final ConversionService conversionService;

    public CardDto createCard(CardDto cardDetails) {
        Card builtCard = conversionService.convert(cardDetails, Card.class);
        builtCard.setWallet(walletService.getById(cardDetails.getWalletId()));
        Card savedCard = cardRepository.save(builtCard);
        return conversionService.convert(savedCard, CardDto.class);
    }

    public CardDto getCardDetailsById(Long id) {
        return conversionService.convert(getById(id), CardDto.class);
    }

    public List<CardDto> getCardDetailsByCustomerId(Long customerId, Pageable pageable) {
        Page<Card> customerCards = cardRepository.findAllByWallet(walletService.getByCustomerId(customerId), pageable);
        return customerCards.stream().map(c -> conversionService.convert(c, CardDto.class))
                .collect(Collectors.toList());
    }

    public List<CardDto> getCardDetailsByWalletId(Long walletId, Pageable pageable) {
        Page<Card> customerCards = cardRepository.findAllByWallet(walletService.getById(walletId), pageable);
        return customerCards.stream().map(c -> conversionService.convert(c, CardDto.class))
                .collect(Collectors.toList());
    }

    public void depositFunds(Card card, BigDecimal amount) {
        card.setBalance(card.getBalance().add(amount));
        cardRepository.save(card);
        log.info(String.format("Added %s EUR to the Card[id= %s, cardNumber = %s]", amount,
                card.getId(), card.getCardNumber()));
    }

    public boolean withdrawFunds(Card card, BigDecimal amount) {
        BigDecimal cardBalance = card.getBalance();
        if (cardBalance.compareTo(amount) < 0) {
            log.warn(String.format("Card[id = %s, cardNumber = %s] has insufficient funds." +
                    " Attempted withdraw amount: %s", card.getId(), card.getCardNumber(), amount));
            return false;
        } else {
            card.setBalance(cardBalance.subtract(amount));
            cardRepository.save(card);
            log.info(String.format("Withdrawn %s EUR from the Card[id= %s, cardNumber = %s].", amount,
                    card.getId(), card.getCardNumber()));
            return true;
        }
    }

    public boolean transferFunds(Card from, Card to, BigDecimal amount) {
        BigDecimal senderCardBalance = from.getBalance();
        if (senderCardBalance.compareTo(amount) < 0) {
            log.warn(String.format("Card[id = %s, cardNumber = %s] has insufficient funds." +
                    " Attempted transfer amount: %s", from.getId(), from.getCardNumber(), amount));
            return false;
        } else {
            from.setBalance(senderCardBalance.subtract(amount));
            to.setBalance(to.getBalance().add(amount));
            cardRepository.save(from);
            cardRepository.save(to);
            log.info(String.format("Transfer %s EUR from the Card[id= %s, cardNumber = %s] " +
                            "to the Card[id= %s, cardNumber = %s] successfully finalized.", amount,
                    from.getId(), from.getCardNumber(), to.getId(), to.getCardNumber()));
            return true;
        }
    }

    public void deleteCard(Long id) {
        Card cardToDelete = getById(id);
        log.warn(String.format("Deleting Card[id: %s, cardNumber: %s]", cardToDelete.getId(),
                cardToDelete.getCardNumber()));
        cardToDelete.setDeleted(true);
        cardRepository.save(cardToDelete);
    }

    public Card getById(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Cannot find card with id: %s", id)));
        if (card.isDeleted()) {
            throw new DeletedEntityException(String.format("Card[id: %s, cardNumber: %s] has been deleted",
                    card.getId(), card.getCardNumber()));
        }
        return card;
    }
}
