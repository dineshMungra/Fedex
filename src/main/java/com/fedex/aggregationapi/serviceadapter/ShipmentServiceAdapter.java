package com.fedex.aggregationapi.serviceadapter;

import com.fedex.aggregationapi.cache.ServiceCache;
import com.fedex.aggregationapi.model.FedexApiResponseData;
import com.fedex.aggregationapi.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShipmentServiceAdapter extends AbstractServiceAdapter {
    @Autowired
    private ServiceCache shipmentServiceCache;

    @Override
    protected ServiceCache getServiceCache() {
        return shipmentServiceCache;
    }

    @Autowired
    public void setShipmentService(ShipmentService shipmentService) {
        this.setService(shipmentService);
    }
}
