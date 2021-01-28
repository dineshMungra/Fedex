package com.fedex.aggregationapi.service;

import com.fedex.aggregationapi.model.FedexApiResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TrackService {

    @Autowired
    private RestTemplate restTemplate;

    public FedexApiResponseData getTracksForIds(List<String> track) {
        StringBuilder sb = new StringBuilder("?q=");
        for(String productOrderNumber : track) {
            sb.append(productOrderNumber);
            if (track.lastIndexOf(productOrderNumber) != track.size() -1) {
                sb.append(",");
            }
        }

        ResponseEntity<FedexApiResponseData> shipments = restTemplate.getForEntity(
                "http://localhost:8080/track" + sb.toString(), FedexApiResponseData.class);

        return shipments.getBody();
    }
}
