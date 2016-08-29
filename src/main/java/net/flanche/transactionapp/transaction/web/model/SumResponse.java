package net.flanche.transactionapp.transaction.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model class to describe the response to a sum request
 *
 * @author <a href="mailto:alex@flanche.net">Alex Dumitru</a>
 */
public class SumResponse {
    @JsonProperty("sum")
    private double sum;

    /**
     * Constructor for the class
     *
     * @param sum the sum of the transactions
     */
    public SumResponse(double sum) {
        this.sum = sum;
    }

    /**
     * Getter for the sum attribute
     *
     * @return the sum of the transactions
     */
    public double getSum() {
        return sum;
    }
}
