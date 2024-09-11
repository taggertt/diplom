package ru.netology.data;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity {
    private String id;
    private long amount;
    private Timestamp created;
    private String status;
    private String transaction_id;
}
