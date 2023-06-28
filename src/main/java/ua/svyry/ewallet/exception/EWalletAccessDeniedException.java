package ua.svyry.ewallet.exception;

public class EWalletAccessDeniedException extends RuntimeException {
    public EWalletAccessDeniedException(String message) {
        super(message);
    }
}
