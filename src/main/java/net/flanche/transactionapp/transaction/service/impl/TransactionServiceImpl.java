package net.flanche.transactionapp.transaction.service.impl;

import net.flanche.transactionapp.transaction.domain.Transaction;
import net.flanche.transactionapp.transaction.domain.TransactionRepository;
import net.flanche.transactionapp.transaction.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

/**
 * Implementation of the TransactionService interface
 *
 * @author <a href="mailto:alex@flanche.net">Alex Dumitru</a>
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Complexity with current implementation is O(1) as primary keys will use a hash index in most database.
     * With a non-jpa approach we would keep a repository with a HashMap container that would give us a similar complexity
     */
    @Override
    public Optional<Transaction> getTransactionById(long id) {
        return Optional.ofNullable(transactionRepository.findOne(id));
    }

    /**
     * Complexity of current implementation is O(n) as with the current schema we would have to go through all the nodes
     * A hash index would be indicated here as we always check for equality giving us a O(1) complexity.
     * With a non-jpa approach we would again get O(n) in the naive approach. Alternatively we could keep one
     * more HashMap container with the key being type in addition to our id one. This would again get us to O(1).
     */
    @Override
    public List<Transaction> getTransactionsOfType(String type) throws InvalidTransactionType {
        if (type == null || type.equals("")) {
            throw new InvalidTransactionType();
        }
        return transactionRepository.findByType(type);
    }

    /**
     * @see TransactionServiceImpl::sumOfTransactionTree discussion for complexity
     */
    @Override
    public BigDecimal getTotalAmountOfChildrenTransactions(long transactionId) throws InvalidTransactionId {
        Optional<Transaction> transaction = Optional.ofNullable(transactionRepository.findOne(transactionId));
        if (!transaction.isPresent()) {
            throw new InvalidTransactionId();
        }
        return sumOfTransactionTree(transaction.get());
    }


    /**
     * This is a O(1) operation no matter our approach
     */
    @Override
    public Transaction saveTransaction(long transactionId, String type, BigDecimal amount) throws DuplicateTransactionException, InvalidTransactionType {
        checkTransactionValidity(transactionId, type);
        Transaction transaction = new Transaction(transactionId, amount, type);
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction saveTransaction(long transactionId, String type, BigDecimal amount, long parentTransactionId) throws InvalidParentTransaction, DuplicateTransactionException, InvalidTransactionType {
        checkTransactionValidity(transactionId, type);
        Optional<Transaction> parentTransaction = getTransactionById(parentTransactionId);
        if (!parentTransaction.isPresent()) {
            throw new InvalidParentTransaction();
        }
        Transaction transaction = new Transaction(transactionId, amount, type, parentTransaction.get());
        return transactionRepository.save(transaction);
    }

    /**
     * Checks if the transaction is valid by checking if the id of the transaction is already in use and if the
     * type is valid (non-null, non-empty)
     */
    private void checkTransactionValidity(long transactionId, String type) throws DuplicateTransactionException, InvalidTransactionType {
        if (transactionRepository.findOne(transactionId) != null) {
            throw new DuplicateTransactionException();
        }
        if (type == null || type.equals("")) {
            throw new InvalidTransactionType();
        }

    }

    /**
     * <p>
     * Returns the sum of all transactions linked transitively through a is-child relationship to this transaction
     * The function simulates a depth-first-search through the tree of children thus having a complexity of O(n) * P
     * P is the complexity of retrieving the children of the current node. In our case, as we use JPA to handle this for us
     * the complexity would depend on the index in the table (e.g. using a Hash index would give us a O(1) complexity,
     * using a binary search tree would lead to a O(log(n)) complexity)
     * </p>
     * <p>
     * If I would have implemented the JPA methods myself, I would have just added one more container (HashMap) for each transaction
     * object that would keep track of the children of each transaction and update that on each save operation.
     * I found the JPA approach a bit more elegant and in the spirit of the Spring Framework that I used for the rest
     * of the application.
     * </p>
     *
     * @param transaction the transaction to calculate the sum for
     * @return the sum of the transaction tree
     */
    private BigDecimal sumOfTransactionTree(Transaction transaction) {
        BigDecimal sum = transaction.getAmount();
        Stack<Transaction> transactionsToProcess = new Stack<>();
        transactionsToProcess.addAll(transactionRepository.findByParentTransaction(transaction));
        while (!transactionsToProcess.empty()) {
            Transaction currentTransaction = transactionsToProcess.pop();
            sum = sum.add(currentTransaction.getAmount());
            transactionsToProcess.addAll(transactionRepository.findByParentTransaction(currentTransaction));
        }
        return sum;
    }

}
