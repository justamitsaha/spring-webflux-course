package com.vinsguru.playground.sec04.controller;

import com.vinsguru.playground.sec04.dto.CustomerDto;
import com.vinsguru.playground.sec04.exceptions.ApplicationExceptions;
import com.vinsguru.playground.sec04.service.CustomerService;
import com.vinsguru.playground.sec04.validator.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public Flux<CustomerDto> allCustomers() {
        return this.customerService.getAllCustomers();
    }

    @GetMapping("paginated")
    public Mono<List<CustomerDto>> allCustomers(@RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "3") Integer size) {
        return this.customerService.getAllCustomers(page, size)
                                   .collectList();
    }

    @GetMapping("{id}")
    public Mono<CustomerDto> getCustomer(@PathVariable Integer id) {
        return this.customerService.getCustomerById(id)
                                   .switchIfEmpty(ApplicationExceptions.customerNotFound(id));  // Error handling
    }

    @PostMapping
    public Mono<CustomerDto> saveCustomer(@RequestBody Mono<CustomerDto> mono) {
        return mono.transform(RequestValidator.validate())
                   .as(this.customerService::saveCustomer);
    }

    @PutMapping("{id}")
    public Mono<CustomerDto> updateCustomer(@PathVariable Integer id, @RequestBody Mono<CustomerDto> mono) {
        return mono.transform(RequestValidator.validate())      //Validate customer
                   .as(validReq -> this.customerService.updateCustomer(id, validReq))
                   .switchIfEmpty(ApplicationExceptions.customerNotFound(id));  // Throw error if validation fails
    }

    @DeleteMapping("{id}")
    public Mono<Void> deleteCustomer(@PathVariable Integer id) {
        return this.customerService.deleteCustomerById(id)
                                   .filter(b -> b)
                                    //Throw error if customer not found
                                   .switchIfEmpty(ApplicationExceptions.customerNotFound(id))
                                   .then();
    }

}
