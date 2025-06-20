package com.nttdata.banking.mobilewallet.consumer.mapper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

/**
 * Class BalanceMobileWalletModel.
 * MobileWallet microservice class BalanceMobileWalletModel.
 */
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BalanceMobileWalletModel {

    @JsonIgnore
    private String id;

    private String idMobileWallet;

    private Double balance;
}
