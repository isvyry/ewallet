package ua.svyry.ewallet.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ua.svyry.ewallet.shared.TransactionDto;
import ua.svyry.ewallet.ui.model.CreateTransferTransactionRequestModel;

@Component
public class CreateTransferTransactionRequestModelToTransactionDtoConverter
        implements Converter<CreateTransferTransactionRequestModel, TransactionDto> {
    @Override
    public TransactionDto convert(CreateTransferTransactionRequestModel source) {
        return TransactionDto
                .builder()
                .amount(source.getAmount())
                .cardId(source.getCardId())
                .receiverCardId(source.getReceiverCardId())
                .build();
    }
}
