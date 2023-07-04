package com.nttdata.msreports.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${ms-client.url}")
    private String msClientUrl;

    @Value("${ms-transaccions.url}")
    private String msTransactionsUrl;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }

    @Bean
    public WebClient msTransactionsWebClient(WebClient webClient) {
        return webClient.mutate()
                .baseUrl(msTransactionsUrl)
                .build();
    }

    @Bean
    public WebClient msClientWebClient(WebClient webClient) {
        return webClient.mutate()
                .baseUrl(msClientUrl)
                .build();
    }
}





