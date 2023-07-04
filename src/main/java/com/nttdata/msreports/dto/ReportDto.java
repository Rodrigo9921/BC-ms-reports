package com.nttdata.msreports.dto;

import lombok.Data;

import java.time.LocalDate;
@Data

public class ReportDto {
    private String reportId;
    private String clientId;
    private Double averageDailyBalance;
    private Double totalCommissions;
    private LocalDate reportDate;
}
