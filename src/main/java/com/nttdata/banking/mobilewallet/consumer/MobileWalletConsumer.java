package com.nttdata.banking.mobilewallet.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import com.nttdata.banking.mobilewallet.application.MobileWalletService;
import com.nttdata.banking.mobilewallet.consumer.mapper.BalanceMobileWalletModel;
import com.nttdata.banking.mobilewallet.infrastructure.MobileWalletRepository;
import com.nttdata.banking.mobilewallet.model.MobileWallet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import reactor.core.publisher.Mono;

/**
 * Class MobileWalletConsumer.
 * MobileWallet microservice class MobileWalletConsumer.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MobileWalletConsumer {

    @Autowired
    private MobileWalletService mobileWalletService;

    @Autowired
    private MobileWalletRepository mobileWalletRepository;

    @KafkaListener(topics = "${spring.kafka.topic.mobile.name}")
    public void listener(@Payload BalanceMobileWalletModel balanceModel) {
        log.info("Message received : {} ", balanceModel);
        applyBalance(balanceModel).block();
    }

    private Mono<MobileWallet> applyBalance(BalanceMobileWalletModel request) {
        log.info("applyBalance executed : {} ", request);
        return mobileWalletService.updateBalanceById(
                request.getIdMobileWallet(), request.getBalance());
    }
}
