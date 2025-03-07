package com.example.moneytransfer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.mockito.Mockito.*;

@SpringBootTest
class MoneyTransferServiceTest {

    @MockitoSpyBean
    private AccountRepository accountRepository;
    @MockitoSpyBean
    private MoneyTransferService moneyTransferService;

    @AfterEach
    void cleanUp(){
        accountRepository.deleteAll();
    }

    @Test
    void getAccount() {
        Account expected = accountRepository.save(new Account(1L, 100L));

        Account actual = accountRepository.findById(expected.id).get();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void transfer() {
        Account accountFrom = accountRepository.save(new Account(1L, 100L));
        Account accountTo = accountRepository.save(new Account(2L, 100L));
        MoneyTransfer moneyTransfer = new MoneyTransfer();
        moneyTransfer.from = accountFrom.getId();
        moneyTransfer.to = accountTo.getId();
        moneyTransfer.amount = 50L;

        moneyTransferService.transfer(moneyTransfer);

        Assertions.assertEquals(50, accountRepository.findById(accountFrom.id).get().amount);
        Assertions.assertEquals(150, accountRepository.findById(accountTo.id).get().amount);
    }

    @Test
    void transferConcurrent() {
        Account accountFrom = accountRepository.save(new Account(1L, 100L));
        Account accountTo = accountRepository.save(new Account(2L, 100L));
        MoneyTransfer moneyTransfer = new MoneyTransfer();
        moneyTransfer.from = accountFrom.getId();
        moneyTransfer.to = accountTo.getId();
        moneyTransfer.amount = 50L;
        var defaultAnswer = Mockito.mockingDetails(accountRepository).getMockCreationSettings().getDefaultAnswer();
        doAnswer((Answer<Void>) invocation -> {
            defaultAnswer.answer(invocation);
            moneyTransferService.transfer(moneyTransfer);
            return null;
        }).doAnswer((Answer<Void>) invocation -> {
            defaultAnswer.answer(invocation);
            return null;
        }).when(accountRepository).save(new Account(accountFrom.id, accountFrom.amount - moneyTransfer.amount, 0));

        moneyTransferService.transfer(moneyTransfer);

        verify(moneyTransferService, times(3)).transfer(moneyTransfer);
        Assertions.assertEquals(0, accountRepository.findById(accountFrom.id).get().amount);
        Assertions.assertEquals(200, accountRepository.findById(accountTo.id).get().amount);
    }

}
