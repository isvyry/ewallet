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
public class TransactionDto {
    private Long id;
    private BigDecimal amount;
    private boolean isSuspicious;
    private Date createdDate;
    private Long cardId;
    private String cardNumber;
    private Long receiverCardId;
    private Long receiverCardNumber;
}
