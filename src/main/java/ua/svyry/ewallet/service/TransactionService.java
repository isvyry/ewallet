package ua.svyry.ewallet.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import ua.svyry.ewallet.shared.TransactionDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class TransactionService {

    public TransactionService(CardService cardService, ConversionService conversionService,
                              CustomerService customerService, AuthenticationUtil authenticationUtil,
                              SuspiciousActivityService suspiciousActivityService, DepositRepository depositRepository,
                              WithdrawalRepository withdrawalRepository, TransferRepository transferRepository,
                              TransactionRepository transactionRepository,
                              @Value("${transaction.withdrawal.daily.limit:5000}") BigDecimal dailyWithdrawalLimit,
                              @Value("${transaction.limit:2000}") BigDecimal singleTransactionLimit,
                              @Value("${transaction.suspicious.limit:10000}") BigDecimal transactionSuspiciousAmountLimit) {
        this.cardService = cardService;
        this.customerService = customerService;
        this.conversionService = conversionService;
        this.authenticationUtil = authenticationUtil;
        this.suspiciousActivityService = suspiciousActivityService;
        this.depositRepository = depositRepository;
        this.withdrawalRepository = withdrawalRepository;
        this.transferRepository = transferRepository;
        this.transactionRepository = transactionRepository;
        this.dailyWithdrawalLimit = dailyWithdrawalLimit;
        this.singleTransactionLimit = singleTransactionLimit;
        this.transactionSuspiciousAmountLimit = transactionSuspiciousAmountLimit;
    }

    private final CardService cardService;
    private final CustomerService customerService;
    private final ConversionService conversionService;
    private final AuthenticationUtil authenticationUtil;
    private final SuspiciousActivityService suspiciousActivityService;
    private final DepositRepository depositRepository;
    private final WithdrawalRepository withdrawalRepository;
    private final TransferRepository transferRepository;
    private final TransactionRepository transactionRepository;
    private final BigDecimal dailyWithdrawalLimit;
    private final BigDecimal singleTransactionLimit;
    private final BigDecimal transactionSuspiciousAmountLimit;

    public TransactionDto depositFunds(TransactionDto transactionDetails, Authentication currentAuthentication) {
        Card card = cardService.getById(transactionDetails.getCardId());
        Customer owner = card.getWallet().getOwner();

        authenticationUtil.validateCustomerIsCurrentUser(owner, currentAuthentication);

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

        authenticationUtil.validateCustomerIsCurrentUser(owner, currentAuthentication);

        Withdrawal withdrawalTransaction = new Withdrawal();
        BigDecimal transactionAmount = transactionDetails.getAmount();
        populateTransaction(withdrawalTransaction, transactionAmount, card);

        boolean shouldBeSuccessful = !isAboveLimit(transactionAmount) &&
                suspiciousActivityService.isCustomerAllowedForTransactions(owner) &&
                !isAboveWithdrawalLimit(owner.getId(), card.getId(), transactionAmount);

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

        authenticationUtil.validateCustomerIsCurrentUser(owner, currentAuthentication);

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

    public List<TransactionDto> getAllTransactionsByCustomerId(Long customerId, Authentication currentAuthentication,
                                                               Pageable pageable) {
        Customer customer = customerService.getCustomerById(customerId);

        authenticationUtil.validateCustomerIsCurrentUser(customer, currentAuthentication);

        Page<Transaction> transactions = transactionRepository
                .findAllByCard_Wallet_OwnerAndCardIsDeletedIsFalse(customer, pageable);

        return transactions.stream().map(t -> conversionService.convert(t, TransactionDto.class))
                .collect(Collectors.toList());
    }

    public List<TransactionDto> getAllTransactionsByCardId(Long cardId, Authentication currentAuthentication,
                                                           Pageable pageable) {
        Card card = cardService.getById(cardId);

        authenticationUtil.validateCustomerIsCurrentUser(card.getWallet().getOwner(), currentAuthentication);

        Page<Transaction> transactions = transactionRepository.findAllByCard(card, pageable);

        return transactions.stream().map(t -> conversionService.convert(t, TransactionDto.class))
                .collect(Collectors.toList());
    }

    public int getLastHourSuspiciousTransactionsByCustomer(Customer customer) {
        return transactionRepository.countAllSuspiciousByCustomerForTheLastHour(customer.getId());
    }

    private boolean isAboveWithdrawalLimit(Long customerId, Long cardId, BigDecimal transactionAmount) {
        BigDecimal doneWithdrawalSum = transactionRepository
                .selectDailyTransactionsAmountSummedUpByCustomerAndByCard(customerId, cardId)
                .orElse(BigDecimal.ZERO);
        BigDecimal totalWithdrawalSum = doneWithdrawalSum.add(transactionAmount);
        if (totalWithdrawalSum.compareTo(dailyWithdrawalLimit) == 1) {
            log.warn(String.format("Withdrawal is unsuccessful from Card[id = %s]. Today's summed withdrawal amount: %s, " +
                            "current transaction amount: %s. Daily withdrawal limit: %s", cardId, doneWithdrawalSum,
                    transactionAmount, dailyWithdrawalLimit));
            return true;
        } else {
            return false;
        }
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
