package com.fedex.aggregationapi.service;

import com.fedex.aggregationapi.model.FedexApiResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class PricingService {

    @Autowired
    private RestTemplate restTemplate;

    public FedexApiResponseData getPricesForCountryCodes(List<String> pricingCountryCodes) {
        StringBuilder sb = new StringBuilder("?q=");
        for(String countryCode : pricingCountryCodes) {
            sb.append(countryCode);
            if (pricingCountryCodes.lastIndexOf(countryCode) != pricingCountryCodes.size() -1) {
                sb.append(",");
            }
        }

        ResponseEntity<FedexApiResponseData> pricings = restTemplate.getForEntity(
                "http://localhost:8080/pricing" + sb.toString(), FedexApiResponseData.class);

        return pricings.getBody();
    }
}
