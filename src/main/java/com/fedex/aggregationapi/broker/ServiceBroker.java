package com.fedex.aggregationapi.broker;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.util.*;
import java.util.stream.Collectors;

public class ServiceBroker {
    private Set<String> idQueue = new HashSet<>();
    private Map<Object, Object> resultMap = new HashMap<>();
    private Map<Integer, Boolean> requestFulfilledMap = new HashMap<>();

    public void updateRequestFulfilledMap(List<String> ids) {
        // if all ids are keys of the resultMap, that means all pricings for the request
        // are already in.
        if (allIdsAreKeysInResultMap(ids)) {
            requestFulfilledMap.put(ids.hashCode(), true);
        }
    }

    public boolean allIdsAreKeysInResultMap(List<String> ids) {
        return ids.stream()
                .filter(id -> !resultMap.keySet().contains(id))
                .collect(Collectors.toList())
                .isEmpty();
    }

    public Set<String> getIdQueue() {
        return idQueue;
    }

    public Map<Object, Object> getResultMap() {
        return resultMap;
    }

    public Map<Integer, Boolean> getRequestFulfilledMap() {
        return requestFulfilledMap;
    }

    public void resetIdQueue() {
        idQueue = new HashSet<>();
    }
}
