package ru.netology.data;

import lombok.Value;

@Value
public class BankCard {
    private String cardNumber;
    private String expireMonth;
    private String expireYear;
    private String holderName;
    private String cvcCode;
}
