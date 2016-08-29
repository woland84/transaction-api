package net.flanche.transactionapp.transaction.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model class that describes the transaction in the API format
 *
 * @author <a href="mailto:alex@flanche.net">Alex Dumitru</a>
 */
public class TransactionPutResponse {
    @JsonProperty("type")
    private String type;
    @JsonProperty("amount")
    private double amount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("parent_id")
    private long parentId;

    /**
     * Constructor for the class
     *
     * @param type     the type of the transaction
     * @param amount   the amount of the transaction
     * @param parentId the id of the parent of the transaction
     */
    public TransactionPutResponse(String type, double amount, long parentId) {
        this.type = type;
        this.amount = amount;
        this.parentId = parentId;
    }

    /**
     * Constructor for the class
     *
     * @param type   the type of the transaction
     * @param amount the amount of the transaction
     */
    public TransactionPutResponse(String type, double amount) {
        this.type = type;
        this.amount = amount;
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
     * Getter for the amount attribute
     *
     * @return the amount of the transaction
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Getter for the parent id attribute
     *
     * @return the parent if od the transaction
     */
    public long getParentId() {
        return parentId;
    }
}
