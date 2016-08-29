package net.flanche.transactionapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main class of the application, initializes the spring context using Spring Boot and runs the application
 *
 * @author <a href="mailto:alex@flanche.net">Alex Dumitru</a>
 */
@SpringBootApplication
public class TransactionApplication {
    public static void main(String[] args) {
        SpringApplication.run(TransactionApplication.class, args);
    }
}
