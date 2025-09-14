package com.example.ticketsystem.model;

import java.sql.Timestamp;
import java.util.UUID;

public class Ticket {
    private UUID id;
    private String acquisionerName;
    private String transactionerName;
    private String clientInfo;
    private String ibsNumber;
    private String status;
    private Timestamp createdAt;

    public Ticket() {
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAcquisionerName() {
        return acquisionerName;
    }

    public void setAcquisionerName(String acquisionerName) {
        this.acquisionerName = acquisionerName;
    }

    public String getTransactionerName() {
        return transactionerName;
    }

    public void setTransactionerName(String transactionerName) {
        this.transactionerName = transactionerName;
    }

    public String getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(String clientInfo) {
        this.clientInfo = clientInfo;
    }

    public String getIbsNumber() {
        return ibsNumber;
    }

    public void setIbsNumber(String ibsNumber) {
        this.ibsNumber = ibsNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}