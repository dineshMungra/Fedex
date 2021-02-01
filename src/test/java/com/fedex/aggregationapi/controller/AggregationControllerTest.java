package com.fedex.aggregationapi.controller;

import com.fedex.aggregationapi.model.FedexApiResponseData;
import com.fedex.aggregationapi.serviceadapter.PricingServiceAdapter;
import com.fedex.aggregationapi.serviceadapter.ShipmentServiceAdapter;
import com.fedex.aggregationapi.serviceadapter.TrackServiceAdapter;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AggregationControllerTest {
   @MockBean
   PricingServiceAdapter pricingServiceAdapter;
   @MockBean
   ShipmentServiceAdapter shipmentServiceAdapter;
   @MockBean
   TrackServiceAdapter trackServiceAdapter;
   @Autowired
   AggregationController aggregationController;

   @Test
   public void testControllerHappyFlow() throws InterruptedException {

       List<String> track = new ArrayList<>();
       track.add("109347263");
       track.add("123456891");

       List<String> shipments = new ArrayList<>();
       shipments.addAll(track); // same ids

       List<String> pricing = new ArrayList<>();
       pricing.add("CN");
       pricing.add("NL");

       FedexApiResponseData pricingFedexApiResponseData = new FedexApiResponseData();
       pricingFedexApiResponseData.put("CN", "503467806384");
       pricingFedexApiResponseData.put("NL", "14. 242090605778");

       FedexApiResponseData shipmentFedexApiResponseData = new FedexApiResponseData();
       shipmentFedexApiResponseData.put("109347263", "[\"box\", \"box\", \"pallet\"]");
       shipmentFedexApiResponseData.put("123456891", "[\"envelope\"]");

       FedexApiResponseData trackFedexApiResponseData = new FedexApiResponseData();
       trackFedexApiResponseData.put("109347263", "NEW");
       trackFedexApiResponseData.put("123456891", "COLLECTING");

       when(pricingServiceAdapter.processRequestAndWaitForResponse(eq(pricing))).thenReturn(pricingFedexApiResponseData);
       when(shipmentServiceAdapter.processRequestAndWaitForResponse(eq(shipments))).thenReturn(shipmentFedexApiResponseData);
       when(trackServiceAdapter.processRequestAndWaitForResponse(eq(track))).thenReturn(trackFedexApiResponseData);

       Map<String, FedexApiResponseData> aggregation = aggregationController.getAggregation(
               pricing, track, shipments);

   }
}
