package ua.svyry.ewallet.shared;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardDto {
    private Long id;
    private BigDecimal balance;
    private String cardNumber;
    private Date expirationDate;
    private Long walletId;

}
