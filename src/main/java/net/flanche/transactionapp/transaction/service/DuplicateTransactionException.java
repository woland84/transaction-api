package net.flanche.transactionapp.transaction.service;

/**
 * Exception thrown when the transaction id given clashes with an existing one
 *
 * @author <a href="mailto:alex@flanche.net">Alex Dumitru</a>
 */
public class DuplicateTransactionException extends Exception {
    public DuplicateTransactionException() {
    }

    public DuplicateTransactionException(String message) {
        super(message);
    }
}
