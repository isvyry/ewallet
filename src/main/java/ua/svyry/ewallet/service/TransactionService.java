package ua.svyry.ewallet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.svyry.ewallet.entity.Customer;
import ua.svyry.ewallet.entity.Card;
import ua.svyry.ewallet.entity.Transfer;
import ua.svyry.ewallet.entity.Deposit;
import ua.svyry.ewallet.entity.Withdrawal;
import ua.svyry.ewallet.entity.Transaction;
import ua.svyry.ewallet.repository.DepositRepository;
import ua.svyry.ewallet.repository.TransactionRepository;
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
    private final SuspiciousActivityService suspiciousActivityService;
    private final DepositRepository depositRepository;
    private final WithdrawalRepository withdrawalRepository;
    private final TransferRepository transferRepository;
    private final TransactionRepository transactionRepository;

    public TransactionDto depositFunds(TransactionDto transactionDetails) {
        Card card = cardService.getById(transactionDetails.getCardId());
        Deposit depositTransaction = new Deposit();
        BigDecimal transactionAmount = transactionDetails.getAmount();
        populateTransaction(depositTransaction, transactionAmount, card);
        boolean shouldBeSuccessful = !isAboveLimit(transactionAmount) &&
                suspiciousActivityService.isCustomerAllowedForTransactions(card.getWallet().getOwner());
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
        populateTransaction(withdrawalTransaction, transactionAmount, card);
        boolean shouldBeSuccessful = !isAboveLimit(transactionAmount) &&
                suspiciousActivityService.isCustomerAllowedForTransactions(card.getWallet().getOwner());
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
        populateTransaction(transferTransaction, transactionAmount, cardFrom, cardTo);
        boolean shouldBeSuccessful = !isAboveLimit(transactionAmount) &&
                suspiciousActivityService.isCustomerAllowedForTransactions(cardFrom.getWallet().getOwner());
        if (shouldBeSuccessful) {
            transferTransaction.setSuccessful(cardService.transferFunds(cardFrom, cardTo, transactionAmount));
        } else transferTransaction.setSuccessful(false);
        Transfer savedTransfer = transferRepository.save(transferTransaction);
        return conversionService.convert(savedTransfer, TransactionDto.class);
    }

    public int getLastHourSuspiciousTransactionsByCustomer(Customer customer) {
        return transactionRepository.countAllByCustomerForTheLastHour(customer.getId());
    }

    private void populateTransaction(Transaction transaction, BigDecimal amount, Card card) {
        transaction.setAmount(amount);
        transaction.setCard(card);
        transaction.setSuspicious(isTransactionSuspicious(amount, card));
    }

    private void populateTransaction(Transfer transaction, BigDecimal amount, Card from, Card to) {
        transaction.setAmount(amount);
        transaction.setCard(from);
        transaction.setReceiver(to);
        transaction.setSuspicious(isTransactionSuspicious(amount, from));
    }

    private boolean isTransactionSuspicious(BigDecimal amount, Card card) {
        if (amount.compareTo(BigDecimal.valueOf(10000)) == 1) {
            suspiciousActivityService.checkSuspiciousActivity(card);
            return true;
        } else return false;
    }

    private boolean isAboveLimit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.valueOf(2000)) == 1) {
            return true;
        } else return false;
    }

}
