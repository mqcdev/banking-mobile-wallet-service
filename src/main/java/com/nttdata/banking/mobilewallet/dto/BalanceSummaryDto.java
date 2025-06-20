package com.nttdata.banking.mobilewallet.dto;

import java.util.List;
import com.nttdata.banking.mobilewallet.model.Movement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Class BalanceSummaryDto.
 * MobileWallet microservice class BalanceSummaryDto.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class BalanceSummaryDto {

    private String documentNumber;
    private List<InfoBankAccount> objBankAccountInfo;
    private List<Movement> movements;

}
