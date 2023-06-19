package ua.svyry.ewallet.ui.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class TransactionResult {
    private Long id;
    private BigDecimal amount;
    private boolean isSuspicious;
    private boolean isSuccessful;
    private Date createdDate;
    private Long cardId;
    private String cardNumber;
    private Long receiverCardId;
    private String receiverCardNumber;
    private String transactionType;
}
