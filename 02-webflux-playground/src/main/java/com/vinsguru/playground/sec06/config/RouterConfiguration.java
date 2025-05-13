package com.vinsguru.playground.sec06.config;

import com.vinsguru.playground.sec06.exceptions.CustomerNotFoundException;
import com.vinsguru.playground.sec06.exceptions.InvalidInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterConfiguration {

    @Autowired
    private CustomerRequestHandler customerRequestHandler;

    @Autowired
    private ApplicationExceptionHandler exceptionHandler;

    @Bean
    public RouterFunction<ServerResponse> customerRoutes() {
        return RouterFunctions.route()
                              .GET("/customers", this.customerRequestHandler::allCustomers)
                              .GET("/customers/paginated", this.customerRequestHandler::paginatedCustomers)
                              .GET("/customers/{id}", this.customerRequestHandler::getCustomer)
                              .POST("/customers", this.customerRequestHandler::saveCustomer)
                              .PUT("/customers/{id}", this.customerRequestHandler::updateCustomer)
                              .DELETE("/customers/{id}", this.customerRequestHandler::deleteCustomer)
                              .onError(CustomerNotFoundException.class, this.exceptionHandler::handleException)
                              .onError(InvalidInputException.class, this.exceptionHandler::handleException)
                              .build();
    }


/*    @Bean
    public RouterFunction<ServerResponse> customerRoutes1() {
        return RouterFunctions.route()
                .path("customers", this::customerRoutes2) // Delegates to customerRoutes2
                .POST("/customers", this.customerRequestHandler::saveCustomer)
                .PUT("/customers/{id}", this.customerRequestHandler::updateCustomer)
                .DELETE("/customers/{id}", this.customerRequestHandler::deleteCustomer)
                .onError(CustomerNotFoundException.class, this.exceptionHandler::handleException)
                .onError(InvalidInputException.class, this.exceptionHandler::handleException)
                .build();
    }

    private RouterFunction<ServerResponse> customerRoutes2() {
        return RouterFunctions.route()
                .GET("/paginated", this.customerRequestHandler::paginatedCustomers)
                .GET("/{id}", this.customerRequestHandler::getCustomer)
                .GET("", this.customerRequestHandler::allCustomers)
                .build();
    }*/


}

