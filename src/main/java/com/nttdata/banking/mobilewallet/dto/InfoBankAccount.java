package com.nttdata.banking.mobilewallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Class InfoBankAccount.
 * MobileWallet microservice class InfoBankAccount.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class InfoBankAccount {

    private String accountType;
    private String accountNumber;
    private Double averageDailyBalance;

}
