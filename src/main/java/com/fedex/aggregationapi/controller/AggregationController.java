package com.fedex.aggregationapi.controller;

import com.fedex.aggregationapi.model.FedexApiResponseData;
import com.fedex.aggregationapi.service.PricingService;
import com.fedex.aggregationapi.service.ShipmentService;
import com.fedex.aggregationapi.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/aggregation")
public class AggregationController {

    @Autowired
    ShipmentService shipmentService;
    @Autowired
    PricingService pricingService;
    @Autowired
    TrackService trackService;

    @GetMapping
    @ResponseBody
    public Map<String, FedexApiResponseData> getAggregation(@RequestParam List<String> pricing,
                              @RequestParam("track") List<String> track,
                              @RequestParam("shipments") List<String> shipments) {

        Map<String, FedexApiResponseData> response = new HashMap<>();

        FedexApiResponseData pricingResults = pricingService.getPricesForCountryCodes(pricing);
        response.put("pricing", pricingResults);

        FedexApiResponseData trackResults = trackService.getTracksForIds(track);
        response.put("track", trackResults);

        FedexApiResponseData shipmentResults = shipmentService.getShipmentsForIds(shipments);
        response.put("shipments", shipmentResults);

        return response;
    }
}
