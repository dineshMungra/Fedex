package com.fedex.aggregationapi.serviceadapter;

import com.fedex.aggregationapi.cache.ServiceCache;
import com.fedex.aggregationapi.model.FedexApiResponseData;
import com.fedex.aggregationapi.service.PricingService;
import com.fedex.aggregationapi.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PricingServiceAdapter extends AbstractServiceAdapter {

    @Autowired
    private ServiceCache pricingServiceCache;

    @Override
    protected ServiceCache getServiceCache() {
        return pricingServiceCache;
    }

    @Autowired
    public void setPricingService(ShipmentService pricingService) {
        this.setService(pricingService);
    }
}
