package com.paarsh.admin_paarsh.dto;

import java.util.Date;

public class SalesReportDTO {

    private Date reportDate;
    private Double totalSales;
    private Integer totalOrders;
    private Integer totalProductsSold;
    private Double remainingStockValue;
    private Integer remainingStockQuantity;
    private String reportType; // "TODAY", "YESTERDAY", or specific date

    // Getters and Setters

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public Double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(Double totalSales) {
        this.totalSales = totalSales;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Integer getTotalProductsSold() {
        return totalProductsSold;
    }

    public void setTotalProductsSold(Integer totalProductsSold) {
        this.totalProductsSold = totalProductsSold;
    }

    public Double getRemainingStockValue() {
        return remainingStockValue;
    }

    public void setRemainingStockValue(Double remainingStockValue) {
        this.remainingStockValue = remainingStockValue;
    }

    public Integer getRemainingStockQuantity() {
        return remainingStockQuantity;
    }

    public void setRemainingStockQuantity(Integer remainingStockQuantity) {
        this.remainingStockQuantity = remainingStockQuantity;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }
}