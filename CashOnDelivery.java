package com.example.onlinebookshop.service;

public class CashOnDelivery implements PaymentStrategy {
    @Override
    public String pay(double amount) {
        return "Pay €" + amount + " in cash upon delivery.";
    }
}
