package com.nttdata.banking.mobilewallet.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;
import javax.validation.Valid;

import com.nttdata.banking.mobilewallet.dto.MobileWalletDto;
import com.nttdata.banking.mobilewallet.model.MobileWallet;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.nttdata.banking.mobilewallet.application.MobileWalletService;

@RestController
@RequestMapping("/api/mobilewallet")
@Slf4j
public class MobileWalletController {
    @Autowired
    private MobileWalletService service;

    @GetMapping
    public Mono<ResponseEntity<Flux<MobileWallet>>> listMobileWallets() {
        return Mono.just(ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll()));
    }

    @GetMapping("/{idMobileWallet}")
    public Mono<ResponseEntity<MobileWallet>> getMobileWalletDetails(@PathVariable("idMobileWallet") String idClient) {
        return service.findById(idClient)
                .map(c -> ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON).body(c))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/cellphone/{cellphone}")
    public Mono<ResponseEntity<MobileWalletDto>> getMobileWalletByAccountNumber(@PathVariable("cellphone") String cellphone) {
        log.info("GetMapping--getMobileWalletByAccountNumber-------cellphone: " + cellphone);
        return service.findByCellphone(cellphone)
                .map(c -> ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(c))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    /*@GetMapping("/accountNumber/{accountNumber}")
    public Mono<ResponseEntity<MobileWallet>> getMobileWalletByAccountNumber(@PathVariable("accountNumber") String accountNumber) {
        log.info("GetMapping--getMobileWalletByAccountNumber-------accountNumber: " + accountNumber);
        return service.findByAccountNumber(accountNumber).map(c -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(c))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }*/

    @GetMapping("/debitCardNumber/{debitCardNumber}")
    public Mono<ResponseEntity<MobileWallet>> getMobileWalletByDebitCardNumber(@PathVariable("debitCardNumber") String debitCardNumber) {
        log.info("GetMapping--getMobileWalletByDebitCardNumber-------debitCardNumber: " + debitCardNumber);
        return service.findByDebitCardNumberAndIsMainAccount(debitCardNumber)
                .map(c -> ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(c))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> saveMobileWallet(@Valid @RequestBody Mono<MobileWalletDto> MobileWalletDto) {
        Map<String, Object> request = new HashMap<>();
        return MobileWalletDto
                .flatMap(bnkAcc -> service.save(bnkAcc)
                        .map(baSv -> {
                            request.put("mobileWallet", baSv);
                            request.put("message", "Monedero Movil guardado con exito");
                            request.put("timestamp", new Date());
                            return ResponseEntity.created(URI.create("/api/mobilewallet/".concat(baSv.getIdMobileWallet())))
                                    .contentType(MediaType.APPLICATION_JSON).body(request);
                        })
                );
    }

    @PutMapping("/{idMobileWallet}")
    public Mono<ResponseEntity<MobileWallet>> editMobileWallet(@Valid @RequestBody MobileWalletDto mobileWalletDto, @PathVariable("idMobileWallet") String idMobileWallet) {
        return service.update(mobileWalletDto, idMobileWallet)
                .map(c -> ResponseEntity.created(URI.create("/api/mobilewallet/".concat(idMobileWallet)))
                        .contentType(MediaType.APPLICATION_JSON).body(c));
    }

    @PutMapping("/{idMobileWallet}/balance/{balance}")
    public Mono<ResponseEntity<MobileWallet>> editBalanceMobileWallet(@PathVariable("idMobileWallet") String idMobileWallet, @PathVariable("balance") Double balance) {

        log.info("PutMapping--editBalanceMobileWallet-------idMobileWallet: " + idMobileWallet);
        log.info("PutMapping--editBalanceMobileWallet-------balance: " + balance);
        return service.updateBalanceById(idMobileWallet, balance)
                .map(c -> ResponseEntity.created(URI.create("/api/mobilewallet/".concat(idMobileWallet)))
                        .contentType(MediaType.APPLICATION_JSON).body(c));
    }

    @DeleteMapping("/{idMobileWallet}")
    public Mono<ResponseEntity<Void>> deleteMobileWallet(@PathVariable("idMobileWallet") String idMobileWallet) {
        return service.delete(idMobileWallet)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
    }

    /*@GetMapping("/documentNumber/{documentNumber}/AccountType/{accountType}")
    public Mono<ResponseEntity<List<MobileWallet>>> getMobileWalletByDocumentNumberAndAccountType(@PathVariable("documentNumber") String documentNumber, @PathVariable("accountType") String accountType) {
        return service.findByDocumentNumber(documentNumber, accountType)
                .collectList()
                .map(c -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(c))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }*/

    @GetMapping("/documentNumber/{documentNumber}/accountNumber/{accountNumber}/movements")
    public Mono<ResponseEntity<MobileWalletDto>> getMovementsOfMobileWalletByDocumentNumberAndAccountType(@PathVariable("documentNumber") String documentNumber, @PathVariable("accountNumber") String accountNumber) {
        return service.findMovementsByDocumentNumber(documentNumber, accountNumber)
                .map(c -> ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(c))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/documentNumber/{documentNumber}/cardNumber/{cardNumber}/withdrawalAmount/{withdrawalAmount}")
    public Mono<ResponseEntity<List<MobileWallet>>> getMobileWalletByDocumentNumberAndWithdrawalAmount(@PathVariable("documentNumber") String documentNumber, @PathVariable("cardNumber") String cardNumber, @PathVariable("withdrawalAmount") Double withdrawalAmount) {
        return service.findByDocumentNumberAndWithdrawalAmount(documentNumber, cardNumber, withdrawalAmount)
                .collectList()
                .map(c -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(c))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/documentNumber/{documentNumber}")
    public Mono<ResponseEntity<List<MobileWallet>>> getMobileWalletBalanceByDocumentNumber(@PathVariable("documentNumber") String documentNumber) {
        return service.findBalanceByDocumentNumber(documentNumber)
                .collectList()
                .map(c -> ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(c))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/count/documentNumber/{documentNumber}")
    public Mono<ResponseEntity<Long>> getCantMobileWalletBalanceByDocumentNumber(@PathVariable("documentNumber") String documentNumber) {
        return service.findBalanceByDocumentNumber(documentNumber)
                .collectList()
                .map(c -> ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(c.stream().count()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/first/documentNumber/{documentNumber}")
    public Mono<ResponseEntity<MobileWallet>> getFirstMobileWalletBalanceByDocumentNumber(@PathVariable("documentNumber") String documentNumber) {
        return service.findBalanceByDocumentNumber(documentNumber)
                .collectList()
                .map(c -> ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(c.get(0)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
