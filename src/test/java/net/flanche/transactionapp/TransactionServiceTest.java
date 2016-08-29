package net.flanche.transactionapp;

import net.flanche.transactionapp.transaction.domain.Transaction;
import net.flanche.transactionapp.transaction.service.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Performs unit tests for the transaction service by testing each of the public methods
 *
 * @author <a href="mailto:alex@flanche.net">Alex Dumitru</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TransactionApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Test
    public void testSaveTransaction() throws DuplicateTransactionException, InvalidTransactionType, InvalidParentTransaction {
        transactionService.saveTransaction(1, "some-type", new BigDecimal("5500.50"));
        Assert.assertTrue(transactionService.getTransactionById(1).isPresent());
        Assert.assertEquals(transactionService.getTransactionById(1).get().getId(), 1);
        Assert.assertEquals(transactionService.getTransactionById(1).get().getType(), "some-type");
        Assert.assertEquals(transactionService.getTransactionById(1).get().getAmount(), new BigDecimal("5500.50"));

        transactionService.saveTransaction(2, "some-type", new BigDecimal("5500.50"), 1);
        Assert.assertTrue(transactionService.getTransactionById(2).isPresent());
        Assert.assertTrue(transactionService.getTransactionById(2).get().getParentTransaction().isPresent());
        Assert.assertEquals(transactionService.getTransactionById(2).get().getParentTransaction().get().getId(), 1);

        try {
            transactionService.saveTransaction(3, "some-type", new BigDecimal("4500.50"), 4);
            Assert.fail("Saved a transaction with an invalid parent id");
        } catch (InvalidParentTransaction e) {
            // correct exception
        }

        try {
            transactionService.saveTransaction(3, null, new BigDecimal("4500.50"));
            Assert.fail("Saved a transaction with an invalid type");
        } catch (InvalidTransactionType e) {
            // correct exception
        }
    }


    @Test
    public void testGetTransaction() throws DuplicateTransactionException, InvalidTransactionType, InvalidParentTransaction {
        transactionService.saveTransaction(1, "some-type", new BigDecimal("5500.50"));
        Assert.assertTrue(transactionService.getTransactionById(1).isPresent());
        Assert.assertEquals(transactionService.getTransactionById(1).get().getId(), 1);
        Assert.assertEquals(transactionService.getTransactionById(1).get().getType(), "some-type");
        Assert.assertEquals(transactionService.getTransactionById(1).get().getAmount(), new BigDecimal("5500.50"));
    }

    @Test
    public void testGetTransactionsOfType() throws DuplicateTransactionException, InvalidTransactionType {
        transactionService.saveTransaction(1, "type1", new BigDecimal("5500.50"));
        transactionService.saveTransaction(2, "type2", new BigDecimal("5500.50"));
        transactionService.saveTransaction(3, "type1", new BigDecimal("5500.50"));
        transactionService.saveTransaction(4, "type2", new BigDecimal("5500.50"));
        transactionService.saveTransaction(5, "type3", new BigDecimal("5500.50"));
        transactionService.saveTransaction(6, "type1", new BigDecimal("5500.50"));
        //test the invalid type
        try {
            transactionService.getTransactionsOfType("");
            Assert.fail("An invalid type was supplied to TransactionService::getTransactionsOfType and no exception was thrown");
        } catch (InvalidTransactionType e) {
            // correct exception
        }

        // test a type that has no transactions
        Assert.assertTrue(transactionService.getTransactionsOfType("type5").isEmpty());

        // normal test case 1
        List<Long> result = transactionService.getTransactionsOfType("type1").stream()
                .map(Transaction::getId)
                .sorted()
                .collect(Collectors.toList());
        Long[] correctResult = {1L, 3L, 6L};
        Assert.assertArrayEquals(result.toArray(new Long[0]), correctResult);

        // normal test case 2
        result = transactionService.getTransactionsOfType("type2").stream()
                .map(Transaction::getId)
                .sorted()
                .collect(Collectors.toList());
        Long[] secondCorrectResult = {2L, 4L};
        Assert.assertArrayEquals(result.toArray(new Long[0]), secondCorrectResult);
    }

    @Test
    public void testGetTotalAmountOfChildrenTransactions() throws DuplicateTransactionException, InvalidTransactionType, InvalidParentTransaction, InvalidTransactionId {
        transactionService.saveTransaction(1, "some-type", new BigDecimal("10"));
        transactionService.saveTransaction(2, "some-type", new BigDecimal("20"), 1);
        transactionService.saveTransaction(3, "some-other-type", new BigDecimal("100"), 2);
        transactionService.saveTransaction(4, "some-other-type", new BigDecimal("150"), 2);
        transactionService.saveTransaction(5, "some-other-type", new BigDecimal("40"), 1);
        transactionService.saveTransaction(6, "some-other-type", new BigDecimal("90"), 5);
        transactionService.saveTransaction(7, "some-other-type", new BigDecimal("140"), 2);
        transactionService.saveTransaction(8, "some-other-type", new BigDecimal("230"), 7);

        //normal test cases for different branches of the tree
        Assert.assertEquals(transactionService.getTotalAmountOfChildrenTransactions(1), new BigDecimal("780"));
        Assert.assertEquals(transactionService.getTotalAmountOfChildrenTransactions(8), new BigDecimal("230d"));
        Assert.assertEquals(transactionService.getTotalAmountOfChildrenTransactions(5), new BigDecimal("130d"));
        Assert.assertEquals(transactionService.getTotalAmountOfChildrenTransactions(2), new BigDecimal("640"));

        //try with an invalid id
        try {
            transactionService.getTotalAmountOfChildrenTransactions(23);
            Assert.fail("The TransactionService::getTotalAmountOfChildrenTransactions worked with an invalid id.");
        } catch (InvalidTransactionId e) {
            // test passed
        }
    }
}

