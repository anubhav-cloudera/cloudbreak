package com.sequenceiq.cloudbreak.reactor.handler.cluster;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.core.cluster.ClusterUpscaleService;
import com.sequenceiq.cloudbreak.eventbus.Event;
import com.sequenceiq.cloudbreak.eventbus.EventBus;
import com.sequenceiq.cloudbreak.reactor.api.event.cluster.ClusterManagerEnsureComponentsAreStoppedResult;
import com.sequenceiq.cloudbreak.reactor.api.event.cluster.EnsureClusterComponentsAreStoppedRequest;
import com.sequenceiq.flow.event.EventSelectorUtil;
import com.sequenceiq.flow.reactor.api.handler.EventHandler;

@Component
public class AmbariEnsureComponentsAreStoppedHandler implements EventHandler<EnsureClusterComponentsAreStoppedRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AmbariEnsureComponentsAreStoppedHandler.class);

    @Inject
    private EventBus eventBus;

    @Inject
    private ClusterUpscaleService clusterUpscaleService;

    @Override
    public String selector() {
        return EventSelectorUtil.selector(EnsureClusterComponentsAreStoppedRequest.class);
    }

    @Override
    public void accept(Event<EnsureClusterComponentsAreStoppedRequest> event) {
        EnsureClusterComponentsAreStoppedRequest request = event.getData();
        Long stackId = request.getResourceId();
        ClusterManagerEnsureComponentsAreStoppedResult result;
        try {
            clusterUpscaleService.ensureComponentsAreStopped(stackId, request.getComponents(), request.getHostName());
            result = new ClusterManagerEnsureComponentsAreStoppedResult(request);
        } catch (Exception e) {
            String message = "Failed to gather installed components on host";
            LOGGER.error(message, e);
            result = new ClusterManagerEnsureComponentsAreStoppedResult(message, e, request);
        }
        eventBus.notify(result.selector(), new Event<>(event.getHeaders(), result));

    }
}
