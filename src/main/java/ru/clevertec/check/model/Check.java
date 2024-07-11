package ru.clevertec.check.model;

import java.time.LocalDateTime;
import java.util.List;

public class Check {
    private List<Product> products;
    private DiscountCard discountCard;
    private double balanceDebitCard;
    private double totalPrice;
    private double totalDiscount;
    private double totalWithDiscount;
    private LocalDateTime dateTime;

    public Check() {
        this.dateTime = LocalDateTime.now();
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public DiscountCard getDiscountCard() {
        return discountCard;
    }

    public void setDiscountCard(DiscountCard discountCard) {
        this.discountCard = discountCard;
    }

    public double getBalanceDebitCard() {
        return balanceDebitCard;
    }

    public void setBalanceDebitCard(double balanceDebitCard) {
        this.balanceDebitCard = balanceDebitCard;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public double getTotalWithDiscount() {
        return totalWithDiscount;
    }

    public void setTotalWithDiscount(double totalWithDiscount) {
        this.totalWithDiscount = totalWithDiscount;
    }

    @Override
    public String toString() {
        return "Check{" +
                "products=" + products +
                ", discountCard=" + discountCard +
                ", balanceDebitCard=" + balanceDebitCard +
                ", totalPrice=" + totalPrice +
                ", totalDiscount=" + totalDiscount +
                ", totalWithDiscount=" + totalWithDiscount +
                ", dateTime=" + dateTime +
                '}';
    }
}