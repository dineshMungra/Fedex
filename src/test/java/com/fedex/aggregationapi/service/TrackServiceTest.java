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
public class TrackServiceTest {

    @Autowired
    TrackService trackService;
    @MockBean
    private RestTemplate mockRestTemplate;

    @Test
    public void testHappyFlow() {

        ResponseEntity<FedexApiResponseData> expectedShipmentsResponse = setupExpectedShipmentResult();

        when(mockRestTemplate.getForEntity(anyString(), eq(FedexApiResponseData.class)))
                .thenReturn(expectedShipmentsResponse);

        FedexApiResponseData tracks =
                trackService.getResultsForIds(Arrays.asList("1234567","7654321","653543214"));

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockRestTemplate).getForEntity(urlCaptor.capture(), eq(FedexApiResponseData.class));

        assertEquals("http://localhost:8080/track?q=1234567,7654321,653543214",
                urlCaptor.getValue());
        assertEquals(expectedShipmentsResponse.getBody(), tracks);
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
