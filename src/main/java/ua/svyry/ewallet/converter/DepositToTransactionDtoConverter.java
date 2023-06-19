package ua.svyry.ewallet.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ua.svyry.ewallet.entity.Deposit;
import ua.svyry.ewallet.shared.TransactionDto;

@Component
public class DepositToTransactionDtoConverter implements Converter<Deposit, TransactionDto> {

    private final String DEPOSIT_TRANSACTION_TYPE = "Deposit";

    @Override
    public TransactionDto convert(Deposit source) {
        return TransactionDto.builder()
                .id(source.getId())
                .amount(source.getAmount())
                .cardId(source.getCard().getId())
                .cardNumber(source.getCard().getCardNumber())
                .transactionType(DEPOSIT_TRANSACTION_TYPE)
                .createdDate(source.getCreatedDate())
                .isSuspicious(source.isSuspicious())
                .isSuccessful(source.isSuccessful())
                .build();
    }
}
