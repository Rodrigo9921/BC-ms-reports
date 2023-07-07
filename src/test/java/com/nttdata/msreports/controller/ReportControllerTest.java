package com.nttdata.msreports.controller;

import com.nttdata.msreports.dto.ReportDto;
import com.nttdata.msreports.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@WebFluxTest(controllers = ReportController.class)
class ReportControllerTest {

    @MockBean
    private ReportService reportService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        this.webTestClient = WebTestClient.bindToController(new ReportController(reportService)).build();
    }

    @Test
    void generateReport() {
        // Arrange
        String clientId = "clientId1";
        String reportDate = "2023-07-03";
        ReportDto reportDto = new ReportDto(); // replace with actual report DTO
        given(reportService.generateReport(clientId, LocalDate.parse(reportDate))).willReturn(Mono.just(reportDto));

        // Act & Assert
        webTestClient.post()
                .uri("/reports/create/{clientId}?reportDate={reportDate}", clientId, reportDate)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ReportDto.class).isEqualTo(reportDto);
    }

    @Test
    void getReport() {
        // Arrange
        String reportId = "reportId1";
        ReportDto reportDto = new ReportDto(); // replace with actual report DTO
        given(reportService.getReport(reportId)).willReturn(Mono.just(reportDto));

        // Act & Assert
        webTestClient.get()
                .uri("/reports/{reportId}", reportId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ReportDto.class).isEqualTo(reportDto);
    }
}