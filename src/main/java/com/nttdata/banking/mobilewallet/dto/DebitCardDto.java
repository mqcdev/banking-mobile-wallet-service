package com.nttdata.banking.mobilewallet.dto;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Class DebitCardDto.
 * MobileWallet microservice class DebitCardDto.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class DebitCardDto {

    @Id
    private String idDebitCard;
    private String cardNumber;
    private Boolean isMainAccount;
    private Integer order;

}
