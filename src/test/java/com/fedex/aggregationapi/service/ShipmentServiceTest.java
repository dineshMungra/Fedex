package com.fedex.aggregationapi.service;

import com.fedex.aggregationapi.model.FedexApiResponseData;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ShipmentServiceTest {

    @Autowired
    ShipmentService shipmentService;

    @MockBean
    private RestTemplate mockRestTemplate;

    @Test
    public void testHappyFlow() {

        ResponseEntity<FedexApiResponseData> expectedShipmentsResponse = setupExpectedShipmentResult();

        when(mockRestTemplate.getForEntity(anyString(), eq(FedexApiResponseData.class)))
                .thenReturn(expectedShipmentsResponse);

        FedexApiResponseData shipments =
                shipmentService.getShipmentsForIds(Arrays.asList("109347263","123456891"));

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockRestTemplate).getForEntity(urlCaptor.capture(), eq(FedexApiResponseData.class));

        assertEquals("http://localhost:8080/shipments?q=109347263,123456891", urlCaptor.getValue());
        assertEquals(expectedShipmentsResponse.getBody(), shipments);
    }

    private ResponseEntity<FedexApiResponseData> setupExpectedShipmentResult() {
        FedexApiResponseData fedexApiResponseData = new FedexApiResponseData();
        fedexApiResponseData.put("109347263", new String[]{"box", "box", "pallet"});
        fedexApiResponseData.put("123456891", new String[]{"envelope"});

        ResponseEntity<FedexApiResponseData> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getBody()).thenReturn(fedexApiResponseData);
        return responseEntity;
    }
}
