package com.sequenceiq.cloudbreak.rotation.secret.vault;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sequenceiq.cloudbreak.rotation.RotationMetadataTestUtil;
import com.sequenceiq.cloudbreak.rotation.common.SecretRotationException;
import com.sequenceiq.cloudbreak.rotation.service.notification.SecretRotationNotificationService;
import com.sequenceiq.cloudbreak.rotation.service.progress.SecretRotationStepProgressService;
import com.sequenceiq.cloudbreak.service.secret.domain.RotationSecret;
import com.sequenceiq.cloudbreak.service.secret.service.SecretService;

@ExtendWith(MockitoExtension.class)
public class VaultRotationExecutorTest {

    @Mock
    private SecretService secretService;

    @Mock
    private SecretRotationStepProgressService secretRotationProgressService;

    @Mock
    private SecretRotationNotificationService secretRotationNotificationService;

    @InjectMocks
    private VaultRotationExecutor underTest;

    @BeforeEach
    public void mockProgressService() {
        lenient().when(secretRotationProgressService.latestStep(any(), any())).thenReturn(Optional.empty());
    }

    @Test
    public void testPreValidation() {
        when(secretService.isSecret(any())).thenReturn(Boolean.TRUE);

        VaultRotationContext rotationContext = VaultRotationContext.builder()
                .withVaultPathSecretMap(Map.of("secretPath", "secret"))
                .build();
        underTest.executePreValidation(rotationContext, null);

        verify(secretService).isSecret(any());
    }

    @Test
    public void testPreValidationIfSecretInvalid() {
        when(secretService.isSecret(any())).thenReturn(Boolean.FALSE);

        VaultRotationContext rotationContext = VaultRotationContext.builder()
                .withVaultPathSecretMap(Map.of("secretPath", "secret"))
                .build();
        assertThrows(SecretRotationException.class, () -> underTest.executePreValidation(rotationContext, null));

        verify(secretService).isSecret(any());
    }

    @Test
    public void testPostValidation() {
        when(secretService.getRotation(anyString())).thenReturn(new RotationSecret("new", "old"));

        VaultRotationContext rotationContext = VaultRotationContext.builder()
                .withVaultPathSecretMap(Map.of("secretPath", "secret"))
                .build();
        underTest.executePostValidation(rotationContext);

        verify(secretService).getRotation(any());
    }

    @Test
    public void testPostValidationIfRotationCorrupted() {
        when(secretService.getRotation(anyString())).thenReturn(new RotationSecret("new", null));

        VaultRotationContext rotationContext = VaultRotationContext.builder()
                .withVaultPathSecretMap(Map.of("secretPath", "secret"))
                .build();
        assertThrows(SecretRotationException.class, () -> underTest.executePostValidation(rotationContext));

        verify(secretService).getRotation(any());
    }

    @Test
    public void testVaultRotation() throws Exception {
        when(secretService.putRotation(anyString(), anyString())).thenReturn("anything");
        when(secretService.getRotation(anyString())).thenReturn(new RotationSecret("new", null));
        VaultRotationContext rotationContext = VaultRotationContext.builder()
                .withVaultPathSecretMap(Map.of("secretPath", "secret"))
                .build();
        underTest.executeRotate(rotationContext, RotationMetadataTestUtil.metadataForRotation("resource", null));

        verify(secretService, times(1)).putRotation(eq("secretPath"), eq("secret"));
    }

    @Test
    public void testVaultRotationFinalization() throws Exception {
        when(secretService.update(anyString(), anyString())).thenReturn("anything");
        when(secretService.getRotation(anyString())).thenReturn(new RotationSecret("new", "old"));
        VaultRotationContext rotationContext = VaultRotationContext.builder()
                .withVaultPathSecretMap(Map.of("secretPath", "secret"))
                .build();
        underTest.executeFinalize(rotationContext, RotationMetadataTestUtil.metadataForFinalize("resource", null));

        verify(secretService, times(1)).update(eq("secretPath"), eq("new"));
    }

    @Test
    public void testVaultRotationRollback() throws Exception {
        when(secretService.update(anyString(), anyString())).thenReturn("anything");
        when(secretService.getRotation(anyString())).thenReturn(new RotationSecret("new", "old"));
        VaultRotationContext rotationContext = VaultRotationContext.builder()
                .withVaultPathSecretMap(Map.of("secretPath", "secret"))
                .build();
        underTest.executeRollback(rotationContext, RotationMetadataTestUtil.metadataForRollback("resource", null));

        verify(secretService, times(1)).update(eq("secretPath"), eq("old"));
    }

    @Test
    public void testVaultRotationFailure() throws Exception {
        when(secretService.putRotation(anyString(), anyString())).thenThrow(new Exception("anything"));
        when(secretService.getRotation(anyString())).thenReturn(new RotationSecret("new", null));
        VaultRotationContext rotationContext = VaultRotationContext.builder()
                .withVaultPathSecretMap(Map.of("secretPath", "secret"))
                .build();
        assertThrows(SecretRotationException.class, () -> underTest.executeRotate(rotationContext,
                RotationMetadataTestUtil.metadataForRotation("resource", null)));
    }
}
