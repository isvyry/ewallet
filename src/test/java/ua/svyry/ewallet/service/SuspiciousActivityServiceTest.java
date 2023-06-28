package ua.svyry.ewallet.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import ua.svyry.ewallet.entity.Card;
import ua.svyry.ewallet.entity.Customer;
import ua.svyry.ewallet.entity.Wallet;

import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class SuspiciousActivityServiceTest {

    ObjectProvider<TransactionService> transactionServiceObjectProvider = mock(ObjectProvider.class);
    TransactionService transactionService = mock(TransactionService.class);

    SuspiciousActivityService service = new SuspiciousActivityService(transactionServiceObjectProvider, 5, 10);

    @ParameterizedTest(name = "index: expect customer blocked: {1}, suspicious: {2} when they have {0} suspicious transactions in DB")
    @MethodSource
    public void testCheckSuspiciousActivity(int queryResult,
                                            boolean customerBlockedForTransactionsExpected,
                                            boolean customerSuspiciousExpected) {
        Customer owner = Customer.builder()
                .id(1l)
                .build();

        Card card = Card.builder()
                .id(1l)
                .wallet(Wallet.builder()
                        .id(1l)
                        .owner(owner)
                        .build())
                .build();

        when(transactionServiceObjectProvider.getIfAvailable()).thenReturn(transactionService);
        when(transactionService.getLastHourSuspiciousTransactionsByCustomer(owner)).thenReturn(queryResult);

        service.checkSuspiciousActivity(card);

        assertEquals(customerBlockedForTransactionsExpected, owner.isBlockedForTransactions());
        assertEquals(customerSuspiciousExpected, owner.isSuspicious());
    }

    @ParameterizedTest(name = "{index}: expect {0} when customer blocked state is {1}")
    @CsvSource({"false,true", "true,false"})
    public void testICustomerAllowedForTransactions(boolean expected, boolean customerBlockedForTransactions) {
        Customer owner = Customer.builder()
                .id(1l)
                .isBlockedForTransactions(customerBlockedForTransactions)
                .build();

        boolean result = service.isCustomerAllowedForTransactions(owner);

        assertEquals(expected, result);
    }

    private static Stream<Arguments> testCheckSuspiciousActivity() {
        return Stream.of(
                Arguments.of(2, false, false),
                Arguments.of(4, false, true),
                Arguments.of(5, false, true),
                Arguments.of(9, true, true),
                Arguments.of(15, true, true));
    }
}
