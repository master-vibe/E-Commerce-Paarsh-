package com.paarsh.admin_paarsh.controller;

import com.paarsh.admin_paarsh.dto.SalesReportDTO;
import com.paarsh.admin_paarsh.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // Endpoint to generate today's sales report
    @GetMapping("/sales/today")
    public SalesReportDTO getTodaySalesReport() {
        return reportService.generateTodaySalesReport();
    }

    // Endpoint to generate yesterday's sales report
    @GetMapping("/sales/yesterday")
    public SalesReportDTO getYesterdaySalesReport() {
        return reportService.generateYesterdaySalesReport();
    }

    // Endpoint to generate sales report for a specific date
    @GetMapping("/sales/date")
    public SalesReportDTO getSalesReportByDate(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date reportDate) {
        return reportService.generateSalesReportForDate(reportDate, "SPECIFIC_DATE");
    }
}
