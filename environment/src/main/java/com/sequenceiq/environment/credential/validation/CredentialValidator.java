package com.sequenceiq.environment.credential.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.BadRequestException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.auth.altus.EntitlementService;
import com.sequenceiq.cloudbreak.auth.crn.Crn;
import com.sequenceiq.cloudbreak.cloud.model.Platform;
import com.sequenceiq.cloudbreak.common.json.Json;
import com.sequenceiq.cloudbreak.common.mappable.CloudPlatform;
import com.sequenceiq.cloudbreak.validation.ValidationResult;
import com.sequenceiq.cloudbreak.validation.ValidationResult.ValidationResultBuilder;
import com.sequenceiq.common.model.CredentialType;
import com.sequenceiq.environment.api.v1.credential.model.parameters.aws.AwsCredentialParameters;
import com.sequenceiq.environment.api.v1.credential.model.parameters.aws.RoleBasedParameters;
import com.sequenceiq.environment.api.v1.credential.model.request.CredentialRequest;
import com.sequenceiq.environment.credential.domain.Credential;
import com.sequenceiq.environment.credential.validation.definition.CredentialDefinitionService;

@Component
public class CredentialValidator {

    private final Set<String> enabledPlatforms;

    private final CredentialDefinitionService credentialDefinitionService;

    private final Map<String, ProviderCredentialValidator> providerValidators = new HashMap<>();

    private final EntitlementService entitlementService;

    public CredentialValidator(@Value("${environment.enabledplatforms}") Set<String> enabledPlatforms,
            CredentialDefinitionService credentialDefinitionService,
            List<ProviderCredentialValidator> providerCredentialValidators,
            EntitlementService entitlementService) {
        this.enabledPlatforms = enabledPlatforms;
        this.credentialDefinitionService = credentialDefinitionService;
        providerCredentialValidators.forEach(validator -> providerValidators.put(validator.supportedProvider(), validator));
        this.entitlementService = entitlementService;
    }

    public void validateCreate(CredentialRequest createCredentialRequest) {
        ValidationResultBuilder resultBuilder = new ValidationResultBuilder();
        ValidationResult validationResult = Optional.ofNullable(providerValidators.get(createCredentialRequest.getCloudPlatform()))
                .map(validator -> validator.validateCreate(createCredentialRequest, resultBuilder))
                .orElse(resultBuilder.build());

        if (validationResult.hasError()) {
            throw new BadRequestException(validationResult.getFormattedErrors());
        }
    }

    public void validateParameters(Platform platform, Json json) {
        credentialDefinitionService.checkPropertiesRemoveSensitives(platform, json);
    }

    public void validateCredentialCloudPlatform(String cloudPlatform, String userCrn, CredentialType type) {
        validateCredentialCloudPlatformInternal(cloudPlatform, Crn.safeFromString(userCrn).getAccountId(), type);
    }

    private void validateCredentialCloudPlatformInternal(String cloudPlatform, String accountId, CredentialType type) {
        if (!enabledPlatforms.contains(cloudPlatform)) {
            throw new BadRequestException(String.format("There is no such cloud platform as '%s'", cloudPlatform));
        }
    }

    public boolean isCredentialCloudPlatformValid(String cloudPlatform, String accountId, CredentialType type) {
        try {
            validateCredentialCloudPlatformInternal(cloudPlatform, accountId, type);
            return true;
        } catch (BadRequestException e) {
            return false;
        }
    }

    public ValidationResult validateCredentialUpdate(Credential original, Credential newCred, CredentialType type) {
        ValidationResultBuilder resultBuilder = new ValidationResultBuilder();
        if (!original.getCloudPlatform().equals(newCred.getCloudPlatform())) {
            resultBuilder.error(String.format("CloudPlatform of the credential cannot be changed! Original: '%s' New: '%s'.",
                    original.getCloudPlatform(), newCred.getCloudPlatform()));
            return resultBuilder.build();
        }
        if (original.getType() != null && !type.equals(original.getType())) {
            resultBuilder.error(String.format("Credential type must be %s type and the current is %s.", type, original.getType()));
            return resultBuilder.build();
        }
        return Optional.ofNullable(providerValidators.get(newCred.getCloudPlatform()))
                .map(validator -> validator.validateUpdate(original, newCred, resultBuilder))
                .orElse(resultBuilder.build());
    }

    public ValidationResult validateAwsCredentialRequest(CredentialRequest credentialRequest) {
        ValidationResultBuilder resultBuilder = new ValidationResultBuilder();
        resultBuilder.ifError(() -> !CloudPlatform.AWS.name().equalsIgnoreCase(credentialRequest.getCloudPlatform()),
                "Credential request is not for AWS.");
        resultBuilder.ifError(() -> StringUtils.isBlank(Optional.ofNullable(credentialRequest.getAws())
                .map(AwsCredentialParameters::getRoleBased)
                .map(RoleBasedParameters::getRoleArn)
                .orElse(null)), "Role ARN is not found in credential request.");
        return resultBuilder.build();
    }
}
