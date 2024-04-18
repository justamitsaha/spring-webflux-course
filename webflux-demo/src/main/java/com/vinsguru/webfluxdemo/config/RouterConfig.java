package com.vinsguru.webfluxdemo.config;

import com.vinsguru.webfluxdemo.dto.InputFailedValidationResponse;
import com.vinsguru.webfluxdemo.exception.InputValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

@Configuration
public class RouterConfig {

    @Autowired
    private RequestHandler requestHandler;

    @Bean
    public RouterFunction<ServerResponse> highLevelRouter() {
        return RouterFunctions.route()
                .path("route", this::checkOperation)
                .build();
    }

    private RouterFunction<ServerResponse> checkOperation() {
        return RouterFunctions.route()
                .path("square", this::serverResponseSquareFunction)
                .path("table", this::serverResponseTableFunction)
                .path("multiply", this::serverResponseMultiplyFunction)
                .build();
    }

    // @Bean
    private RouterFunction<ServerResponse> serverResponseSquareFunction() {
        return RouterFunctions.route()
                .GET("/{input}", RequestPredicates.path("/1?"), requestHandler::squareHandler)
                .GET("{input}", req -> ServerResponse.badRequest().bodyValue("only 10-19 allowed"))
                .onError(InputValidationException.class, exceptionHandler())
                .build();
    }

    private RouterFunction<ServerResponse> serverResponseTableFunction() {
        return RouterFunctions.route()
                .GET("{input}", RequestPredicates.path(".*[^0]$"), requestHandler::tableHandler)
                .GET("{input}", request -> ServerResponse.badRequest().bodyValue("For Number ending with Zero don't use API"))
                .GET("{input}/stream", RequestPredicates.path(".*[^0]$"), requestHandler::tableStreamHandler)
                .GET("{input}/stream", request -> ServerResponse.badRequest().bodyValue("For Number ending with Zero don't use API"))
                .onError(InputValidationException.class, exceptionHandler())
                .build();
    }

    private RouterFunction<ServerResponse> serverResponseMultiplyFunction() {
        return RouterFunctions.route()
                .POST("multiply", requestHandler::multiplyHandler)
                .GET("square/{input}/validation", requestHandler::squareHandlerWithValidation)
                .onError(InputValidationException.class, exceptionHandler())
                .build();
    }

    private BiFunction<Throwable, ServerRequest, Mono<ServerResponse>> exceptionHandler() {
        return (err, req) -> {
            InputValidationException ex = (InputValidationException) err;
            InputFailedValidationResponse response = new InputFailedValidationResponse();
            response.setInput(ex.getInput());
            response.setMessage(ex.getMessage());
            response.setErrorCode(ex.getErrorCode());
            return ServerResponse.badRequest().bodyValue(response);
        };
    }


}
