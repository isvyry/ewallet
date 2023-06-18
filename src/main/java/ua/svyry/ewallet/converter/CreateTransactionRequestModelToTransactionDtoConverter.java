package ua.svyry.ewallet.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ua.svyry.ewallet.shared.TransactionDto;
import ua.svyry.ewallet.ui.model.CreateTransactionRequestModel;

@Component
public class CreateTransactionRequestModelToTransactionDtoConverter
        implements Converter<CreateTransactionRequestModel, TransactionDto> {
    @Override
    public TransactionDto convert(CreateTransactionRequestModel source) {
        return TransactionDto.builder()
                .amount(source.getAmount())
                .cardId(source.getCardId())
                .build();
    }
}
