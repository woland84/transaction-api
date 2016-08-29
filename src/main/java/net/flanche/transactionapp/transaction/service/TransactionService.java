package net.flanche.transactionapp.transaction.service;

import net.flanche.transactionapp.transaction.domain.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * A service used for operation on transactions
 *
 * @author <a href="mailto:alex@flanche.net">Alex Dumitru</a>
 */
public interface TransactionService {

    /**
     * Returns a transaction given its id. If no transaction with that id
     * is available an empty optional object is returned
     *
     * @param id the id of the transaction
     * @return the transaction wrapped in an optional or empty if none could be found
     */
    Optional<Transaction> getTransactionById(long id);

    /**
     * Returns all the transactions of a given type
     *
     * @param type the type to filter transactions with
     * @return a list of transactions with the given type
     */
    List<Transaction> getTransactionsOfType(String type) throws InvalidTransactionType;

    /**
     * Returns the sum of all amounts belonging to transactions that
     * are transitively linked to the given transaction id
     *
     * @param transactionId the transaction id of the parent
     * @return the sum of all transactions that respect the filter
     */
    BigDecimal getTotalAmountOfChildrenTransactions(long transactionId) throws InvalidTransactionId;

    /**
     * Saves a transaction to be later used in the application
     *
     * @param transactionId the id of the transaction
     * @param type          the type of the transaction
     * @param amount        the amount used in the transaction
     * @return a valid transaction object
     */
    Transaction saveTransaction(long transactionId, String type, BigDecimal amount) throws DuplicateTransactionException, InvalidTransactionType;

    /**
     * Saves a transaction to be later used in the application
     *
     * @param transactionId       the id of the transaction
     * @param type                the type of the transaction
     * @param amount              the amount used in the transaction
     * @param parentTransactionId the id of the parent transaction
     * @return a valid transaction object
     */
    Transaction saveTransaction(long transactionId, String type, BigDecimal amount, long parentTransactionId) throws InvalidParentTransaction, DuplicateTransactionException, InvalidTransactionType;


}
