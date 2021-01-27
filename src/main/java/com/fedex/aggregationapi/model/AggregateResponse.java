package com.fedex.aggregationapi.model;

import java.util.List;

public class AggregateResponse {
    private List<Pricing> pricing;
    private List<TrackingStatus> track;
    private List<Shipment> shipments;

    public AggregateResponse(List<Pricing> pricing, List<TrackingStatus> track, List<Shipment> shipments) {
        this.pricing = pricing;
        this.track = track;
        this.shipments = shipments;
    }

    public List<Pricing> getPricing() {
        return pricing;
    }

    public void setPricing(List<Pricing> pricing) {
        this.pricing = pricing;
    }

    public List<TrackingStatus> getTrack() {
        return track;
    }

    public void setTrack(List<TrackingStatus> track) {
        this.track = track;
    }

    public List<Shipment> getShipments() {
        return shipments;
    }

    public void setShipments(List<Shipment> shipments) {
        this.shipments = shipments;
    }
}
