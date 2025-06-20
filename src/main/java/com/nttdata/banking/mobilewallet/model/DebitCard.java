package com.nttdata.banking.mobilewallet.model;

import org.springframework.data.annotation.Id;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Class DebitCard.
 * MobileWallet microservice class DebitCard.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DebitCard {

    @Id
    private String idDebitCard;
    private String cardNumber;
    private Boolean state;

}
