package KiemTra.model;

import java.math.BigDecimal;

public class Account {
    private String accountId;
    private String fullName;
    private BigDecimal balance;

    public Account() {
    }

    public Account(String accountId, String fullName, BigDecimal balance) {
        this.accountId = accountId;
        this.fullName = fullName;
        this.balance = balance;
    }


    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return String.format("| %-8s | %-20s | %,15.2f |", accountId, fullName, balance);
    }

}
