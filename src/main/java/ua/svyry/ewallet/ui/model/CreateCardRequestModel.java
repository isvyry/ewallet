package ua.svyry.ewallet.ui.model;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCardRequestModel {

    @NotNull
    private BigDecimal balance;
    @NotNull
    private String cardNumber;
    @FutureOrPresent
    @NotNull
    private Date expirationDate;
    @NotNull
    private Long walletId;
}
