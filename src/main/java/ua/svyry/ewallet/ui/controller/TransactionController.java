package ua.svyry.ewallet.ui.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ua.svyry.ewallet.service.AuthenticationUtil;
import ua.svyry.ewallet.service.TransactionService;
import ua.svyry.ewallet.shared.TransactionDto;
import ua.svyry.ewallet.ui.model.CreateTransactionRequestModel;
import ua.svyry.ewallet.ui.model.CreateTransferTransactionRequestModel;
import ua.svyry.ewallet.ui.model.TransactionResult;

import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<List<TransactionResult>> getAllByCustomerId(@PathVariable Long customerId,
                                                                   @RequestParam(defaultValue = "0")
                                                                   Integer pageNumber,
                                                                   @RequestParam(defaultValue = "20")
                                                                   Integer pageSize) {
        List<TransactionResult> transactions = transactionService.getAllTransactionsByCustomerId(customerId,
                authenticationUtil.getAuthentication(), PageRequest.of(pageNumber, pageSize))
                        .stream().map(transaction -> conversionService.convert(transaction, TransactionResult.class))
                        .collect(Collectors.toList());
        return ResponseEntity.ok(transactions);

    }

    @GetMapping("/cards/{cardId}")
    public ResponseEntity<List<TransactionResult>> getAllByCardId(@PathVariable Long cardId,
                                                                   @RequestParam(defaultValue = "0")
                                                                   Integer pageNumber,
                                                                   @RequestParam(defaultValue = "20")
                                                                   Integer pageSize) {
        List<TransactionResult> transactions = transactionService.getAllTransactionsByCardId(cardId,
                        authenticationUtil.getAuthentication(), PageRequest.of(pageNumber, pageSize))
                .stream().map(transaction -> conversionService.convert(transaction, TransactionResult.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactions);

    }
    
}
