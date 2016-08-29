package net.flanche.transactionapp;

import net.flanche.transactionapp.transaction.service.TransactionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Test for the rest API. For each URL that we expose in the TransactionController we define a test method here
 *
 * @author <a href="mailto:alex@flanche.net">Alex Dumitru</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TransactionApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@WebAppConfiguration
public class RestApiTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private TransactionService transactionService;
    private MockMvc mockMvc;

    @Before
    public void before() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testPut() throws Exception {
        // Normal request
        String jsonRequest = "{ \n" +
                "\"amount\": 5000, \n" +
                "\"type\": \"cars\"\n" +
                "}";
        mockMvc.perform(put("/transactionservice/transaction/1").content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(5000d)))
                .andExpect(jsonPath("$.type", is("cars")));

        //Normal request with parent
        String jsonRequestWithParent = "{ \n" +
                "\"amount\": 1200, \n" +
                "\"type\": \"parts\",\n" +
                "\"parent_id\": 1\n" +
                "}";
        mockMvc.perform(put("/transactionservice/transaction/2").content(jsonRequestWithParent).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(1200d)))
                .andExpect(jsonPath("$.type", is("parts")))
                .andExpect(jsonPath("$.parent_id", is(1)));

        //Omit type, expect error
        String jsonWithoutType = "{ \n" +
                "\"amount\": 5000, \n" +
                "}";
        mockMvc.perform(put("/transactionservice/transaction/3").content(jsonWithoutType).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //Duplicate id, expect error
        mockMvc.perform(put("/transactionservice/transaction/1").content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

    }

    @Test
    public void testGet() throws Exception {
        // Typical request
        transactionService.saveTransaction(1, "some-type", new BigDecimal("5500.50"));
        mockMvc.perform(get("/transactionservice/transaction/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(5500.50)))
                .andExpect(jsonPath("$.type", is("some-type")));

        // Request with parent
        transactionService.saveTransaction(2, "some-other-type", new BigDecimal("4000"), 1);
        mockMvc.perform(get("/transactionservice/transaction/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(4000d)))
                .andExpect(jsonPath("$.type", is("some-other-type")))
                .andExpect(jsonPath("$.parent_id", is(1)));

        // Check with a transaction that does not exist
        mockMvc.perform(get("/transactionservice/transaction/3"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testTypes() throws Exception {
        // Typical request, add 3 transactions, 2 of some type, one of some other, check if the ids match
        transactionService.saveTransaction(1, "some-type", new BigDecimal("5500.50"));
        transactionService.saveTransaction(2, "some-type", new BigDecimal("5500.50"));
        transactionService.saveTransaction(3, "some-other-type", new BigDecimal("5500.50"));

        mockMvc.perform(get("/transactionservice/types/some-type"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", is(1)))
                .andExpect(jsonPath("$[1]", is(2)));

        mockMvc.perform(get("/transactionservice/types/some-other-type"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]", is(3)));
    }

    @Test
    public void testSum() throws Exception {
        transactionService.saveTransaction(1, "some-type", new BigDecimal("10"));
        transactionService.saveTransaction(2, "some-type", new BigDecimal("20"), 1);
        transactionService.saveTransaction(3, "some-other-type", new BigDecimal("100"), 2);
        transactionService.saveTransaction(4, "some-other-type", new BigDecimal("150"), 2);
        transactionService.saveTransaction(5, "some-other-type", new BigDecimal("40"), 1);
        transactionService.saveTransaction(6, "some-other-type", new BigDecimal("90"), 5);
        transactionService.saveTransaction(7, "some-other-type", new BigDecimal("140"), 2);
        transactionService.saveTransaction(8, "some-other-type", new BigDecimal("230"), 7);

        mockMvc.perform(get("/transactionservice/sum/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum", is(780d)));

        mockMvc.perform(get("/transactionservice/sum/8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum", is(230d)));

        mockMvc.perform(get("/transactionservice/sum/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum", is(130d)));

        mockMvc.perform(get("/transactionservice/sum/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum", is(640d)));

        mockMvc.perform(get("/transactionservice/sum/17"))
                .andExpect(status().isNotFound());

    }
}
