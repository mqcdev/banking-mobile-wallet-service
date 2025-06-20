package com.nttdata.banking.mobilewallet.dto;

import org.springframework.data.annotation.Id;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nttdata.banking.mobilewallet.model.BankAccount;
import com.nttdata.banking.mobilewallet.model.Client;
import com.nttdata.banking.mobilewallet.model.MobileWallet;
import com.nttdata.banking.mobilewallet.model.Movement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Class MobileWalletDto.
 * MobileWallet microservice class MobileWalletDto.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MobileWalletDto {

    @Id
    private String idMobileWallet;

    private String documentNumber;

    private String cellphone;

    private String imei_cellphone;

    private String email;

    private String cardNumber;

    private Client client;

    private DebitCardDto debitCard;

    private Double balance;

    private BankAccount account;

    private List<Movement> movements;

    public Mono<MobileWallet> mapperToMobileWallet() {
        log.info("ini mapperToMobileWallet-------: ");

        Client client = Client.builder()
                .documentNumber(this.getDocumentNumber())
                .cellphone(this.getCellphone())
                .imei_cellphone(this.getImei_cellphone())
                .email(this.getEmail())
                .build();

        MobileWallet mobileWallet = MobileWallet.builder()
                .idMobileWallet(this.getIdMobileWallet())
                .client(client)
                .debitCard(this.getDebitCard())
                .balance(this.getBalance() == null ? 0 : this.getBalance())
                .build();
        log.info("fn MapperToMobileWallet-------: ");
        return Mono.just(mobileWallet);
    }
}