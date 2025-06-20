package com.nttdata.banking.mobilewallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Class MsMobileWalletApplication Main.
 * MobileWallet microservice class MsMobileWalletApplication.
 */
@SpringBootApplication
@EnableEurekaClient
public class MsMobileWalletApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsMobileWalletApplication.class, args);
    }

}
