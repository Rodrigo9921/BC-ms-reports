package com.nttdata.msreports.dto;




import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private String id;
    private String clientId;
    private String productId;
    private String productType; // This can be "Passive" or "Active"
    private Double amount;
    private String transactionType; // This can be "Deposit", "Withdrawal", "Payment", "Charge"
    private LocalDateTime transactionDate;
    private Double commission;
}
