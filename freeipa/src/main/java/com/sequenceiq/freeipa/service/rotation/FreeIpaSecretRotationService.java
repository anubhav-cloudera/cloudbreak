package com.sequenceiq.freeipa.service.rotation;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.auth.altus.EntitlementService;
import com.sequenceiq.cloudbreak.common.event.Acceptable;
import com.sequenceiq.cloudbreak.common.event.Selectable;
import com.sequenceiq.cloudbreak.common.exception.BadRequestException;
import com.sequenceiq.cloudbreak.rotation.SecretType;
import com.sequenceiq.cloudbreak.rotation.SecretTypeConverter;
import com.sequenceiq.cloudbreak.rotation.flow.chain.SaltUpdateFlowEventProvider;
import com.sequenceiq.cloudbreak.rotation.flow.chain.SecretRotationFlowChainTriggerEvent;
import com.sequenceiq.cloudbreak.rotation.service.SecretRotationValidationService;
import com.sequenceiq.cloudbreak.rotation.service.multicluster.MultiClusterRotationValidationService;
import com.sequenceiq.flow.api.model.FlowIdentifier;
import com.sequenceiq.flow.event.EventSelectorUtil;
import com.sequenceiq.freeipa.api.v1.freeipa.stack.model.rotate.FreeIpaSecretRotationRequest;
import com.sequenceiq.freeipa.entity.Stack;
import com.sequenceiq.freeipa.flow.freeipa.salt.update.SaltUpdateTriggerEvent;
import com.sequenceiq.freeipa.service.freeipa.flow.FreeIpaFlowManager;
import com.sequenceiq.freeipa.service.stack.StackService;

@Service
public class FreeIpaSecretRotationService implements SaltUpdateFlowEventProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(FreeIpaSecretRotationService.class);

    @Inject
    private StackService stackService;

    @Inject
    private FreeIpaFlowManager flowManager;

    @Inject
    private EntitlementService entitlementService;

    @Inject
    private SecretRotationValidationService secretRotationValidationService;

    @Inject
    private MultiClusterRotationValidationService multiClusterRotationValidationService;

    public FlowIdentifier rotateSecretsByCrn(String accountId, String environmentCrn, FreeIpaSecretRotationRequest request) {
        LOGGER.info("Requested secret rotation. Account id: {}, environment crn: {}, request: {}", accountId, environmentCrn, request);
        if (!entitlementService.isSecretRotationEnabled(accountId)) {
            throw new BadRequestException("Account is not entitled to execute secret rotation.");
        }
        Stack stack = stackService.getByEnvironmentCrnAndAccountId(environmentCrn, accountId);
        List<SecretType> secretTypes = SecretTypeConverter.mapSecretTypes(request.getSecrets());
        secretTypes.stream().filter(SecretType::multiSecret).forEach(secretType ->
                multiClusterRotationValidationService.validateMultiRotationRequest(environmentCrn, secretType));
        secretRotationValidationService.validateExecutionType(environmentCrn, secretTypes, request.getExecutionType());
        String selector = EventSelectorUtil.selector(SecretRotationFlowChainTriggerEvent.class);
        Acceptable triggerEvent = new SecretRotationFlowChainTriggerEvent(
                selector, stack.getId(), stack.getEnvironmentCrn(), secretTypes, request.getExecutionType());
        return flowManager.notify(selector, triggerEvent);
    }

    @Override
    public Selectable getSaltUpdateTriggerEvent(SecretRotationFlowChainTriggerEvent event) {
        return new SaltUpdateTriggerEvent(event.getResourceId(), event.accepted(), true, false, null);
    }
}
