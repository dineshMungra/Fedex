package com.fedex.aggregationapi.controller;

import com.fedex.aggregationapi.model.FedexApiResponseData;
import com.fedex.aggregationapi.serviceadapter.PricingServiceAdapter;
import com.fedex.aggregationapi.serviceadapter.ShipmentServiceAdapter;
import com.fedex.aggregationapi.serviceadapter.TrackServiceAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.*;

@RestController
@RequestMapping("/aggregation")
public class AggregationController {

    @Autowired
    PricingServiceAdapter pricingServiceAdapter;
    @Autowired
    ShipmentServiceAdapter shipmentServiceAdapter;
    @Autowired
    TrackServiceAdapter trackServiceAdapter;

    @GetMapping
    @ResponseBody
    public Map<String, FedexApiResponseData> getAggregation(@RequestParam List<String> pricing,
                              @RequestParam("track") List<String> track,
                              @RequestParam("shipments") List<String> shipments) {

        Map<String, FedexApiResponseData> response = new HashMap<>();

        // one thread per API (pricing, track, shipments) so the calls can execute in parallel
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        Collection<? extends Callable<Map<String, FedexApiResponseData>>> serviceCalls =
                createListOfApiCalls(pricing, shipments, track);
        List<Future<Map<String, FedexApiResponseData>>> futures = null;

        try {
            futures = executorService.invokeAll(serviceCalls);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(Future<Map<String, FedexApiResponseData>> future : futures) {
            try {
                Map<String, FedexApiResponseData> result = future.get();
                for(String key : result.keySet()) {
                    response.put(key, result.get(key));
                }
            } catch (InterruptedException e) {

            } catch (ExecutionException e) {

            }
        }
        executorService.shutdown();
        return response;
    }

    private Collection<? extends Callable<Map<String,FedexApiResponseData>>> createListOfApiCalls(
            List<String> pricing, List<String> shipments, List<String> track) {

        Callable<Map<String, FedexApiResponseData>> pricingCallable = () -> {
            Map<String, FedexApiResponseData> pricingResult = new HashMap<>();
            pricingResult.put("pricing", pricingServiceAdapter.processRequestAndWaitForResponse(pricing));
            return pricingResult;
        };

        Callable<Map<String, FedexApiResponseData>> shipmentCallable = () -> {
            Map<String, FedexApiResponseData> shipmentResult = new HashMap<>();
            shipmentResult.put("shipment", shipmentServiceAdapter.processRequestAndWaitForResponse(shipments));
            return shipmentResult;
        };

        Callable<Map<String, FedexApiResponseData>> trackCallable = () -> {
            Map<String, FedexApiResponseData> trackResult = new HashMap<>();
            trackResult.put("track", trackServiceAdapter.processRequestAndWaitForResponse(track));
            return trackResult;
        };

        List<Callable<Map<String, FedexApiResponseData>>> apiCalls = new ArrayList<>(
                Arrays.asList(pricingCallable, shipmentCallable, trackCallable));
        return apiCalls;
    }


}
