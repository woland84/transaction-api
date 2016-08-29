package net.flanche.transactionapp.transaction.web;

import net.flanche.transactionapp.transaction.domain.Transaction;
import net.flanche.transactionapp.transaction.service.*;
import net.flanche.transactionapp.transaction.web.model.SumResponse;
import net.flanche.transactionapp.transaction.web.model.TransactionPutRequest;
import net.flanche.transactionapp.transaction.web.model.TransactionPutResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for the transaction REST api
 *
 * @author <a href="mailto:alex@flanche.net">Alex Dumitru</a>
 */
@RestController
@RequestMapping("/transactionservice")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @RequestMapping(value = "/types/{type}", method = RequestMethod.GET)
    public ResponseEntity<List<Long>> findTransactionIdsOfType(@PathVariable("type") String type) {
        try {
            List<Long> outputList = transactionService.getTransactionsOfType(type).stream()
                    .map(Transaction::getId)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(outputList);

        } catch (InvalidTransactionType invalidTransactionType) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/sum/{transactionId}", method = RequestMethod.GET)
    public ResponseEntity<SumResponse> sumOfTransactions(@PathVariable("transactionId") long transactionId) {
        try {
            BigDecimal totalSum = transactionService.getTotalAmountOfChildrenTransactions(transactionId);
            return ResponseEntity.ok(new SumResponse(totalSum.doubleValue()));
        } catch (InvalidTransactionId e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/transaction/{transactionId}", method = RequestMethod.GET)
    public ResponseEntity<TransactionPutResponse> get(@PathVariable("transactionId") long transactionId) {
        Optional<Transaction> transaction = transactionService.getTransactionById(transactionId);
        if (transaction.isPresent()) {
            return ResponseEntity.ok(encodeTransaction(transaction.get()));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @RequestMapping(value = "/transaction/{transactionId}", method = RequestMethod.PUT)
    public ResponseEntity<TransactionPutResponse> put(@PathVariable("transactionId") long transactionId,
                                                      @Valid @RequestBody TransactionPutRequest transactionPutRequest) {
        Transaction transaction;
        BigDecimal amount = new BigDecimal(transactionPutRequest.getAmount());
        try {
            if (transactionPutRequest.getParentId().isPresent()) {
                transaction = transactionService.saveTransaction(transactionId, transactionPutRequest.getType(), amount, transactionPutRequest.getParentId().get());
            } else {
                transaction = transactionService.saveTransaction(transactionId, transactionPutRequest.getType(), amount);
            }
        } catch (DuplicateTransactionException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (InvalidTransactionType | InvalidParentTransaction invalidTransactionType) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(encodeTransaction(transaction));
    }

    /**
     * Encodes a transaction into an output transaction
     *
     * @param transaction the transaction to encode
     * @return a representation of the transaction in the output format
     */
    private TransactionPutResponse encodeTransaction(Transaction transaction) {
        if (transaction.getParentTransaction().isPresent()) {
            return new TransactionPutResponse(transaction.getType(), transaction.getAmount().doubleValue(),
                    transaction.getParentTransaction().get().getId());
        } else {
            return new TransactionPutResponse(transaction.getType(), transaction.getAmount().doubleValue());
        }
    }

}
