package com.example.moneytransfer;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class MoneyTransferController {

    private final MoneyTransferService moneyTransferService;

    @Autowired
    public MoneyTransferController(MoneyTransferService moneyTransferService) {
        this.moneyTransferService = moneyTransferService;
    }

    @PostMapping("/transfer")
    public void transfer(@RequestBody @Valid MoneyTransfer moneyTransfer) {
        moneyTransferService.transfer(moneyTransfer);
    }

    @GetMapping("/account/{id}")
    public Account getAccount(@PathVariable Long id) {
        return moneyTransferService.getAccount(id);
    }

}
