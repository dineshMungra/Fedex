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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PricingServiceTest {

    @Autowired
    PricingService pricingService;

    @MockBean
    private RestTemplate mockRestTemplate;

    @Test
    public void testHappyFlow() {

        ResponseEntity<FedexApiResponseData> responseWithExpectedPrices = setupExpectedPricingResult();

        when(mockRestTemplate.getForEntity(anyString(), eq(FedexApiResponseData.class)))
                .thenReturn(responseWithExpectedPrices);

        FedexApiResponseData pricesForCountryCodes =
                pricingService.getResultsForIds(Arrays.asList("NL","SR"));

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockRestTemplate).getForEntity(urlCaptor.capture(), eq(FedexApiResponseData.class));

        assertEquals("http://localhost:8080/pricing?q=NL,SR", urlCaptor.getValue());
        assertEquals(responseWithExpectedPrices.getBody(), pricesForCountryCodes);
    }

    private ResponseEntity<FedexApiResponseData> setupExpectedPricingResult() {
        FedexApiResponseData fedexApiResponseData = new FedexApiResponseData();
        fedexApiResponseData.put("NL", "12.33323323");
        fedexApiResponseData.put("SR", "20.2211321567");

        ResponseEntity<FedexApiResponseData> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getBody()).thenReturn(fedexApiResponseData);
        return responseEntity;
    }
}
