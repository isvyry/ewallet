package ua.svyry.ewallet.ui.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.svyry.ewallet.service.AuthenticationUtil;
import ua.svyry.ewallet.service.TransactionService;
import ua.svyry.ewallet.shared.TransactionDto;
import ua.svyry.ewallet.ui.model.CreateTransactionRequestModel;
import ua.svyry.ewallet.ui.model.CreateTransferTransactionRequestModel;
import ua.svyry.ewallet.ui.model.TransactionResult;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "Bearer Authentication")
public class TransactionController {

    private final TransactionService transactionService;
    private final ConversionService conversionService;
    private final AuthenticationUtil authenticationUtil;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResult> deposit(@RequestBody @Valid
                                                         CreateTransactionRequestModel
                                                                 createTransactionRequestModel) {
        TransactionDto transactionDetails = conversionService
                .convert(createTransactionRequestModel, TransactionDto.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(conversionService
                .convert(transactionService.depositFunds(transactionDetails,
                        authenticationUtil.getAuthentication()), TransactionResult.class));
    }
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResult> transfer(@RequestBody @Valid
                                                          CreateTransferTransactionRequestModel
                                                                  createTransferRequestModel) {
        TransactionDto transactionDetails = conversionService
                .convert(createTransferRequestModel, TransactionDto.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(conversionService
                .convert(transactionService.transferFunds(transactionDetails,
                        authenticationUtil.getAuthentication()), TransactionResult.class));
    }
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResult> withdraw(@RequestBody @Valid
                                                          CreateTransactionRequestModel
                                                                  createTransactionRequestModel) {
        TransactionDto transactionDetails = conversionService
                .convert(createTransactionRequestModel, TransactionDto.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(conversionService
                .convert(transactionService.withdrawFunds(transactionDetails,
                        authenticationUtil.getAuthentication()), TransactionResult.class));
    }

    
}
