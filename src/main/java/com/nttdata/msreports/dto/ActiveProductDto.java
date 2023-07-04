package com.nttdata.msreports.dto;

import lombok.Data;

@Data
public class ActiveProductDto {
    private String id;
    private String name;
    private double balance;
    private double interestRate;
}
