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
public class CreateTransactionRequestModel {

    @NotNull(message = "amount should not be null")
    @Positive(message = "amount should be a positive number")
    private BigDecimal amount;

    @NotNull(message = "cardId should be provided")
    private Long cardId;
}
