package com.nttdata.banking.mobilewallet.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class Headline.
 * MobileWallet microservice class Headline.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Headline {

    private String names;
    private String surnames;
    private String documentType;
    private String documentNumber;

}