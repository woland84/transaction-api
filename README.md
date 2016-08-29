# Transaction API

 This implementation of the transaction spec is structured as a typical maven project. You can find the sources
under /src/main/java and tests under /src/main/test.

# Implementation

 The implementation revolves around the TransactionService that holds all the business logic needed for the application.
The REST API is contained in the TransactionController and only translates web requests to TransactionService calls
converting data when needed. 

The data is held in a Spring CRUD Repository, and we let the framework implement all the
filter methods for us. These methods could be easily implemented using a HashMap container 
with the transaction id as the key if a custom implementation was needed. I found the Spring JPA approach
a bit more elegant.

All the methods in the service implementation (TransactionServiceImpl) have a comment discussing the Big-O complexity
of the methods.

The implementation uses the Spring Framework to achieve the following tasks:
 - Spring Boot for configuring the project
 - Spring JPA for maintaining the data repository
 - Spring MVC to handle the web requests
 - Spring Test to test the web requests and internal services
 
# Testing

 The service is tested in the TransactionServiceTest using unit tests. All methods are tested covering all good
and invalid inputs.
 The REST API is tested in the RestApiTest and for each request mapping we test with good and invalid requests,
parse the responses and verify outputs.

# Running
 The code is runnable and deployable. Use the typical maven commands to check.
 mvn test - runs the service and REST API tests
 mvn package - generates a jar that you can start with java -jar package.jar