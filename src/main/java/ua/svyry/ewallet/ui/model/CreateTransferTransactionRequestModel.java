package ua.svyry.ewallet.ui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransferTransactionRequestModel {

    private BigDecimal amount;
    private Long cardId;
    private Long receiverCardNumber;
}
