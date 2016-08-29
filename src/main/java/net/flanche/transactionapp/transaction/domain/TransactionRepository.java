package net.flanche.transactionapp.transaction.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * A repository interface for all transactions. The interface is automatically
 * implemented by the Spring Data JPA framework.
 *
 * @author <a href="mailto:alex@flanche.net">Alex Dumitru</a>
 */
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    /**
     * Returns all transaction by a given type
     *
     * @param type the type to filter transactions for
     * @return all the transactions that fit the type criteria
     */
    List<Transaction> findByType(String type);

    /**
     * Finds all transactions that have as parent the given transaction
     *
     * @param parentTransaction the parent transaction to filter for
     * @return the children transactions of the given parent transactions
     */
    List<Transaction> findByParentTransaction(Transaction parentTransaction);
}
