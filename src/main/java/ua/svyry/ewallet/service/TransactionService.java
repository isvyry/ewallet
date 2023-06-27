package ua.svyry.ewallet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
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
import ua.svyry.ewallet.shared.CustomerDto;
import ua.svyry.ewallet.shared.TransactionDto;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionService {

    private final CardService cardService;
    private final ConversionService conversionService;
    private final CustomerService customerService;
    private final SuspiciousActivityService suspiciousActivityService;
    private final DepositRepository depositRepository;
    private final WithdrawalRepository withdrawalRepository;
    private final TransferRepository transferRepository;
    private final TransactionRepository transactionRepository;
    @Value("${transaction.withdrawal.daily.limit:5000}")
    private BigDecimal dailyWithdrawalLimit;
    @Value("${transaction.limit:2000}")
    private BigDecimal singleTransactionLimit;
    @Value("${transaction.suspicious.limit:10000}")
    private BigDecimal transactionSuspiciousAmountLimit;

    public TransactionDto depositFunds(TransactionDto transactionDetails, Authentication currentAuthentication) {
        Card card = cardService.getById(transactionDetails.getCardId());
        Customer owner = card.getWallet().getOwner();
        checkIsCurrentUser(owner, currentAuthentication);
        Deposit depositTransaction = new Deposit();
        BigDecimal transactionAmount = transactionDetails.getAmount();
        populateTransaction(depositTransaction, transactionAmount, card);
        boolean shouldBeSuccessful = !isAboveLimit(transactionAmount) &&
                suspiciousActivityService.isCustomerAllowedForTransactions(owner);
        if (shouldBeSuccessful) {
            cardService.depositFunds(card, transactionAmount);
            depositTransaction.setSuccessful(true);
        } else depositTransaction.setSuccessful(false);
        Deposit savedDeposit = depositRepository.save(depositTransaction);
        return conversionService.convert(savedDeposit, TransactionDto.class);
    }

    public TransactionDto withdrawFunds(TransactionDto transactionDetails, Authentication currentAuthentication) {
        Card card = cardService.getById(transactionDetails.getCardId());
        Customer owner = card.getWallet().getOwner();
        checkIsCurrentUser(owner, currentAuthentication);
        Withdrawal withdrawalTransaction = new Withdrawal();
        BigDecimal transactionAmount = transactionDetails.getAmount();
        populateTransaction(withdrawalTransaction, transactionAmount, card);
        boolean shouldBeSuccessful = !isAboveLimit(transactionAmount) &&
                suspiciousActivityService.isCustomerAllowedForTransactions(owner) &&
                !isAboveWithdrawalLimit(owner.getId());
        if (shouldBeSuccessful) {
            withdrawalTransaction.setSuccessful(cardService.withdrawFunds(card, transactionAmount));
        } else withdrawalTransaction.setSuccessful(false);
        Withdrawal savedWithdrawal = withdrawalRepository.save(withdrawalTransaction);
        return conversionService.convert(savedWithdrawal, TransactionDto.class);
    }

    public TransactionDto transferFunds(TransactionDto transactionDetails, Authentication currentAuthentication) {
        Card cardFrom = cardService.getById(transactionDetails.getCardId());
        Card cardTo = cardService.getById(transactionDetails.getReceiverCardId());
        Customer owner = cardFrom.getWallet().getOwner();
        checkIsCurrentUser(owner, currentAuthentication);
        Transfer transferTransaction = new Transfer();
        BigDecimal transactionAmount = transactionDetails.getAmount();
        populateTransaction(transferTransaction, transactionAmount, cardFrom, cardTo);
        boolean shouldBeSuccessful = !isAboveLimit(transactionAmount) &&
                suspiciousActivityService.isCustomerAllowedForTransactions(owner);
        if (shouldBeSuccessful) {
            transferTransaction.setSuccessful(cardService.transferFunds(cardFrom, cardTo, transactionAmount));
        } else transferTransaction.setSuccessful(false);
        Transfer savedTransfer = transferRepository.save(transferTransaction);
        return conversionService.convert(savedTransfer, TransactionDto.class);
    }

    private void checkIsCurrentUser(Customer cardOwner, Authentication currentUser) {
        CustomerDto currentUserDetails = customerService.getUserDetailsByUsername((String) currentUser.getPrincipal());
        if (currentUserDetails.getUserId().equals(cardOwner.getId())) {
            throw new AccessDeniedException("");
        }
    }

    private boolean isAboveWithdrawalLimit(Long customerId) {
        return transactionRepository.selectDailyTransactionsAmountSummedUpByCustomer(customerId)
                .compareTo(dailyWithdrawalLimit) == 1;
    }

    public int getLastHourSuspiciousTransactionsByCustomer(Customer customer) {
        return transactionRepository.countAllSuspiciousByCustomerForTheLastHour(customer.getId());
    }

    private void populateTransaction(Transaction transaction, BigDecimal amount, Card card) {
        transaction.setAmount(amount);
        transaction.setCard(card);
        transaction.setSuspicious(isTransactionSuspicious(amount, card));
    }

    private void populateTransaction(Transfer transaction, BigDecimal amount, Card from, Card to) {
        transaction.setAmount(amount);
        transaction.setCard(from);
        transaction.setReceiverCard(to);
        transaction.setSuspicious(isTransactionSuspicious(amount, from));
    }

    private boolean isTransactionSuspicious(BigDecimal amount, Card card) {
        if (amount.compareTo(transactionSuspiciousAmountLimit) == 1) {
            suspiciousActivityService.checkSuspiciousActivity(card);
            return true;
        } else return false;
    }

    private boolean isAboveLimit(BigDecimal amount) {
        if (amount.compareTo(singleTransactionLimit) == 1) {
            return true;
        } else return false;
    }

}
