package ua.svyry.ewallet.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ua.svyry.ewallet.shared.CardDto;
import ua.svyry.ewallet.ui.model.CreateCardRequestModel;

@Component
public class CreateCardRequestModelToCardDtoConverter implements Converter<CreateCardRequestModel, CardDto> {
    @Override
    public CardDto convert(CreateCardRequestModel source) {
        return CardDto.builder()
                .balance(source.getBalance())
                .cardNumber(source.getCardNumber())
                .expirationDate(source.getExpirationDate())
                .walletId(source.getWalletId())
                .build();
    }
}
