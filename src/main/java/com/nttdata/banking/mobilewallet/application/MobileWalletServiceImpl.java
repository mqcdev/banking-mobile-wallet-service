package com.nttdata.banking.mobilewallet.application;

import com.nttdata.banking.mobilewallet.dto.DebitCardDto;
import com.nttdata.banking.mobilewallet.dto.MobileWalletDto;
import com.nttdata.banking.mobilewallet.exception.ResourceNotFoundException;
import com.nttdata.banking.mobilewallet.infrastructure.*;
import com.nttdata.banking.mobilewallet.model.MobileWallet;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class MobileWalletServiceImpl implements MobileWalletService {

    @Autowired
    private MobileWalletRepository mobileWalletRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private MovementRepository movementRepository;
    @Autowired
    private CreditRepository creditRepository;
    @Autowired
    private DebitCardRepository debitCardRepository;
    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Override
    public Flux<MobileWallet> findAll() {
        return mobileWalletRepository.findAll();
    }

    @Override
    public Mono<MobileWallet> findById(String idMobileWallet) {
        return Mono.just(idMobileWallet)
                .flatMap(mobileWalletRepository::findById)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Monedero móvil", "idMobileWallet", idMobileWallet)));
    }

    @Override
    public Mono<MobileWalletDto> findByCellphone(String cellphone) {
        return Mono.just(cellphone)
                .flatMap(mobileWalletRepository::findByCellphone)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Monedero móvil", "cellphone", cellphone)))
                .flatMap(mw -> {
                    if (mw.getDebitCard() != null) {
                        return bankAccountRepository.findBankAccountByDebitCardNumber(mw.getDebitCard().getCardNumber())
                                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Cuenta bancaria", "CardNumber", mw.getDebitCard().getCardNumber())))
                                .flatMap(ac -> {
                                    mw.setAccount(ac);
                                    return Mono.just(mw);
                                });
                    } else {
                        return Mono.just(mw);
                    }
                });
    }
    /*@Override
    public Mono<MobileWallet> findByAccountNumber(String accountNumber) {
        return Mono.just(accountNumber)
                .flatMap(mobileWalletRepository::findByAccountNumber)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Número Cuenta Bancaria", "accountNumber", accountNumber)));
    }*/

    @Override
    public Mono<MobileWalletDto> findMovementsByDocumentNumber(String documentNumber, String cellphone) {
        return mobileWalletRepository.findByAccountAndDocumentNumber(documentNumber, cellphone)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Cuenta Bancaria", "accountNumber", cellphone)))
                .flatMap(d -> movementRepository.findMovementsByAccountNumber(cellphone)
                        .collectList()
                        .flatMap(m -> {
                            d.setMovements(m);
                            return Mono.just(d);
                        })
                );
    }

    @Override
    public Mono<MobileWallet> save(MobileWalletDto mobileWalletDto) {
        log.info("----save-------mobileWalletDto : " + mobileWalletDto.toString());
        return Mono.just(mobileWalletDto)
                .flatMap(mwd -> setDebitCard(mwd))
                .flatMap(mwd -> validateNumberClientAccounts(mwd, "save").then(Mono.just(mwd)))
                .flatMap(mwd -> mwd.mapperToMobileWallet())
                .flatMap(mobileWalletRepository::save);
    }

    public Mono<MobileWalletDto> setDebitCard(MobileWalletDto mobileWalletDto) {
        log.info("---setDebitCard : " + mobileWalletDto.toString());
        return Mono.just(mobileWalletDto)
                .flatMap(badto -> {
                    if (badto.getCardNumber() != null) {
                        return debitCardRepository.findByCardNumber(badto.getCardNumber())
                                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Tarjeta de débito", "CardNumber", badto.getCardNumber())))
                                .flatMap(dc -> {
                                    DebitCardDto debitCardDto = DebitCardDto.builder()
                                            .idDebitCard(dc.getIdDebitCard())
                                            .cardNumber(dc.getCardNumber())
                                            .build();

                                    badto.setDebitCard(debitCardDto);
                                    return Mono.just(badto);
                                })
                                .then(Mono.just(badto));
                    } else {
                        return Mono.just(badto);
                    }
                });
    }

    @Override
    public Mono<MobileWallet> update(MobileWalletDto mobileWalletDto, String idMobileWallet) {
        log.info("----update-------mobileWalletDto -- idMobileWallet: " + mobileWalletDto.toString() + " -- " + idMobileWallet);
        return Mono.just(mobileWalletDto)
                .flatMap(badto -> setDebitCard(badto))
                .flatMap(mwd -> validateNumberClientAccounts(mwd, "save").then(Mono.just(mwd)))
                .flatMap(mwd -> mwd.mapperToMobileWallet())
                .flatMap(mwd -> mobileWalletRepository.findById(idMobileWallet)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Monedero móvil", "idMobileWallet", idMobileWallet)))
                        .flatMap(x -> {
                            mwd.setIdMobileWallet(x.getIdMobileWallet());
                            return mobileWalletRepository.save(mwd);
                        })
                );
    }

    @Override
    public Mono<Void> delete(String idMobileWallet) {
        return Mono.just(idMobileWallet)
                .flatMap(b -> mobileWalletRepository.findById(b))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Monedero móvil", "idMobileWallet", idMobileWallet)))
                .flatMap(mobileWalletRepository::delete);
    }

    public Mono<Boolean> validateNumberClientAccounts(MobileWalletDto MobileWalletDto, String method) {
        log.info("--validateNumberClientAccounts-------: ");
        if (method.equals("save")) {
            return mobileWalletRepository.findAllByCellphone(MobileWalletDto.getCellphone())
                    .count().flatMap(cnt -> {
                        if (cnt >= 1) {
                            return Mono.error(new ResourceNotFoundException("Monedero móvil ya existe : Cellphone", MobileWalletDto.getCellphone()));
                        } else {
                            return Mono.just(true);
                        }
                    });
        } else {
            return Mono.just(true);
        }
    }

    @Override
    public Flux<MobileWallet> findByDocumentNumberAndWithdrawalAmount(String documentNumber, String cardNumber, Double withdrawalAmount) {
        return mobileWalletRepository.findByClientAndCardAndIsNotMainAccount(documentNumber, cardNumber)
                .collectList()
                .flatMap(dcc -> {
                    Stream<MobileWallet> bankAccounts = dcc.stream();
                    Long countBA = dcc.stream().count();
                    if (countBA > 0) {
                        AtomicReference<Double> missingOutflowAmount = new AtomicReference<>(withdrawalAmount);
                        List<MobileWallet> bcAV = bankAccounts
                                .sorted((o1, o2) -> o1.getDebitCard().getOrder().compareTo(o2.getDebitCard().getOrder()))
                                .filter(ft -> {
                                    Double valIni = missingOutflowAmount.get();
                                    if (valIni <= 0) {
                                        return false;
                                    } else {
                                        Double balance = ft.getBalance() != null ? ft.getBalance() : 0;
                                        missingOutflowAmount.set(missingOutflowAmount.get() - balance);
                                        return true;
                                    }
                                })
                                .collect(Collectors.toList());
                        return Mono.just(bcAV);
                    } else {
                        return Mono.just(dcc);
                    }
                })
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<MobileWallet> updateBalanceById(String idMobileWallet, Double balance) {
        return mobileWalletRepository.findById(idMobileWallet)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Monedero móvil", "idMobileWallet", idMobileWallet)))
                .flatMap(x -> {
                    x.setBalance(balance);
                    return mobileWalletRepository.save(x);
                });
    }

    @Override
    public Mono<MobileWallet> findByDebitCardNumberAndIsMainAccount(String debitCardNumber) {
        return mobileWalletRepository.findByCardNumberAndIsMainAccount(debitCardNumber)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Tarjeta de débito", "debitCardNumber", debitCardNumber)));
    }

    @Override
    public Flux<MobileWallet> findBalanceByDocumentNumber(String documentNumber) {
        return mobileWalletRepository.findBalanceByDocumentNumber(documentNumber)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Cliente", "documentNumber", documentNumber)));
    }

}
