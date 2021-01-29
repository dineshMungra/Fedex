package com.fedex.aggregationapi.serviceadapter;

import com.fedex.aggregationapi.broker.ServiceBroker;
import com.fedex.aggregationapi.model.FedexApiResponseData;
import com.fedex.aggregationapi.service.PricingService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class PricingServiceAdapter {
    @Autowired
    private PricingService pricingService;

    @Autowired
    private ServiceBroker pricingServiceBroker;

    private ExecutorService executor
            = Executors.newSingleThreadExecutor();

    public Future<FedexApiResponseData> supplyRequestAndWaitForResponse(int hashCode, List<String> ids) {
        return executor.submit(new Callable<FedexApiResponseData>(){
            @Override
            public FedexApiResponseData call() throws Exception {
                if (allIdsAreKeysInResultMap(ids)) {
                    FedexApiResponseData fedexApiResponseData = getResponseDataFromCache(ids);
                    return fedexApiResponseData;
                }

                pricingServiceBroker.getIdQueue().addAll(ids);
                pricingServiceBroker.getRequestFulfilledMap().put(hashCode, false);

                if (pricingServiceBroker.getIdQueue().size() >= 5) {
                    List<String> idList = List.copyOf(pricingServiceBroker.getIdQueue());
                    FedexApiResponseData pricesForCountryCodes = pricingService.getPricesForCountryCodes(idList);

                    // add result of this request to overall result map
                    pricesForCountryCodes.keySet().stream().forEach(
                            key -> pricingServiceBroker.getResultMap().put(key, pricesForCountryCodes.get(key))
                    );
                    pricingServiceBroker.resetIdQueue(); // reset id queue
                    // new results added to overall map, check if pending requests are now fulfilled.
                    updateRequestFulfilledMap(ids);
                    if (allIdsAreKeysInResultMap(ids)) {
                        FedexApiResponseData fedexApiResponseData = getResponseDataFromCache(ids);
                        return fedexApiResponseData;
                    }
                }
                // block unit all values have been added to the cache by future calls.
                while(!allIdsAreKeysInResultMap(ids)) {};
                FedexApiResponseData fedexApiResponseData = getResponseDataFromCache(ids);
                return fedexApiResponseData;
            }
        });
    }

    private FedexApiResponseData getResponseDataFromCache(List<String> pricing) {
        FedexApiResponseData fedexApiResponseData = new FedexApiResponseData();
        pricing.stream().forEach(
                id -> fedexApiResponseData.put(id, pricingServiceBroker.getResultMap().get(id))
        );
        return fedexApiResponseData;
    }

    private void updateRequestFulfilledMap(List<String> pricing) {
        // if all ids are keys of the resultMap, that means all pricings for the request
        // are already in.
        if (allIdsAreKeysInResultMap(pricing)) {
            pricingServiceBroker.getRequestFulfilledMap().put(pricing.hashCode(), true);
        }
    }

    private boolean allIdsAreKeysInResultMap(List<String> pricing) {
        return pricing.stream()
                .filter(countryCode -> !pricingServiceBroker.getResultMap().keySet().contains(countryCode))
                .collect(Collectors.toList())
                .isEmpty();
    }

}
