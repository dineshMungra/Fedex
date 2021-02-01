package com.fedex.aggregationapi.serviceadapter;

import com.fedex.aggregationapi.cache.ServiceCache;
import com.fedex.aggregationapi.model.FedexApiResponseData;
import com.fedex.aggregationapi.service.AbstractFedexApiService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractServiceAdapter {
    private static final int THRESHOLD = 5;
    protected AbstractFedexApiService service;

    protected abstract ServiceCache getServiceCache();
    private LocalDateTime time = LocalDateTime.now();

    /**
     * Returns the service replies for every id supplied, in one response.
     * If all desired results are in the service cache, get them from the cache.
     * If not, add the incomingRequestIds to the id queue.
     * If the queue then exceeds 5 entries, perform a bulk request.
     * @param ids the incomingRequestIds for which we want data from the service.
     * @return a map containing the incomingRequestIds and their corresponding data.
     */
    public FedexApiResponseData processRequestAndWaitForResponse(List<String> ids) throws InterruptedException {
        List<String> idBucket = new ArrayList<>(ids); // arraylist because we need the remove() operation to work
        resetTime();
        boolean timeExpired;

        /*
         * Perform eventual bulk request, add request ids to the queue, loop until all results for this request are in cache.
         */
        do {

            timeExpired = hasTimeExpired();
            if (getServiceCache().getIdQueue().remainingCapacity() == 0 || timeExpired) {

                List<String> idList = new ArrayList<>();
                int nrOfIdsDrained = 0;
                synchronized (getServiceCache().getIdQueue()) {
                    getServiceCache().getIdQueue().drainTo(idList);
                }
                FedexApiResponseData values = service.getResultsForIds(idList);

                // add result of this request to overall result map
                getServiceCache().getResultMap().putAll(values);

            }
            // add ids to queue until all ids are added or queue is full
            while (idBucket.size() > 0 && getServiceCache().getIdQueue().remainingCapacity() > 0) { // queue not full yet; if not all ids of the incoming request added to the id queue already
                // if id not already in service cache result map and id not in queue yet, add it.
                if (!getServiceCache().getIdQueue().contains(idBucket.get(0))
                        && getServiceCache().getResultMap().get(idBucket.get(0)) == null) { // no result in cache yet for this id
                    if (getServiceCache().getIdQueue().isEmpty()) {
                        // this will be first (oldest) id in queue, set timer
                        resetTime();
                    }
                    getServiceCache().putInIdQueue(idBucket.get(0)); // add next id
                }
                idBucket.remove(0); // remove from parameter list

            }
        } while (!allIdsAreKeysInResultMap(ids) && !timeExpired); // loop until a) this thread gets all results needed b) some other thread gets all results needed for this request c) a timeout or interrupt

        // For this request, all the results are present in the cache.
        return getResponseDataFromCache(ids);
    }

    private void resetTime() {
        time = LocalDateTime.now();
    }

    private boolean hasTimeExpired() {
        LocalDateTime currentTime = LocalDateTime.now();
        long msPassed = Duration.between(time, currentTime).toMillis();
        return msPassed >= 5000;
    }

    private FedexApiResponseData getResponseDataFromCache(List<String> ids) {
        FedexApiResponseData fedexApiResponseData = new FedexApiResponseData();
        ids.stream().forEach(
            id -> fedexApiResponseData.put(id, getServiceCache().getResultMap().get(id))
        );
        return fedexApiResponseData;
    }

    private boolean allIdsAreKeysInResultMap(List<String> ids) {
        return ids.stream()
            .filter(id -> !getServiceCache().getResultMap().containsKey(id))
            .count() == 0;
    }

    protected void setService(AbstractFedexApiService service) {
        this.service = service;
    }
}
