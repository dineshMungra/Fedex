package com.fedex.aggregationapi.model;

public class Pricing {
    String countryCode;
    double amount;

    public Pricing(String countryCode, double amount) {
        this.countryCode = countryCode;
        this.amount = amount;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
