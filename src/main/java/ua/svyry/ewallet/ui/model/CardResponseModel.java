package ua.svyry.ewallet.ui.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardResponseModel {
    private Long id;
    private BigDecimal balance;
    private String cardNumber;

    private Date expirationDate;

    private Long walletId;
}
