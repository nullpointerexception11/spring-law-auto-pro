package com.lawauto.backend.operations;

public enum OutboxStatus {
    PENDING,
    SENT,
    FAILED,
    CANCELLED
}
