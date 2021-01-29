package com.fedex.aggregationapi.controller;

import com.fedex.aggregationapi.model.FedexApiResponseData;
import com.fedex.aggregationapi.service.PricingService;
import com.fedex.aggregationapi.service.ShipmentService;
import com.fedex.aggregationapi.service.TrackService;
import com.fedex.aggregationapi.serviceadapter.PricingServiceAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/aggregation")
public class AggregationController {

    @Autowired
    ShipmentService shipmentService;
    @Autowired
    PricingService pricingService;
    @Autowired
    TrackService trackService;

    @Autowired
    PricingServiceAdapter pricingServiceAdapter;

    @GetMapping
    @ResponseBody
    public Map<String, FedexApiResponseData> getAggregation(@RequestParam List<String> pricing,
                              @RequestParam("track") List<String> track,
                              @RequestParam("shipments") List<String> shipments) {

        Map<String, FedexApiResponseData> response = new HashMap<>();
        pricingServiceAdapter.supplyRequestAndWaitForResponse(pricing.hashCode(), pricing);

        // blocks waiting for pricing service response
        Future<FedexApiResponseData> pricingResultsFuture =
                pricingServiceAdapter.supplyRequestAndWaitForResponse(pricing.hashCode(), pricing);

        FedexApiResponseData pricingResults = null;
        // block waiting for response
        try {
            pricingResults = pricingResultsFuture.get();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted waiting for response of Pricing service.", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Error while processing response of Pricing service.", e);
        }

        response.put("pricing", pricingResults);

        FedexApiResponseData trackResults = trackService.getTracksForIds(track);
        response.put("track", trackResults);

        FedexApiResponseData shipmentResults = shipmentService.getShipmentsForIds(shipments);
        response.put("shipments", shipmentResults);

        return response;
    }
}
