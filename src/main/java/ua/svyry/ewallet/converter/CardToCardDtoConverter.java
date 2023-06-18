package ua.svyry.ewallet.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ua.svyry.ewallet.entity.Card;
import ua.svyry.ewallet.shared.CardDto;

@Component
public class CardToCardDtoConverter implements Converter<Card, CardDto> {
    @Override
    public CardDto convert(Card source) {
        return CardDto.builder()
                .id(source.getId())
                .balance(source.getBalance())
                .cardNumber(source.getCardNumber())
                .walletId(source.getWallet().getId())
                .expirationDate(source.getExpirationDate())
                .build();
    }
}
