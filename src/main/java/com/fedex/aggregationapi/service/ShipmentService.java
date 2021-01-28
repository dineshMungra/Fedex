package com.fedex.aggregationapi.service;

import com.fedex.aggregationapi.model.FedexApiResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ShipmentService {
    @Autowired
    private RestTemplate restTemplate;

    public FedexApiResponseData getShipmentsForIds(List<String> productOrderNumbers) {
        StringBuilder sb = new StringBuilder("?q=");
        for(String productOrderNumber : productOrderNumbers) {
            sb.append(productOrderNumber);
            if (productOrderNumbers.lastIndexOf(productOrderNumber) != productOrderNumbers.size() -1) {
                sb.append(",");
            }
        }

        ResponseEntity<FedexApiResponseData> shipments = restTemplate.getForEntity(
                "http://localhost:8080/shipments" + sb.toString(), FedexApiResponseData.class);

        return shipments.getBody();
    }
}
