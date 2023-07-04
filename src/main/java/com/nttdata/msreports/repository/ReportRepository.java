package com.nttdata.msreports.repository;

import com.nttdata.msreports.model.Report;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ReportRepository extends ReactiveMongoRepository<Report, String> {
}
