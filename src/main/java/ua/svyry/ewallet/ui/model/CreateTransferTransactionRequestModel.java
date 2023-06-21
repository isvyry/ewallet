package ua.svyry.ewallet.ui.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransferTransactionRequestModel {

    @NotNull(message = "amount should not be provided")
    @Positive(message = "transfer amount should be a positive value")
    private BigDecimal amount;

    @NotNull(message = "cardId should be provided")
    private Long cardId;

    @NotNull(message = "receiverCardId should be provided")
    private Long receiverCardId;
}
