package com.nttdata.banking.mobilewallet.infrastructure;

import com.nttdata.banking.mobilewallet.config.WebClientConfig;
import com.nttdata.banking.mobilewallet.model.Movement;
import com.nttdata.banking.mobilewallet.util.Constants;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class MovementRepository {

    @Value("${local.property.host.ms-movement}")
    private String propertyHostMsMovement;

    @Autowired
    ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory;

    @CircuitBreaker(name = Constants.MOVEMENT_CB, fallbackMethod = "getDefaultMovementsByAccountNumber")
    public Flux<Movement> findMovementsByAccountNumber(String accountNumber) {

        log.info("ini----findMovementsByAccountNumber-------: ");
        WebClientConfig webconfig = new WebClientConfig();
        Flux<Movement> movements = webconfig.setUriData("http://" + propertyHostMsMovement + ":8092")
                .flatMap(d -> webconfig.getWebclient().get().uri("/api/movements/accountNumber/" + accountNumber).retrieve()
                        .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new Exception("Error 400")))
                        .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new Exception("Error 500")))
                        .bodyToFlux(Movement.class)
                        // .transform(it -> reactiveCircuitBreakerFactory.create("parameter-service").run(it, throwable -> Flux.just(new Movement())))
                        .collectList()
                )
                .flatMapMany(iterable -> Flux.fromIterable(iterable));
        return movements;
    }

    public Flux<Movement> getDefaultMovementsByAccountNumber(String accountNumber, Exception e) {
        return Flux.empty();
    }
}
