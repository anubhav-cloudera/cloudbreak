package com.sequenceiq.cloudbreak.rotation.flow.chain;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.common.event.Selectable;
import com.sequenceiq.cloudbreak.rotation.flow.rotation.event.SecretRotationTriggerEvent;
import com.sequenceiq.flow.core.chain.FlowEventChainFactory;
import com.sequenceiq.flow.core.chain.config.FlowTriggerEventQueue;
import com.sequenceiq.flow.event.EventSelectorUtil;

@Component
public class SecretRotationFlowEventChainFactory implements FlowEventChainFactory<SecretRotationFlowChainTriggerEvent> {

    @Inject
    private Optional<SaltUpdateFlowEventProvider> saltUpdateFlowEventProvider;

    @Override
    public String initEvent() {
        return EventSelectorUtil.selector(SecretRotationFlowChainTriggerEvent.class);
    }

    @Override
    public FlowTriggerEventQueue createFlowTriggerEventQueue(SecretRotationFlowChainTriggerEvent event) {
        Queue<Selectable> flowEventChain = new ConcurrentLinkedQueue<>();
        if (saltUpdateFlowEventProvider.isPresent()) {
            SaltUpdateFlowEventProvider eventProvider = saltUpdateFlowEventProvider.get();
            if (eventProvider.saltUpdateNeeded(event)) {
                flowEventChain.add(eventProvider.getSaltUpdateTriggerEvent(event));
            }
        }
        event.getSecretTypes().forEach(secretType -> {
            flowEventChain.add(SecretRotationTriggerEvent.fromChainTrigger(event, secretType));
        });
        return new FlowTriggerEventQueue(getName(), event, flowEventChain);
    }
}
