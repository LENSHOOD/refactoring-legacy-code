package cn.xpbootcamp.legacy_code.entity;

import java.util.UUID;

public class User {
    private long id;
    private double balance;

    public User() {
        id = UUID.randomUUID().getMostSignificantBits();
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public long getId() {
        return id;
    }
}
