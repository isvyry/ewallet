package ua.svyry.ewallet.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ua.svyry.ewallet.entity.Card;
import ua.svyry.ewallet.shared.CardDto;

@Component
public class CardDtoToCardConverter implements Converter<CardDto, Card> {
    @Override
    public Card convert(CardDto source) {
        return Card.builder()
                .cardNumber(source.getCardNumber())
                .expirationDate(source.getExpirationDate())
                .balance(source.getBalance())
                .build();
    }
}
