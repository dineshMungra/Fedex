package com.fedex.aggregationapi.service;

import com.fedex.aggregationapi.model.Shipment;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class ShipmentService {
    private WebClient webClient = WebClient.create("http://localhost:8080/shipments");

    public Flux<Shipment> getShipmentsForIds(List<Long> productOrderNumbers) {
        Flux<Shipment> shipments = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("")
                        .queryParam("q", productOrderNumbers)
                        .build())
                .retrieve()
                .bodyToFlux(Shipment.class);
        return shipments;
    }
}
