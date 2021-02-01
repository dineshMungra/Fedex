package com.fedex.aggregationapi.service;

import com.fedex.aggregationapi.model.FedexApiResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public abstract class AbstractFedexApiService {
    @Autowired
    private RestTemplate restTemplate;

    protected String endpoint;

    public FedexApiResponseData getResultsForIds(List<String> requestIds) {
        StringBuilder sb = new StringBuilder("?q=");
        for(String requestId : requestIds) {
            sb.append(requestId);
            if (requestIds.lastIndexOf(requestId) != requestIds.size() -1) {
                sb.append(",");
            }
        }

        ResponseEntity<FedexApiResponseData> response = restTemplate.getForEntity(
                endpoint + sb.toString(), FedexApiResponseData.class);

        return response.getBody();
    }

    // fill in from specific service child class
    protected void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
