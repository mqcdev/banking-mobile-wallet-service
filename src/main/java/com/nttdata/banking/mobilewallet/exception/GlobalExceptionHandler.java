package com.nttdata.banking.mobilewallet.exception;

import com.nttdata.banking.mobilewallet.dto.ErrorDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ResponseEntity<ErrorDetail>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return Mono.just(ex).flatMap(e -> {

            ErrorDetail errorDetail = new ErrorDetail(new Date(), ex.getMessage(), "");
            return Mono.just(new ResponseEntity<>(errorDetail, HttpStatus.NOT_FOUND));
        });
    }

    @ExceptionHandler(BlogAppException.class)
    public Mono<ResponseEntity<ErrorDetail>> handleBlogAppException(BlogAppException ex) {
        return Mono.just(ex).flatMap(e -> {
            ErrorDetail errorDetail = new ErrorDetail(new Date(), e.getMessage(), "");
            return Mono.just(new ResponseEntity<>(errorDetail, HttpStatus.BAD_REQUEST));
        });
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorDetail>> handleGlobalException(Exception ex) {
        return Mono.just(ex).flatMap(e -> {
            ErrorDetail errorDetail = new ErrorDetail(new Date(), e.getMessage(), "");
            return Mono.just(new ResponseEntity<>(errorDetail, HttpStatus.INTERNAL_SERVER_ERROR));
        });
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WebExchangeBindException.class)
    protected Mono<ResponseEntity<Object>> handleMethodArgumentNotValid(WebExchangeBindException ex) {

        Map<String, Object> request = new HashMap<>();
        return Mono.just(ex).flatMap(e -> Mono.just(e.getFieldErrors()))
                .flatMapMany(Flux::fromIterable)
                .map(fieldError -> "El campo: " + fieldError.getField() + " " + fieldError.getDefaultMessage())
                .collectList().flatMap(list -> {
                    request.put("errors", list);
                    request.put("timestamp", new Date());
                    request.put("status", HttpStatus.BAD_REQUEST.value());
                    return Mono.just(ResponseEntity.badRequest().body(request));
                });
    }
}
