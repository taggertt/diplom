package ru.netology.data;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditRequestEntity {
    private String id;
    private String bank_id;
    private Timestamp created;
    private String status;
}
