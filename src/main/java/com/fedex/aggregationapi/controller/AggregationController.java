package com.fedex.aggregationapi.controller;

import com.fedex.aggregationapi.model.AggregateResponse;
import com.fedex.aggregationapi.model.Shipment;
import com.fedex.aggregationapi.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@RestController
@RequestMapping("/aggregation")
public class AggregationController {

    @Autowired
    ShipmentService shipmentService;

    @GetMapping
    @ResponseBody
    public Flux getAggregation(@RequestParam List<String> pricing, @RequestParam("track") List<Long> track, @RequestParam("shipments") List<Long> shipments) {

        System.out.println(pricing);
        System.out.println(track);
        System.out.println(shipments);

        Flux<Shipment> shipmentFlux = shipmentService.getShipmentsForIds(shipments);
        List responseList = new ArrayList();
        responseList.add(shipmentFlux);
        Flux responseFlux = Flux.concat(shipmentFlux);
        return responseFlux;
    }
}
