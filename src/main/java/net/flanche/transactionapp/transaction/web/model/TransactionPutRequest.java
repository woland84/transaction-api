package net.flanche.transactionapp.transaction.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

/**
 * Model class to describe the transaction input in our REST api
 *
 * @author <a href="mailto:alex@flanche.net">Alex Dumitru</a>
 */
public class TransactionPutRequest {
    @JsonProperty(value = "amount", required = true)
    private String amount;
    @JsonProperty(value = "type", required = true)
    private String type;
    @JsonProperty(value = "parent_id")
    private Long parentId;

    public TransactionPutRequest() {
    }

    /**
     * Getter for the amount attribute
     *
     * @return the amount of this transaction
     */
    public String getAmount() {
        return amount;
    }

    /**
     * Getter for the type attribute
     *
     * @return the type of this transaction
     */
    public String getType() {
        return type;
    }

    /**
     * Getter for the parentId attribute
     *
     * @return the parent id of this transaction if any, otherwise Empty is returned
     */
    public Optional<Long> getParentId() {
        return Optional.ofNullable(parentId);
    }
}
