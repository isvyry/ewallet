package ua.svyry.ewallet.exception;

public class TransactionBlockedException extends RuntimeException {
    public TransactionBlockedException(String message) {
        super(message);
    }
}
