package com.sequenceiq.freeipa.rotation;

import static com.sequenceiq.cloudbreak.rotation.CommonSecretRotationStep.CUSTOM_JOB;
import static com.sequenceiq.cloudbreak.rotation.CommonSecretRotationStep.SALTBOOT_CONFIG;
import static com.sequenceiq.cloudbreak.rotation.CommonSecretRotationStep.USER_DATA;
import static com.sequenceiq.cloudbreak.rotation.CommonSecretRotationStep.VAULT;
import static com.sequenceiq.cloudbreak.rotation.MultiSecretType.FREEIPA_CA_CERT;
import static com.sequenceiq.freeipa.rotation.FreeIpaSecretRotationStep.CCMV2_JUMPGATE;
import static com.sequenceiq.freeipa.rotation.FreeIpaSecretRotationStep.FREEIPA_ADMIN_USER_PASSWORD;
import static com.sequenceiq.freeipa.rotation.FreeIpaSecretRotationStep.FREEIPA_DIRECTORY_MANAGER_PASSWORD;
import static com.sequenceiq.freeipa.rotation.FreeIpaSecretRotationStep.LAUNCH_TEMPLATE;
import static com.sequenceiq.freeipa.rotation.FreeIpaSecretRotationStep.SALT_PILLAR_UPDATE;
import static com.sequenceiq.freeipa.rotation.FreeIpaSecretRotationStep.SALT_STATE_APPLY;

import java.util.List;
import java.util.Optional;

import com.sequenceiq.cloudbreak.rotation.MultiSecretType;
import com.sequenceiq.cloudbreak.rotation.SecretRotationStep;
import com.sequenceiq.cloudbreak.rotation.SecretType;

public enum FreeIpaSecretType implements SecretType {
    FREEIPA_ADMIN_PASSWORD(List.of(VAULT, FREEIPA_ADMIN_USER_PASSWORD, FREEIPA_DIRECTORY_MANAGER_PASSWORD, SALT_PILLAR_UPDATE)),
    FREEIPA_SALT_BOOT_SECRETS(List.of(VAULT, CUSTOM_JOB, SALTBOOT_CONFIG, USER_DATA, LAUNCH_TEMPLATE)),
    CCMV2_JUMPGATE_AGENT_ACCESS_KEY(List.of(CCMV2_JUMPGATE, LAUNCH_TEMPLATE, SALT_PILLAR_UPDATE, SALT_STATE_APPLY)),
    FREEIPA_CA_CERT_RENEWAL(List.of(CUSTOM_JOB), FREEIPA_CA_CERT);

    private final List<SecretRotationStep> steps;

    private final Optional<MultiSecretType> multiSecretType;

    FreeIpaSecretType(List<SecretRotationStep> steps) {
        this.steps = steps;
        this.multiSecretType = Optional.empty();
    }

    FreeIpaSecretType(List<SecretRotationStep> steps, MultiSecretType multiSecretType) {
        this.steps = steps;
        this.multiSecretType = Optional.ofNullable(multiSecretType);
    }

    @Override
    public List<SecretRotationStep> getSteps() {
        return steps;
    }

    @Override
    public Optional<MultiSecretType> getMultiSecretType() {
        return multiSecretType;
    }

    @Override
    public Class<? extends Enum<?>> getClazz() {
        return FreeIpaSecretType.class;
    }

    @Override
    public String value() {
        return name();
    }
}
