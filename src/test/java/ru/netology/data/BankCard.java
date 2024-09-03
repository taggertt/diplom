package ru.netology.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankCard {
    private String cardNumber;
    private String expireMonth;
    private String expireYear;
    private String holderName;
    private String cvcCode;
}
