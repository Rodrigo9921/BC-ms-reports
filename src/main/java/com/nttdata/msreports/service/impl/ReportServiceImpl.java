package com.nttdata.msreports.service.impl;

import com.nttdata.msreports.dto.ClientDto;
import com.nttdata.msreports.dto.ReportDto;
import com.nttdata.msreports.dto.TransactionDto;
import com.nttdata.msreports.model.Report;
import com.nttdata.msreports.repository.ReportRepository;
import com.nttdata.msreports.service.ReportService;
import com.nttdata.msreports.utils.ReportMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final WebClient msTransactionsWebClient;
    private final WebClient msClientWebClient;

    public ReportServiceImpl(ReportRepository reportRepository,
                             @Qualifier("msTransactionsWebClient") WebClient msTransactionsWebClient,
                             @Qualifier("msClientWebClient") WebClient msClientWebClient) {
        this.reportRepository = reportRepository;
        this.msTransactionsWebClient = msTransactionsWebClient;
        this.msClientWebClient = msClientWebClient;
    }
    @Override
    public Mono<ReportDto> generateReport(String clientId, LocalDate reportDate) {
        Mono<ClientDto> clientMono = msClientWebClient
                .get()
                .uri("/" + clientId)
                .retrieve()
                .bodyToMono(ClientDto.class);

        Flux<TransactionDto> transactionFlux = msTransactionsWebClient
                .get()
                .uri("/client/" + clientId)
                .retrieve()
                .bodyToFlux(TransactionDto.class);

        return clientMono.zipWith(transactionFlux.collectList())
                .flatMap(tuple -> {
                    ClientDto client = tuple.getT1();
                    List<TransactionDto> transactions = tuple.getT2();

                    return ReportMapper.calculateAverageDailyBalance(Flux.fromIterable(transactions), reportDate)
                            .map(averageDailyBalance -> {
                                ReportDto report = new ReportDto();
                                report.setReportId(UUID.randomUUID().toString());
                                report.setClientId(clientId);
                                report.setReportDate(reportDate);
                                report.setAverageDailyBalance(averageDailyBalance);
                                report.setTotalCommissions(ReportMapper.calculateTotalCommissions(transactions));
                                return report;
                            });
                }).flatMap(reportDto -> reportRepository.save(ReportMapper.toEntity(reportDto))
                        .thenReturn(reportDto));  // Guarda el Report en la base de datos y luego devuelve el ReportDto;
    }
    @Override
    public Mono<ReportDto> getReport(String reportId) {
        return reportRepository.findById(reportId)
                .map(ReportMapper::toDto);
    }

}
