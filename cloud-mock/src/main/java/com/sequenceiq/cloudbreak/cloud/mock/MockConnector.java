package com.sequenceiq.cloudbreak.cloud.mock;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.cloud.Authenticator;
import com.sequenceiq.cloudbreak.cloud.CloudConnector;
import com.sequenceiq.cloudbreak.cloud.CloudConstant;
import com.sequenceiq.cloudbreak.cloud.CredentialConnector;
import com.sequenceiq.cloudbreak.cloud.EncryptionResources;
import com.sequenceiq.cloudbreak.cloud.InstanceConnector;
import com.sequenceiq.cloudbreak.cloud.MetadataCollector;
import com.sequenceiq.cloudbreak.cloud.NetworkConnector;
import com.sequenceiq.cloudbreak.cloud.NoSqlConnector;
import com.sequenceiq.cloudbreak.cloud.ObjectStorageConnector;
import com.sequenceiq.cloudbreak.cloud.PlatformParameters;
import com.sequenceiq.cloudbreak.cloud.PlatformResources;
import com.sequenceiq.cloudbreak.cloud.PublicKeyConnector;
import com.sequenceiq.cloudbreak.cloud.ResourceConnector;
import com.sequenceiq.cloudbreak.cloud.ResourceVolumeConnector;
import com.sequenceiq.cloudbreak.cloud.Setup;
import com.sequenceiq.cloudbreak.cloud.Validator;
import com.sequenceiq.cloudbreak.cloud.ValidatorType;
import com.sequenceiq.cloudbreak.cloud.model.Platform;
import com.sequenceiq.cloudbreak.cloud.model.Variant;

@Service
public class MockConnector implements CloudConnector {

    @Inject
    private MockAuthenticator mockAuthenticator;

    @Inject
    private MockSetup mockSetup;

    @Inject
    private MockCredentialConnector mockCredentialConnector;

    @Inject
    private MockResourceConnector mockResourceConnector;

    @Inject
    private MockPlatformResources mockPlatformResources;

    @Inject
    private MockInstanceConnector mockInstanceConnector;

    @Inject
    private MockMetadataCollector mockMetadataCollector;

    @Inject
    private MockPlatformParameters mockPlatformParameters;

    @Inject
    private MockConstants mockConstants;

    @Inject
    private MockNetworkConnector mockNetworkConnector;

    @Inject
    private MockObjectStorageConnector mockObjectStorageConnector;

    @Inject
    private MockNoSqlConnector mockNoSqlConnector;

    @Inject
    private MockPublicKeyConnector mockPublicKeyConnector;

    @Override
    public Authenticator authentication() {
        return mockAuthenticator;
    }

    @Override
    public Setup setup() {
        return mockSetup;
    }

    @Override
    public List<Validator> validators(ValidatorType validatorType) {
        return Collections.emptyList();
    }

    @Override
    public CredentialConnector credentials() {
        return mockCredentialConnector;
    }

    @Override
    public ResourceConnector resources() {
        return mockResourceConnector;
    }

    @Override
    public InstanceConnector instances() {
        return mockInstanceConnector;
    }

    @Override
    public MetadataCollector metadata() {
        return mockMetadataCollector;
    }

    @Override
    public PlatformParameters parameters() {
        return mockPlatformParameters;
    }

    @Override
    public PlatformResources platformResources() {
        return mockPlatformResources;
    }

    @Override
    public CloudConstant cloudConstant() {
        return mockConstants;
    }

    @Override
    public Platform platform() {
        return MockConstants.MOCK_PLATFORM;
    }

    @Override
    public Variant variant() {
        return MockConstants.MOCK_VARIANT;
    }

    @Override
    public NetworkConnector networkConnector() {
        return mockNetworkConnector;
    }

    @Override
    public ObjectStorageConnector objectStorage() {
        return mockObjectStorageConnector;
    }

    @Override
    public NoSqlConnector noSql() {
        return mockNoSqlConnector;
    }

    @Override
    public PublicKeyConnector publicKey() {
        return mockPublicKeyConnector;
    }

    @Override
    public ResourceVolumeConnector volumeConnector() {
        throw new UnsupportedOperationException("This connector is not implemented for the service!");
    }

    @Override
    public EncryptionResources encryptionResources() {
        return null;
    }
}
