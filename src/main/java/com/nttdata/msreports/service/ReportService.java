package com.nttdata.msreports.service;

import com.nttdata.msreports.dto.ReportDto;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface ReportService {
    Mono<ReportDto> generateReport(String clientId, LocalDate reportDate);
    Mono<ReportDto> getReport(String reportId);
}
