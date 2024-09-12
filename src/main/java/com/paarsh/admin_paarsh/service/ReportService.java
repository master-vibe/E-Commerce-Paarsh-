package com.paarsh.admin_paarsh.service;

import com.paarsh.admin_paarsh.dto.SalesReportDTO;
import com.paarsh.admin_paarsh.model.Order;
import com.paarsh.admin_paarsh.model.OrderItem;
import com.paarsh.admin_paarsh.model.Product;
import com.paarsh.admin_paarsh.repository.CustomerEnquiryRepository;
import com.paarsh.admin_paarsh.repository.OrderRepository;
import com.paarsh.admin_paarsh.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ReportService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerEnquiryRepository customerEnquiryRepository;

    @Autowired
    public ReportService(OrderRepository orderRepository, ProductRepository productRepository, CustomerEnquiryRepository customerEnquiryRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerEnquiryRepository = customerEnquiryRepository;
    }


    public SalesReportDTO generateSalesReportForDate(Date reportDate, String reportType) {
        List<Order> orders = orderRepository.findByOrderDate(reportDate);
        return generateSalesReport(orders, reportDate, reportType);
    }

    public SalesReportDTO generateTodaySalesReport() {
        Date today = new Date();
        return generateSalesReportForDate(today, "TODAY");
    }

    public SalesReportDTO generateYesterdaySalesReport() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date yesterday = calendar.getTime();
        return generateSalesReportForDate(yesterday, "YESTERDAY");
    }

    private SalesReportDTO generateSalesReport(List<Order> orders, Date reportDate, String reportType) {
        List<Product> products = productRepository.findAll();

        double totalSales = orders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();

        int totalOrders = orders.size();

        int totalProductsSold = orders.stream()
                .flatMap(order -> order.getItems().stream())
                .mapToInt(OrderItem::getQuantity)
                .sum();

        int remainingStockQuantity = products.stream()
                .mapToInt(Product::getStockQuantity)
                .sum();

        double remainingStockValue = products.stream()
                .mapToDouble(product -> product.getStockQuantity() * product.getPrice())
                .sum();

        SalesReportDTO report = new SalesReportDTO();
        report.setReportDate(reportDate);
        report.setTotalSales(totalSales);
        report.setTotalOrders(totalOrders);
        report.setTotalProductsSold(totalProductsSold);
        report.setRemainingStockValue(remainingStockValue);
        report.setRemainingStockQuantity(remainingStockQuantity);
        report.setReportType(reportType);

        return report;
    }
}
