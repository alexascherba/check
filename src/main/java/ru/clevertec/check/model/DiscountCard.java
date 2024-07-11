package ru.clevertec.check.model;

public class DiscountCard {
    private int id;
    private int number;
    private double discountAmount;

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "DiscountCard{" +
                "id=" + id +
                ", number=" + number +
                ", discountAmount=" + discountAmount +
                '}';
    }
}