package com.fedex.aggregationapi.serviceadapter;

import com.fedex.aggregationapi.cache.ServiceCache;
import com.fedex.aggregationapi.model.FedexApiResponseData;
import com.fedex.aggregationapi.service.AbstractFedexApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class AbstractServiceAdapterTest {

    private ServiceCache testServiceCache = new ServiceCache();

    private AbstractFedexApiService mockService = mock(AbstractFedexApiService.class);
    private TestServiceAdapter testServiceAdapter = new TestServiceAdapter();
    private TestServiceAdapter otherTestServiceAdapter = new TestServiceAdapter();

    private FedexApiResponseData mockCallResult = new FedexApiResponseData();

    @BeforeEach
    public void setupMockService() {
        when(mockService.getResultsForIds(anyList())).thenReturn(mockCallResult);
    }
    @Test
    public void testProcessRequest_ResultsForAllIdsInCache() throws InterruptedException {
        List<String> ids = Arrays.asList("12345", "67890");

        // set cache content, three results, two of which are for the ids above
        FedexApiResponseData fedexApiResponseData12345 = new FedexApiResponseData();
        FedexApiResponseData fedexApiResponseData67890 = new FedexApiResponseData();
        FedexApiResponseData fedexApiResponseData54321 = new FedexApiResponseData();

        fedexApiResponseData12345.put("12345", "[\"box\", \"pallet\", \"envelope\"]");
        fedexApiResponseData67890.put("67890", "[\"pallet\", \"pallet\"]");
        fedexApiResponseData54321.put("54321", "[\"envelope\", \"pallet\"]");

        testServiceCache.getResultMap().putAll(fedexApiResponseData12345);
        testServiceCache.getResultMap().putAll(fedexApiResponseData67890);
        testServiceCache.getResultMap().putAll(fedexApiResponseData54321);

        FedexApiResponseData adapterResponse = testServiceAdapter.processRequestAndWaitForResponse(ids);

        assertNotNull(adapterResponse);
        assertTrue(adapterResponse.keySet().size() == 2);
        assertEquals("[\"box\", \"pallet\", \"envelope\"]", adapterResponse.get("12345"));
        assertEquals("[\"pallet\", \"pallet\"]", adapterResponse.get("67890"));
        assertNull(adapterResponse.get("54321")); // no other results in the response
    }

    /*
     * Een unit test voor als bulk request wordt uitgevoerd en alle resultaten voor het meegegeven request
     * daarna binnen zijn; en een unit test voor als 1 client moet wachten, totdat een andere client na een bulk request
     * de resultaten die client 1 nodig heeft in de cache heeft gedaan.
     */

    @Test
    public void testProcessRequest_ResultsCompleteAfterBulkRequest() throws InterruptedException {
        List<String> ids = Arrays.asList("12345", "67890");

        FedexApiResponseData fedexApiResponseData12345 = new FedexApiResponseData();
        FedexApiResponseData fedexApiResponseData67890 = new FedexApiResponseData();
        FedexApiResponseData fedexApiResponseData54321 = new FedexApiResponseData();
        FedexApiResponseData fedexApiResponseData1010101 = new FedexApiResponseData();

        fedexApiResponseData12345.put("12345", "[\"box\", \"pallet\", \"envelope\"]");
        fedexApiResponseData67890.put("67890", "[\"pallet\", \"pallet\"]");
        fedexApiResponseData54321.put("54321", "[\"envelope\", \"pallet\"]");
        fedexApiResponseData1010101.put("54321", "[\"envelope\"]");

        // set cache content, two results, one for id 12345 above
        testServiceCache.getResultMap().putAll(fedexApiResponseData12345);
        testServiceCache.getResultMap().putAll(fedexApiResponseData54321);

        testServiceCache.getIdQueue().clear();
        testServiceCache.getIdQueue().put("67890");
        testServiceCache.getIdQueue().put("1010101");
        testServiceCache.getIdQueue().put("2020202");
        testServiceCache.getIdQueue().put("3030303");
        testServiceCache.getIdQueue().put("4040404");

        // setup mock call result to return the other result, for 67890
        mockCallResult.putAll(fedexApiResponseData67890);
        mockCallResult.putAll(fedexApiResponseData1010101);

        FedexApiResponseData adapterResponse = testServiceAdapter.processRequestAndWaitForResponse(ids);
        assertNotNull(adapterResponse);
    }

    /**
     * First do a request that triggers a bulk request but will not have all the results it need so it blocks waiting.
     * Then do a second request that also triggers a bulk request which completes the results for the first request.
     * The first request should unblock and return a result.
     *
     * @throws InterruptedException request processing thread interrupted
     * @throws ExecutionException exception from within request processing thread
     */
    @Test
    public void testProcessRequest_ResultOfClientACompleteAfterBulkReqFromClientB() throws InterruptedException, ExecutionException {
        // setup test data
        List<String> ids = Arrays.asList("12345", "67890");

        FedexApiResponseData fedexApiResponseData12345 = new FedexApiResponseData();
        FedexApiResponseData fedexApiResponseData67890 = new FedexApiResponseData();
        FedexApiResponseData fedexApiResponseData54321 = new FedexApiResponseData();
        FedexApiResponseData fedexApiResponseData1010101 = new FedexApiResponseData();
        FedexApiResponseData fedexApiResponseData2020202 = new FedexApiResponseData();
        FedexApiResponseData fedexApiResponseData3030303 = new FedexApiResponseData();
        FedexApiResponseData fedexApiResponseData4040404 = new FedexApiResponseData();

        FedexApiResponseData fedexApiResponseData77777 = new FedexApiResponseData();
        FedexApiResponseData fedexApiResponseData66666 = new FedexApiResponseData();
        FedexApiResponseData fedexApiResponseData55555 = new FedexApiResponseData();
        FedexApiResponseData fedexApiResponseData44444 = new FedexApiResponseData();

        fedexApiResponseData12345.put("12345", "[\"box\", \"pallet\", \"envelope\"]");
        fedexApiResponseData67890.put("67890", "[\"pallet\", \"pallet\"]");
        fedexApiResponseData54321.put("54321", "[\"envelope\", \"pallet\"]");
        fedexApiResponseData1010101.put("1010101", "[\"envelope\"]");
        fedexApiResponseData2020202.put("2020202", "[\"envelope\"]");
        fedexApiResponseData3030303.put("3030303", "[\"envelope\"]");
        fedexApiResponseData4040404.put("4040404", "[\"envelope\"]");

        fedexApiResponseData77777.put("67890", "[\"pallet\", \"pallet\"]");
        fedexApiResponseData66666.put("66666", "[\"pallet\", \"pallet\"]");
        fedexApiResponseData55555.put("55555", "[\"pallet\", \"pallet\"]");
        fedexApiResponseData44444.put("44444", "[\"pallet\", \"pallet\"]");

        // set cache content, no results in yet, no ids in queue.
        testServiceCache.getResultMap().clear();
        testServiceCache.resetIdQueue();

        // Make sure the id cache is filled to full capacity, to trigger a bulk request.
        testServiceCache.getIdQueue().put("67890");
        testServiceCache.getIdQueue().put("1010101");
        testServiceCache.getIdQueue().put("2020202");
        testServiceCache.getIdQueue().put("3030303");
        testServiceCache.getIdQueue().put("4040404");

        // setup mock call result to return the other result, for 67890
        mockCallResult.putAll(fedexApiResponseData67890);
        mockCallResult.putAll(fedexApiResponseData1010101);
        mockCallResult.putAll(fedexApiResponseData2020202);
        mockCallResult.putAll(fedexApiResponseData3030303);
        mockCallResult.putAll(fedexApiResponseData4040404);

        // adapter 1 does bulk call and updates cache. Its own request is not fulfilled yet however, so it blocks.
        // The request still needs the result for 12345
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        var adapter1ResponseFuture = executorService.submit(() -> testServiceAdapter.processRequestAndWaitForResponse(ids));
        //Thread.sleep(5000);
        // simulate queue filling up again
        testServiceCache.getIdQueue().put("12345");
        testServiceCache.getIdQueue().put("777777");
        testServiceCache.getIdQueue().put("666666");
        testServiceCache.getIdQueue().put("555555");
        testServiceCache.getIdQueue().put("444444");

        // setup test response for bulk request 2
        mockCallResult.clear();
        mockCallResult.putAll(fedexApiResponseData12345); // the result that adapter 1 needs
        mockCallResult.putAll(fedexApiResponseData77777);
        mockCallResult.putAll(fedexApiResponseData66666);
        mockCallResult.putAll(fedexApiResponseData55555);
        mockCallResult.putAll(fedexApiResponseData44444);

        // adapter 2 does bulk call and the remaining results for adapter 1 are now in
        //ExecutorService executorService2 = Executors.newSingleThreadExecutor();
        List<String> ids2 = Arrays.asList("67890"); // result already in cache so no blocking
        FedexApiResponseData adapter2Response = otherTestServiceAdapter.processRequestAndWaitForResponse(ids2);

        // adapter 1 should unblock and return result
        FedexApiResponseData fedexApiResponseData = adapter1ResponseFuture.get();
        assertNotNull(fedexApiResponseData.get("12345"));
        assertNotNull(fedexApiResponseData.get("67890"));
        assertEquals(fedexApiResponseData12345.get("12345"), fedexApiResponseData.get("12345"));
        assertEquals(fedexApiResponseData67890.get("67890"), fedexApiResponseData.get("67890"));
    }

    class TestServiceAdapter extends AbstractServiceAdapter {

        public TestServiceAdapter() {
            setService(mockService);
        }
        @Override
        protected ServiceCache getServiceCache() {
            return testServiceCache;
        }

    }

}
