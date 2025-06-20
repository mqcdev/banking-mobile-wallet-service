package com.nttdata.banking.mobilewallet.dto.bean;

import java.util.List;
import com.nttdata.banking.mobilewallet.dto.DebitCardDto;
import com.nttdata.banking.mobilewallet.model.BankAccount;
import com.nttdata.banking.mobilewallet.model.Client;
import com.nttdata.banking.mobilewallet.model.Headline;
import com.nttdata.banking.mobilewallet.model.Movement;
import com.nttdata.bootcamp.msmobilewallet.model.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import reactor.core.publisher.Mono;

/**
 * Class BankAccountBean.
 * MobileWallet microservice class BankAccountBean.
 */
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@ToString
public abstract class BankAccountBean {

    private String idBankAccount;
    private String documentNumber;
    private String accountType;
    //private String cardNumber;
    private DebitCardDto debitCard;
    private String accountNumber;
    private Double commission;
    private Integer movementDate;
    private Integer maximumMovement;
    private Double startingAmount;
    private List<Headline> listHeadline;
    private List<Headline> listAuthorizedSignatories;
    private String currency;
    private Double minimumAmount;
    private Double transactionLimit;
    private Double commissionTransaction;
    private List<Movement> movements;

    public abstract Mono<Boolean> validateFields();

    public abstract Mono<Boolean> validateCommissionByAccountType();

    public abstract Mono<Boolean> validateMovementsByAccountType();

    public abstract Mono<BankAccount> mapperToBankAccount(Client client);
}