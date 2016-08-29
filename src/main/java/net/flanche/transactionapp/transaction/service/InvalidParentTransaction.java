package net.flanche.transactionapp.transaction.service;

/**
 * Exception to be thrown when an invalid parent transaction is given
 *
 * @author <a href="mailto:alex@flanche.net">Alex Dumitru</a>
 */
public class InvalidParentTransaction extends Exception {
    public InvalidParentTransaction() {
    }

    public InvalidParentTransaction(String message) {
        super(message);
    }
}
