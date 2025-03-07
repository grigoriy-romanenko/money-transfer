package com.example.moneytransfer;

import jakarta.validation.constraints.Min;

public class MoneyTransfer {

    Long from;
    Long to;
    @Min(1) Long amount;

}
