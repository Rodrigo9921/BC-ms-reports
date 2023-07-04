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
                                report.setClientId(clientId);
                                report.setReportDate(reportDate);
                                report.setAverageDailyBalance(averageDailyBalance);
                                report.setTotalCommissions(ReportMapper.calculateTotalCommissions(transactions));
                                return report;
                            });
                });
    }



    /*
    @Override
    public Mono<ReportDto> generateReport(String clientId) {
        // Obtén los datos necesarios de los otros microservicios
        Mono<ClientDto> clientMono = msClientWebClient.get()
                .uri("/" + clientId)
                .retrieve()
                .bodyToMono(ClientDto.class);

        Mono<List<TransactionDto>> transactionsMono = msTransactionsWebClient.get()
                .uri("/client/" + clientId)
                .retrieve()
                .bodyToFlux(TransactionDto.class)
                .collectList();

        // Combina los datos obtenidos para generar el informe
        return Mono.zip(clientMono, transactionsMono)
                .flatMap(tuple -> {
                    ClientDto client = tuple.getT1();
                    List<TransactionDto> transactions = tuple.getT2();

                    Report report = new Report();
                    report.setClientId(clientId);
                    report.setAverageDailyBalance(ReportMapper.calculateAverageDailyBalance(transactions));
                    report.setTotalCommissions(ReportMapper.calculateTotalCommissions(transactions));

                    return reportRepository.save(report)
                            .map(ReportMapper::toDto);
                });
    }
*/
    @Override
    public Mono<ReportDto> getReport(String reportId) {
        return reportRepository.findById(reportId)
                .map(ReportMapper::toDto);
    }
}
