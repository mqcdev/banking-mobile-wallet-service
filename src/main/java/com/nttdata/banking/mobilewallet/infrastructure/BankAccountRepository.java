package com.nttdata.banking.mobilewallet.infrastructure;

import com.nttdata.banking.mobilewallet.config.WebClientConfig;
import com.nttdata.banking.mobilewallet.model.BankAccount;
import com.nttdata.banking.mobilewallet.util.Constants;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class BankAccountRepository {

    @Value("${local.property.host.ms-bank-account}")
    private String propertyHostMsBankAccount;

    @Autowired
    ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory;

    @CircuitBreaker(name = Constants.BANKACCOUNT_CB, fallbackMethod = "getDefaultBankAccountByDebitCardNumber")
    public Mono<BankAccount> findBankAccountByDebitCardNumber(String debitCardNumber) {
        log.info("Inicio----findBankAccountByDebitCardNumber-------debitCardNumber: " + debitCardNumber);
        WebClientConfig webconfig = new WebClientConfig();
        return webconfig.setUriData("http://" + propertyHostMsBankAccount + ":8085")
                .flatMap(d -> webconfig.getWebclient().get().uri("/api/bankaccounts/debitCardNumber/" + debitCardNumber).retrieve()
                                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new Exception("Error 400")))
                                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new Exception("Error 500")))
                                .bodyToMono(BankAccount.class)
                        // .transform(it -> reactiveCircuitBreakerFactory.create("parameter-service").run(it, throwable -> Mono.just(new BankAccount())) )
                );
    }

    public Mono<BankAccount> getDefaultBankAccountByDebitCardNumber(String debitCardNumber, Exception e) {
        log.info("Inicio----getDefaultBankAccountByDebitCardNumber-------debitCardNumber: " + debitCardNumber);
        return Mono.empty();
    }
}
