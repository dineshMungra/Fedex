package com.fedex.aggregationapi.cache;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * Cache for the results of a calls to an API service.
 * A service result cache contains the following:
 * <ul>
 *     <li>a queue of ids to use in the next bulk request to the service</li>
 *     <li>a map of ids to results already fetched from the service</li>
 * </ul>
 */
public class ServiceCache {
    private BlockingQueue<String> idQueue = new LinkedBlockingQueue<>(5);
    private Map<Object, Object> resultMap = new HashMap<>();


    public boolean allIdsAreKeysInResultMap(List<String> ids) {
        return ids.stream().noneMatch(id -> !resultMap.containsKey(id));
    }

    public BlockingQueue<String> getIdQueue() {
        return idQueue;
    }

    public Map<Object, Object> getResultMap() {
        return resultMap;
    }

    public void resetIdQueue() {
        idQueue.clear();
    }

    public void invalidateCache() {
        resultMap = new HashMap<>();
    }

    /**
     * Add an id to the queue for the bulk request. Only add the id if it
     * is not already present in the queue.
     *
     * @param id id to add to the queue for the bulk request
     * @throws InterruptedException thread interrupted while blocking to insert into queue
     */
    public void putInIdQueue(String id) throws InterruptedException {
        if (!idQueue.contains(id)) {
            idQueue.put(id);
        }
    }

}
