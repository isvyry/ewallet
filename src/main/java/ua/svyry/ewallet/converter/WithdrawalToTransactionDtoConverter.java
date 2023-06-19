package ua.svyry.ewallet.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ua.svyry.ewallet.entity.Withdrawal;
import ua.svyry.ewallet.shared.TransactionDto;

@Component
public class WithdrawalToTransactionDtoConverter implements Converter<Withdrawal, TransactionDto> {

    private final String WITHDRAWAL_TRANSACTION_TYPE = "Withdrawal";

    @Override
    public TransactionDto convert(Withdrawal source) {
        return TransactionDto.builder()
                .id(source.getId())
                .amount(source.getAmount())
                .cardId(source.getCard().getId())
                .cardNumber(source.getCard().getCardNumber())
                .transactionType(WITHDRAWAL_TRANSACTION_TYPE)
                .createdDate(source.getCreatedDate())
                .isSuspicious(source.isSuspicious())
                .isSuccessful(source.isSuccessful())
                .build();
    }
}