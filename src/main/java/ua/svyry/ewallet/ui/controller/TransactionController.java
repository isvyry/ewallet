package ua.svyry.ewallet.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.svyry.ewallet.service.TransactionService;
import ua.svyry.ewallet.shared.TransactionDto;
import ua.svyry.ewallet.ui.model.CreateTransactionRequestModel;
import ua.svyry.ewallet.ui.model.CreateTransferTransactionRequestModel;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;
    private final ConversionService conversionService;

    @PostMapping("/deposit")
    public ResponseEntity deposit(@RequestBody CreateTransactionRequestModel createTransactionRequestModel) {
        TransactionDto transactionDetails = conversionService
                .convert(createTransactionRequestModel, TransactionDto.class);
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.depositFunds(transactionDetails));
    }
    @PostMapping("/transfer")
    public ResponseEntity transfer(@RequestBody CreateTransferTransactionRequestModel createTransferRequestModel) {
        TransactionDto transactionDetails = conversionService
                .convert(createTransferRequestModel, TransactionDto.class);
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.transferFunds(transactionDetails));
    }
    @PostMapping("/withdraw")
    public ResponseEntity withdraw(@RequestBody CreateTransactionRequestModel createTransactionRequestModel) {
        TransactionDto transactionDetails = conversionService
                .convert(createTransactionRequestModel, TransactionDto.class);
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.withdrawFunds(transactionDetails));
    }
}
