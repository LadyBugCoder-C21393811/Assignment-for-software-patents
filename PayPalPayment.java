package com.example.onlinebookshop.service;

public class PayPalPayment implements PaymentStrategy {
    @Override
    public String pay(double amount) {
        return "Paid €" + amount + " using PayPalPayment.";
    }
}
