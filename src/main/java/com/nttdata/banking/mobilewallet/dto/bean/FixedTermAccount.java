package com.nttdata.banking.mobilewallet.dto.bean;

import com.nttdata.banking.mobilewallet.exception.ResourceNotFoundException;
import com.nttdata.banking.mobilewallet.model.BankAccount;
import com.nttdata.banking.mobilewallet.model.Client;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@SuperBuilder
@Slf4j
@Getter
@Setter
@ToString
public class FixedTermAccount extends BankAccountBean {

    @Override
    public Mono<Boolean> validateFields() {
        log.info("FixedTermAccount validateFields-------: ");
        return Mono.when(validateCommissionByAccountType(), validateMovementsByAccountType()).then(Mono.just(true));
    }
    @Override
    public Mono<Boolean> validateCommissionByAccountType() {
        log.info("ini FixedTermAccount validateCommissionByAccountType-------: ");
        return Mono.just(this.getAccountType()).flatMap(ct -> {
            this.setCommission(0.0);
            log.info("fn FixedTermAccount validateCommissionByAccountType-------: ");
            return Mono.just(true);
        });
    }
    @Override
    public Mono<Boolean> validateMovementsByAccountType() {
        log.info("ini FixedTermAccount validateMovementsByAccountType-------: ");
        return Mono.just(this.getAccountType()).flatMap(ct -> {
            log.info("--FixedTermAccount validateMovementsByAccountType------- set setMaximumMovement: ");
            this.setMaximumMovement(1);
            if (this.getMovementDate() == null || !(this.getMovementDate() > 0)) {
                return Mono.error(new ResourceNotFoundException("Fecha de movimientos", "MovementDate", this.getMovementDate() == null ? "" : this.getMovementDate().toString()));
            }
            log.info("fin FixedTermAccount validateMovementsByAccountType-------: ");
            return Mono.just(true);
        });
    }
    @Override
    public Mono<BankAccount> mapperToBankAccount(Client client) {
        log.info("ini FixedTermAccount MapperToBankAccount-------: ");
        BankAccount bankAccount = BankAccount.builder()
                .idBankAccount(this.getIdBankAccount())
                .client(client)
                .accountType(this.getAccountType())
                //.cardNumber(this.getCardNumber())
                .debitCard(this.getDebitCard())
                .accountNumber(this.getAccountNumber())
                .commission(this.getCommission()) // Se setea
                .movementDate(this.getMovementDate())
                .maximumMovement(this.getMaximumMovement())// Se setea
                .startingAmount(this.getStartingAmount())
                .currency(this.getCurrency())
                .minimumAmount(this.getMinimumAmount())
                .transactionLimit(this.getTransactionLimit())
                .commissionTransaction(this.getCommissionTransaction())
                .build();
        log.info("fn FixedTermAccount MapperToBankAccount-------: ");
        return Mono.just(bankAccount);
    }
}