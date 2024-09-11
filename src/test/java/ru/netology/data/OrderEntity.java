package ru.netology.data;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {
    private String id;
    private Timestamp created;
    private String credit_id;
    private String payment_id;
}