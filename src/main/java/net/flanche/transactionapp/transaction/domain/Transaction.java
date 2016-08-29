package net.flanche.transactionapp.transaction.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Domain class to represent a transaction
 *
 * @author <a href="mailto:alex@flanche.net">Alex Dumitru</a>
 */
@Entity
public class Transaction {
    @Id
    private long id;
    private BigDecimal amount;
    private String type;
    @ManyToOne(fetch = FetchType.EAGER)
    private Transaction parentTransaction;


    /**
     * Constructor for the class
     *
     * @param id                the id of the transaction
     * @param amount            the amount used in this transaction
     * @param type              the type of the transaction
     * @param parentTransaction the parent of this transaction if any
     */
    public Transaction(long id, BigDecimal amount, String type, Transaction parentTransaction) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.parentTransaction = parentTransaction;
    }


    /**
     * Constructor for the class
     *
     * @param id     the id of the transaction
     * @param amount the amount used in this transaction
     * @param type   the type of the transaction
     */
    public Transaction(long id, BigDecimal amount, String type) {
        this.id = id;
        this.amount = amount;
        this.type = type;
    }

    /**
     * Constructor used by Hibernate
     */
    protected Transaction() {
    }


    /**
     * Getter for the id attribute
     *
     * @return the id of the transaction
     */
    public long getId() {
        return id;
    }

    /**
     * Getter for the amount attribute
     *
     * @return the amount of the transaction
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Getter for the type attribute
     *
     * @return the type of the transaction
     */
    public String getType() {
        return type;
    }

    /**
     * Getter for the parent transaction
     *
     * @return the parent transaction wrapped in an Optional
     */
    public Optional<Transaction> getParentTransaction() {
        return Optional.ofNullable(parentTransaction);
    }
}
