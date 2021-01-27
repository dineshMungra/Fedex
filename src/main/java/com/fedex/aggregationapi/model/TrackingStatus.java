package com.fedex.aggregationapi.model;

/**
 * Models the result of a query to the Tracking API.
 * A TrackingStatus contains the status of an order.
 */
public class TrackingStatus {
    private long orderNumber;
    private TrackingStatusType status;

    public TrackingStatus(long orderNumber, TrackingStatusType status) {
        this.orderNumber = orderNumber;
        this.status = status;
    }

    public long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public TrackingStatusType getStatus() {
        return status;
    }

    public void setStatus(TrackingStatusType status) {
        this.status = status;
    }
}
