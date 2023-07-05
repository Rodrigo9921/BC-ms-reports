package com.nttdata.msreports.controller;

import com.nttdata.msreports.dto.ReportDto;
import com.nttdata.msreports.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/create/{clientId}")
    public Mono<ResponseEntity<ReportDto>> generateReport(@PathVariable String clientId, @RequestParam("reportDate") String reportDate) {
        return reportService.generateReport(clientId, LocalDate.parse(reportDate))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    @GetMapping("/{reportId}")
    public Mono<ReportDto> getReport(@PathVariable String reportId) {
        return reportService.getReport(reportId);
    }
}