package com.sequenceiq.distrox.v1.distrox.controller;

import static com.sequenceiq.authorization.resource.AuthorizationResourceAction.ROTATE_DH_SECRETS;
import static com.sequenceiq.authorization.resource.AuthorizationVariableType.CRN;
import static com.sequenceiq.cloudbreak.auth.crn.CrnResourceDescriptor.DATALAKE;
import static com.sequenceiq.cloudbreak.auth.crn.CrnResourceDescriptor.ENVIRONMENT;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import com.sequenceiq.authorization.annotation.CheckPermissionByRequestProperty;
import com.sequenceiq.authorization.annotation.InternalOnly;
import com.sequenceiq.authorization.annotation.RequestObject;
import com.sequenceiq.cloudbreak.auth.security.internal.InitiatorUserCrn;
import com.sequenceiq.cloudbreak.rotation.annotation.ValidMultiSecretType;
import com.sequenceiq.cloudbreak.service.stack.flow.StackRotationService;
import com.sequenceiq.cloudbreak.validation.ValidCrn;
import com.sequenceiq.distrox.api.v1.distrox.endpoint.DistroXV1RotationEndpoint;
import com.sequenceiq.distrox.api.v1.distrox.model.DistroXChildResourceMarkingRequest;
import com.sequenceiq.distrox.api.v1.distrox.model.DistroXSecretRotationRequest;
import com.sequenceiq.flow.api.model.FlowIdentifier;

@Controller
public class DistroXV1RotationController implements DistroXV1RotationEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(DistroXV1RotationController.class);

    @Inject
    private StackRotationService stackRotationService;

    @Override
    @CheckPermissionByRequestProperty(type = CRN, path = "crn", action = ROTATE_DH_SECRETS)
    public FlowIdentifier rotateSecrets(@RequestObject DistroXSecretRotationRequest request) {
        return stackRotationService.rotateSecrets(request.getCrn(), request.getSecrets(), request.getExecutionType());
    }

    @Override
    @InternalOnly
    public boolean checkOngoingChildrenMultiSecretRotationsByParent(@ValidCrn(resource = { ENVIRONMENT, DATALAKE }) String parentCrn,
            @ValidMultiSecretType String multiSecret,
            @InitiatorUserCrn String initiatorUserCrn) {
        return stackRotationService.checkOngoingChildrenMultiSecretRotations(parentCrn, multiSecret);
    }

    @Override
    @InternalOnly
    public void markMultiClusterChildrenResourcesByParent(@Valid DistroXChildResourceMarkingRequest request,
            @InitiatorUserCrn String initiatorUserCrn) {
        stackRotationService.markMultiClusterChildrenResources(request.getParentCrn(), request.getSecret());
    }
}
