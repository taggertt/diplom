package ru.netology.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditRequestEntity {
    private String id;
    private String bank_id;
    private Timestamp created;
    private String status;

}
