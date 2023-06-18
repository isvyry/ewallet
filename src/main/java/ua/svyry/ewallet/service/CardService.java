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
import ua.svyry.ewallet.entity.Customer;
import ua.svyry.ewallet.repository.CardRepository;
import ua.svyry.ewallet.shared.CardDto;
import ua.svyry.ewallet.shared.CustomerDto;

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

    public void deleteCard(Long id) {
        Card cardToDelete = getById(id);
        log.warn(String.format("Deleting Card[id: %s, cardNumber: %s]", cardToDelete.getId(),
                cardToDelete.getCardNumber()));
        cardRepository.delete(cardToDelete);
    }

    public Card getById(Long id) {
        return cardRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Cannot find card with id: %s", id)));
    }
}
