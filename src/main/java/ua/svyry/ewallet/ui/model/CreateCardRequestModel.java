package ua.svyry.ewallet.ui.model;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.CreditCardNumber;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCardRequestModel {

    @NotNull(message = "lastName should not be null")
    @PositiveOrZero(message = "balance should not be a negative number")
    private BigDecimal balance;
    @NotNull(message = "cardNumber should not be null")
    @CreditCardNumber(message = "cardNumber should not contain any non-digit character")
    private String cardNumber;
    @FutureOrPresent(message = "expiration date should not be in past")
    @NotNull(message = "expiration date should not be null")
    private Date expirationDate;
    @NotNull(message = "walletId should be provided")
    private Long walletId;
}
