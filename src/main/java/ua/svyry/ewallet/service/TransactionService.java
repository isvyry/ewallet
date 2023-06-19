package ua.svyry.ewallet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.svyry.ewallet.entity.Transfer;
import ua.svyry.ewallet.entity.Deposit;
import ua.svyry.ewallet.entity.Withdrawal;
import ua.svyry.ewallet.entity.Card;
import ua.svyry.ewallet.repository.DepositRepository;
import ua.svyry.ewallet.repository.TransferRepository;
import ua.svyry.ewallet.repository.WithdrawalRepository;
import ua.svyry.ewallet.shared.TransactionDto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionService {

    private final CardService cardService;
    private final ConversionService conversionService;
    private final DepositRepository depositRepository;
    private final WithdrawalRepository withdrawalRepository;
    private final TransferRepository transferRepository;

    public TransactionDto depositFunds(TransactionDto transactionDetails) {
        Card card = cardService.getById(transactionDetails.getCardId());
        Deposit depositTransaction = new Deposit();
        BigDecimal transactionAmount = transactionDetails.getAmount();
        depositTransaction.setAmount(transactionDetails.getAmount());
        depositTransaction.setCard(card);
        depositTransaction.setCreatedDate(new Date(Instant.now().toEpochMilli()));
        depositTransaction.setSuspicious(isTransactionSuspicious(transactionAmount));
        boolean shouldBeSuccessful = !isAboveLimit(transactionAmount);
        if (shouldBeSuccessful) {
            cardService.depositFunds(card, transactionAmount);
            depositTransaction.setSuccessful(true);
        } else depositTransaction.setSuccessful(false);
        Deposit savedDeposit = depositRepository.save(depositTransaction);
        return conversionService.convert(savedDeposit, TransactionDto.class);
    }

    public TransactionDto withdrawFunds(TransactionDto transactionDetails) {
        Card card = cardService.getById(transactionDetails.getCardId());
        Withdrawal withdrawalTransaction = new Withdrawal();
        BigDecimal transactionAmount = transactionDetails.getAmount();
        withdrawalTransaction.setAmount(transactionDetails.getAmount());
        withdrawalTransaction.setCard(card);
        withdrawalTransaction.setCreatedDate(new Date(Instant.now().toEpochMilli()));
        withdrawalTransaction.setSuspicious(isTransactionSuspicious(transactionAmount));
        boolean shouldBeSuccessful = !isAboveLimit(transactionAmount);
        if (shouldBeSuccessful) {
            withdrawalTransaction.setSuccessful(cardService.withdrawFunds(card, transactionAmount));
        } else withdrawalTransaction.setSuccessful(false);
        Withdrawal savedWithdrawal = withdrawalRepository.save(withdrawalTransaction);
        return conversionService.convert(savedWithdrawal, TransactionDto.class);
    }

    public TransactionDto transferFunds(TransactionDto transactionDetails) {
        Card cardFrom = cardService.getById(transactionDetails.getCardId());
        Card cardTo = cardService.getById(transactionDetails.getReceiverCardId());
        Transfer transferTransaction = new Transfer();
        BigDecimal transactionAmount = transactionDetails.getAmount();
        transferTransaction.setAmount(transactionDetails.getAmount());
        transferTransaction.setCard(cardFrom);
        transferTransaction.setReceiver(cardTo);
        transferTransaction.setCreatedDate(new Date(Instant.now().toEpochMilli()));
        transferTransaction.setSuspicious(isTransactionSuspicious(transactionAmount));
        boolean shouldBeSuccessful = !isAboveLimit(transactionAmount);
        if (shouldBeSuccessful) {
            transferTransaction.setSuccessful(cardService.transferFunds(cardFrom, cardTo, transactionAmount));
        } else transferTransaction.setSuccessful(false);
        Transfer savedTransfer = transferRepository.save(transferTransaction);
        return conversionService.convert(savedTransfer, TransactionDto.class);
    }

    private boolean isTransactionSuspicious(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.valueOf(10000)) == 1) {
            return true;
        } else return false;
    }

    private boolean isAboveLimit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.valueOf(2000)) == 1) {
            return true;
        } else return false;
    }

}
