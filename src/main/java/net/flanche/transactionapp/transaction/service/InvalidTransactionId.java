package net.flanche.transactionapp.transaction.service;

/**
 * Exception to be thrown when an invalid transaction id is passed to a service
 *
 * @author <a href="mailto:alex@flanche.net">Alex Dumitru</a>
 */
public class InvalidTransactionId extends Exception {

    public InvalidTransactionId() {
    }

    public InvalidTransactionId(String message) {
        super(message);
    }
}
