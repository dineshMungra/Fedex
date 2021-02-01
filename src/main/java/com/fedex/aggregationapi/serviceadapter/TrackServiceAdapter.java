package com.fedex.aggregationapi.serviceadapter;

import com.fedex.aggregationapi.cache.ServiceCache;
import com.fedex.aggregationapi.model.FedexApiResponseData;
import com.fedex.aggregationapi.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sound.midi.Track;
import java.util.List;

@Component
public class TrackServiceAdapter extends AbstractServiceAdapter {
    @Autowired
    private ServiceCache trackServiceCache;
    @Autowired
    private TrackService trackService;

    @Override
    protected ServiceCache getServiceCache() {
        return trackServiceCache;
    }

    @Autowired
    public void setTrackService(TrackService trackService) {
        this.service = trackService;
    }

}
