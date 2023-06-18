package ua.svyry.ewallet.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ua.svyry.ewallet.shared.CardDto;
import ua.svyry.ewallet.ui.model.CardResponseModel;

@Component
public class CardDtoToCardResponseModelConverter implements Converter<CardDto, CardResponseModel> {
    @Override
    public CardResponseModel convert(CardDto source) {
        return CardResponseModel.builder()
                .id(source.getId())
                .cardNumber(source.getCardNumber())
                .balance(source.getBalance())
                .expirationDate(source.getExpirationDate())
                .walletId(source.getWalletId())
                .build();
    }
}
