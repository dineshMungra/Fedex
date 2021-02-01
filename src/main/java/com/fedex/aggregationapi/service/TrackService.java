package com.fedex.aggregationapi.service;

import com.fedex.aggregationapi.model.FedexApiResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TrackService extends AbstractFedexApiService {

    @Value("${endpointurl.trackapi}")
    public void setShipmentEndpoint(String trackEndpoint) {
        this.setEndpoint(trackEndpoint);
    }
}
