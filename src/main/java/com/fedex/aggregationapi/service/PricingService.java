package com.fedex.aggregationapi.service;

import com.fedex.aggregationapi.model.FedexApiResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class PricingService extends AbstractFedexApiService {

    @Value("${endpointurl.pricingapi}")
    public void setShipmentEndpoint(String pricingEndpoint) {
        this.setEndpoint(pricingEndpoint);
    }

}
