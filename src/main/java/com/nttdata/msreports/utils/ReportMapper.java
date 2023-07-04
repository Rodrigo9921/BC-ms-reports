package com.nttdata.msreports.utils;

import com.nttdata.msreports.dto.ReportDto;
import com.nttdata.msreports.dto.TransactionDto;
import com.nttdata.msreports.model.Report;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportMapper {
    public static ReportDto toDto(Report report) {
        ReportDto dto = new ReportDto();
        dto.setReportId(report.getReportId());
        dto.setClientId(report.getClientId());
        dto.setAverageDailyBalance(report.getAverageDailyBalance());
        dto.setTotalCommissions(report.getTotalCommissions());
        dto.setReportDate(report.getReportDate());
        // establece otros campos según sea necesario
        return dto;
    }

    public static Report toEntity(ReportDto dto) {
        Report report = new Report();
        report.setReportId(dto.getReportId());
        report.setClientId(dto.getClientId());
        report.setAverageDailyBalance(dto.getAverageDailyBalance());
        report.setTotalCommissions(dto.getTotalCommissions());
        report.setReportDate(dto.getReportDate());
        // establece otros campos según sea necesario
        return report;
    }
    public static Mono<Double> calculateAverageDailyBalance(Flux<TransactionDto> transactions, LocalDate reportDate) {
        return transactions
                .collectList()
                .map(transactionList -> {
                    System.out.println(transactionList.size());
                    Map<LocalDate, List<TransactionDto>> transactionsByDate = transactionList.stream()
                            .collect(Collectors.groupingBy(transaction -> transaction.getTransactionDate().toLocalDate()));

                    double totalDailyBalances = transactionsByDate.entrySet().stream()
                            .mapToDouble(entry -> entry.getValue().stream()
                                    .mapToDouble(TransactionDto::getAmount)
                                    .sum())
                            .sum();

                    long daysBetween =ChronoUnit.DAYS.between(transactionsByDate.keySet().stream().min(LocalDate::compareTo).orElse(reportDate), reportDate);

                    return totalDailyBalances / (daysBetween + 1);
                });
    }

    /*
    public static double calculateAverageDailyBalance(List<TransactionDto> transactions) {
        // Agrupa las transacciones por fecha y suma los montos de las transacciones de cada día
        Map<LocalDate, Double> dailyBalances = transactions.stream()
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getTransactionDate().toLocalDate(),
                        Collectors.summingDouble(TransactionDto::getAmount)
                ));

        // Calcula el saldo promedio diario
        long daysInPeriod = ChronoUnit.DAYS.between(
                transactions.get(0).getTransactionDate().toLocalDate(),
                transactions.get(transactions.size() - 1).getTransactionDate().toLocalDate()
        );
        return dailyBalances.values().stream().mapToDouble(Double::doubleValue).sum() / daysInPeriod;
    }
*/
    public static double calculateTotalCommissions(List<TransactionDto> transactions) {
        // Suma las comisiones de todas las transacciones
        // Asume que la comisión se calcula como un porcentaje del monto de la transacción
        // y que el porcentaje de la comisión se almacena en un campo 'commissionRate' en TransactionDto
        // Si tu lógica para calcular las comisiones es diferente, necesitarás ajustar esto
        return transactions.stream()
                .filter(transaction -> transaction.getCommission() != null)
                .mapToDouble(TransactionDto::getCommission)
                .sum();
    }
}
