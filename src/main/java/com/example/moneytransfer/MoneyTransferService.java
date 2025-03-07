package com.example.moneytransfer;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
@NoArgsConstructor
public class MoneyTransferService {

    private AccountRepository accountRepository;

    @Autowired
    public MoneyTransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Retryable(maxAttemptsExpression = "${transfer.retry.max.attempts}", retryFor = ObjectOptimisticLockingFailureException.class)
    @Transactional(propagation = REQUIRES_NEW)
    public void transfer(MoneyTransfer moneyTransfer) {
        Account accountFrom = getAccount(moneyTransfer.from);
        Account accountTo = getAccount(moneyTransfer.to);
        accountFrom.setAmount(accountFrom.amount - moneyTransfer.amount);
        accountTo.setAmount(accountTo.amount + moneyTransfer.amount);
        accountRepository.save(accountFrom);
        accountRepository.save(accountTo);
    }

    public Account getAccount(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }

}
