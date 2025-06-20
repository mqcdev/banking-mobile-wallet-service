package com.nttdata.banking.mobilewallet.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nttdata.banking.mobilewallet.dto.DebitCardDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Class MobileWallet.
 * MobileWallet microservice class MobileWallet.
 */
@Document(collection = "MobileWallet")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MobileWallet {

    @Id
    private String idMobileWallet;

    private Client client;

    private DebitCardDto debitCard;

    private Double balance;
}
