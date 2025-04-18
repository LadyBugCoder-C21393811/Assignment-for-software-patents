package com.example.onlinebookshop.service;

public class VisaPayment implements PaymentStrategy {
    @Override
    public String pay(double amount) {
        return "Paid €" + amount + " using VisaPayment.";
    }
}
