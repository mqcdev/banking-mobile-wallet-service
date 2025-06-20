package com.nttdata.banking.mobilewallet.util;

import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import lombok.AllArgsConstructor;

/**
 * Class Constants.
 * MobileWallet microservice class Constants.
 */
@AllArgsConstructor
@Component
public class Constants {

    public static final String CLIENT_CB = "clientCB";
    public static final String CREDIT_CB = "creditCB";
    public static final String DEBITCARD_CB = "debitcardCB";
    public static final String MOVEMENT_CB = "movementCB";
    public static final String BANKACCOUNT_CB = "bankaccountCB";

    public LocalDate dateToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public Date cutTimeFromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
