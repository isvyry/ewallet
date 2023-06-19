package ua.svyry.ewallet.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ua.svyry.ewallet.entity.Transfer;
import ua.svyry.ewallet.shared.TransactionDto;

@Component
public class TransferToTransactionDtoConverter implements Converter<Transfer, TransactionDto> {

    private final String TRANSFER_TRANSACTION_TYPE = "Transfer";

    @Override
    public TransactionDto convert(Transfer source) {
        return TransactionDto.builder()
                .id(source.getId())
                .amount(source.getAmount())
                .cardId(source.getCard().getId())
                .cardNumber(source.getCard().getCardNumber())
                .receiverCardId(source.getReceiver().getId())
                .receiverCardNumber(source.getReceiver().getCardNumber())
                .transactionType(TRANSFER_TRANSACTION_TYPE)
                .createdDate(source.getCreatedDate())
                .isSuspicious(source.isSuspicious())
                .isSuccessful(source.isSuccessful())
                .build();
    }
}