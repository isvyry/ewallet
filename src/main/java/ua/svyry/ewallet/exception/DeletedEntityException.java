package ua.svyry.ewallet.exception;

public class DeletedEntityException extends RuntimeException {
    public DeletedEntityException(String message) {
        super(message);
    }
}
