package com.sequenceiq.datalake.service.sdx.status;

import javax.inject.Inject;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.rotation.SecretType;
import com.sequenceiq.cloudbreak.rotation.service.status.SecretRotationStatusService;
import com.sequenceiq.datalake.entity.DatalakeStatusEnum;
import com.sequenceiq.datalake.entity.SdxCluster;
import com.sequenceiq.datalake.entity.SdxStatusEntity;
import com.sequenceiq.datalake.service.sdx.SdxService;

@Primary
@Component
public class SdxSecretRotationStatusService implements SecretRotationStatusService {

    @Inject
    private SdxStatusService sdxStatusService;

    @Inject
    private SdxService sdxService;

    @Override
    public void rotationStarted(String resourceCrn, SecretType secretType) {
        sdxStatusService.setStatusForDatalakeAndNotify(DatalakeStatusEnum.DATALAKE_SECRET_ROTATION_IN_PROGRESS,
                String.format("Rotation started, secret type: %s", secretType.value()), resourceCrn);
    }

    @Override
    public void rotationFinished(String resourceCrn, SecretType secretType) {
        sdxStatusService.setStatusForDatalakeAndNotify(DatalakeStatusEnum.DATALAKE_SECRET_ROTATION_FINISHED,
                String.format("Rotation finished, waiting for finalize, secret type: %s", secretType.value()), resourceCrn);
    }

    @Override
    public void rotationFailed(String resourceCrn, SecretType secretType, String reason) {
        SdxCluster sdxCluster = sdxService.getByCrn(resourceCrn);
        SdxStatusEntity actualStatusForSdx = sdxStatusService.getActualStatusForSdx(sdxCluster);
        if (actualStatusForSdx.getStatus() != DatalakeStatusEnum.DATALAKE_SECRET_ROTATION_ROLLBACK_FAILED
                && actualStatusForSdx.getStatus() != DatalakeStatusEnum.DATALAKE_SECRET_ROTATION_FINALIZE_FAILED) {
            sdxStatusService.setStatusForDatalakeAndNotify(DatalakeStatusEnum.DATALAKE_SECRET_ROTATION_FAILED,
                    String.format("Rotation failed, secret type: %s, reason: %s", secretType.value(), reason), resourceCrn);
        }
    }

    @Override
    public void rollbackStarted(String resourceCrn, SecretType secretType) {
        sdxStatusService.setStatusForDatalakeAndNotify(DatalakeStatusEnum.DATALAKE_SECRET_ROTATION_ROLLBACK_IN_PROGRESS,
                String.format("Rotation rollback started, secret type: %s", secretType.value()), resourceCrn);
    }

    @Override
    public void rollbackFinished(String resourceCrn, SecretType secretType) {
        sdxStatusService.setStatusForDatalakeAndNotify(DatalakeStatusEnum.DATALAKE_SECRET_ROTATION_ROLLBACK_FINISHED,
                String.format("Rotation rollback finished, secret type: %s", secretType.value()), resourceCrn);
    }

    @Override
    public void rollbackFailed(String resourceCrn, SecretType secretType, String reason) {
        sdxStatusService.setStatusForDatalakeAndNotify(DatalakeStatusEnum.DATALAKE_SECRET_ROTATION_ROLLBACK_FAILED,
                String.format("Rotation rollback failed, secret type: %s, reason: %s", secretType.value(), reason), resourceCrn);
    }

    @Override
    public void finalizeStarted(String resourceCrn, SecretType secretType) {
        sdxStatusService.setStatusForDatalakeAndNotify(DatalakeStatusEnum.DATALAKE_SECRET_ROTATION_FINALIZE_IN_PROGRESS,
                String.format("Rotation finalize started, secret type: %s", secretType.value()), resourceCrn);
    }

    @Override
    public void finalizeFinished(String resourceCrn, SecretType secretType) {
        sdxStatusService.setStatusForDatalakeAndNotify(DatalakeStatusEnum.RUNNING,
                String.format("Rotation finished, secret type: %s", secretType.value()), resourceCrn);
    }

    @Override
    public void finalizeFailed(String resourceCrn, SecretType secretType, String reason) {
        sdxStatusService.setStatusForDatalakeAndNotify(DatalakeStatusEnum.DATALAKE_SECRET_ROTATION_FINALIZE_FAILED,
                String.format("Rotation finalize failed, secret type: %s, reason: %s", secretType.value(), reason), resourceCrn);
    }
}
