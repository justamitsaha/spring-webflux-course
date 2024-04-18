package com.vinsguru.webfluxdemo.controller;

import com.vinsguru.webfluxdemo.dto.MultiplyRequestDto;
import com.vinsguru.webfluxdemo.dto.Response;
import com.vinsguru.webfluxdemo.service.ReactiveMathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.AbstractJackson2Encoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("reactive-math")
public class ReactiveMathController {

    @Autowired
    private ReactiveMathService mathService;

    @GetMapping("square/{input}")
    public Mono<Response> findSquare(@PathVariable int input) {
        return this.mathService.findSquare(input)
                .defaultIfEmpty(new Response(-1));
    }

    /*
    Although this endpoint is returning Flux, but to UI it will be returned as JSON,
    So unless the user receives entire response it can't do anything.
    In some browsers it will continuously give data, but it is still part of a JSON and UI can't do anything
    until entire JSON is received
    Since we are not defining that it produces stream Spring with help of AbstractJackson2Encoder is converting
    it to a list and retuning
    */
    @GetMapping("table/{input}")
    public Flux<Response> multiplicationTable(@PathVariable int input) {
        //AbstractJackson2Encoder is converting to Mono of List 
        return this.mathService.multiplicationTable(input);
    }

    /*
    This API returns a stream
     If we stop the request in browser we will see no logs in Server, that is the process will stop
    So the browser is subscribing to it, once the subscriptions ends the process on backend ends
     */
    @GetMapping(value = "table/{input}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Response> multiplicationTableStream(@PathVariable int input) {
        return this.mathService.multiplicationTable(input);
    }

    @PostMapping("multiply")
    public Mono<Response> multiply(@RequestBody Mono<MultiplyRequestDto> requestDtoMono,
                                   @RequestHeader Map<String, String> headers) {
        System.out.println(headers);
        return this.mathService.multiply(requestDtoMono);
    }


}
