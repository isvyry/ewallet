package ua.svyry.ewallet.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ua.svyry.ewallet.shared.TransactionDto;
import ua.svyry.ewallet.ui.model.TransactionResult;

@Component
public class TransactionDtoToTransactionResultConverter implements Converter<TransactionDto, TransactionResult> {
    @Override
    public TransactionResult convert(TransactionDto source) {
        return TransactionResult.builder()
                .id(source.getId())
                .cardId(source.getCardId())
                .amount(source.getAmount())
                .transactionType(source.getTransactionType())
                .cardNumber(source.getCardNumber())
                .createdDate(source.getCreatedDate())
                .isSuspicious(source.isSuspicious())
                .isSuccessful(source.isSuccessful())
                .receiverCardId(source.getReceiverCardId())
                .receiverCardNumber(source.getReceiverCardNumber())
                .build();
    }
}
