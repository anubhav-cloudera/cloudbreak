package com.sequenceiq.cloudbreak.cm;

import static com.sequenceiq.cloudbreak.cmtemplate.CMRepositoryVersionUtil.CLOUDERAMANAGER_VERSION_7_1_0;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.cloudera.api.swagger.ClouderaManagerResourceApi;
import com.cloudera.api.swagger.RoleConfigGroupsResourceApi;
import com.cloudera.api.swagger.ServicesResourceApi;
import com.cloudera.api.swagger.client.ApiClient;
import com.cloudera.api.swagger.client.ApiException;
import com.cloudera.api.swagger.model.ApiConfig;
import com.cloudera.api.swagger.model.ApiConfigList;
import com.cloudera.api.swagger.model.ApiRoleConfigGroup;
import com.cloudera.api.swagger.model.ApiRoleConfigGroupList;
import com.cloudera.api.swagger.model.ApiService;
import com.cloudera.api.swagger.model.ApiServiceConfig;
import com.cloudera.api.swagger.model.ApiServiceList;
import com.cloudera.api.swagger.model.ApiVersionInfo;
import com.sequenceiq.cloudbreak.cm.client.retry.ClouderaManagerApiFactory;
import com.sequenceiq.cloudbreak.dto.StackDtoDelegate;

@RunWith(MockitoJUnitRunner.class)
public class ClouderaManagerConfigServiceTest {

    private static final String VERSION_7_0_1 = "7.0.1";

    private static final String VERSION_7_1_0 = "7.1.0";

    private static final String TEST_CLUSTER_NAME = "test-cluster-name";

    private static final ApiClient API_CLIENT = new ApiClient();

    private static final String NIFI_SERVICE = "NIFI";

    private static final String HIVE_SERVICE = "HIVE";

    private static final String NIFI_ROLE = "NIFI-ROLE";

    private static final String NIFI_SERVICE_TYPE = "NIFI-SERVICE-TYPE";

    private static final String HIVE_SERVICE_TYPE = "HIVE-SERVICE";

    private static final String NIFI_CONFIG_GROUP = "NIFI-CONFIG-GROUP";

    private static final String CONFIG_NAME = "config-name";

    private static final String DEFAULT_VALUE = "/var/lib";

    private static final String CONFIG_VALUE = "/hadoop/fs1";

    private static final String CONFIG_VIEW = "full";

    @Mock
    private ClouderaManagerApiFactory clouderaManagerApiFactory;

    @InjectMocks
    private ClouderaManagerConfigService underTest;

    @Test
    public void testDisableKnoxAutorestartIfCmVersionAtLeast() throws ApiException {
        setUpCMVersion(VERSION_7_1_0);

        ServicesResourceApi serviceResourceApi = mock(ServicesResourceApi.class);
        when(clouderaManagerApiFactory.getServicesResourceApi(any())).thenReturn(serviceResourceApi);

        String knoxName = "knox-e07";
        ApiServiceList apiServiceList = new ApiServiceList()
                .addItemsItem(new ApiService().name("hbase-a63").type("HBASE"))
                .addItemsItem(new ApiService().name(knoxName).type("KNOX"));
        when(serviceResourceApi.readServices(TEST_CLUSTER_NAME, DataView.SUMMARY.name())).thenReturn(apiServiceList);

        underTest.disableKnoxAutorestartIfCmVersionAtLeast(CLOUDERAMANAGER_VERSION_7_1_0, new ApiClient(), TEST_CLUSTER_NAME);

        ArgumentCaptor<ApiServiceConfig> apiServiceConfigArgumentCaptor = ArgumentCaptor.forClass(ApiServiceConfig.class);
        verify(serviceResourceApi, times(1))
                .updateServiceConfig(eq(TEST_CLUSTER_NAME), eq(knoxName), eq(""), apiServiceConfigArgumentCaptor.capture());

        ApiServiceConfig actualBody = apiServiceConfigArgumentCaptor.getValue();
        assertFalse(actualBody.getItems().isEmpty());
        ApiConfig actualApiConfig = actualBody.getItems().get(0);
        assertEquals(ClouderaManagerConfigService.KNOX_AUTORESTART_ON_STOP, actualApiConfig.getName());
        assertEquals(Boolean.FALSE.toString(), actualApiConfig.getValue());
    }

    private void setUpCMVersion(String version) throws ApiException {
        ClouderaManagerResourceApi clouderaManagerResourceApi = mock(ClouderaManagerResourceApi.class);
        when(clouderaManagerApiFactory.getClouderaManagerResourceApi(any())).thenReturn(clouderaManagerResourceApi);
        ApiVersionInfo version701 = new ApiVersionInfo().version(version);
        when(clouderaManagerResourceApi.getVersion()).thenReturn(version701);
    }

    @Test
    public void testDisableKnoxAutorestartIfCmVersionAtLeastWhenKnoxIsMissing() throws ApiException {
        setUpCMVersion(VERSION_7_1_0);

        ServicesResourceApi serviceResourceApi = mock(ServicesResourceApi.class);
        when(clouderaManagerApiFactory.getServicesResourceApi(any())).thenReturn(serviceResourceApi);

        ApiServiceList apiServiceList = new ApiServiceList().addItemsItem(new ApiService().name("hbase-a63").type("HBASE"));
        when(serviceResourceApi.readServices(TEST_CLUSTER_NAME, DataView.SUMMARY.name())).thenReturn(apiServiceList);

        underTest.disableKnoxAutorestartIfCmVersionAtLeast(CLOUDERAMANAGER_VERSION_7_1_0, new ApiClient(), TEST_CLUSTER_NAME);

        verify(serviceResourceApi, never()).updateServiceConfig(any(), any(), any(), any());
    }

    @Test
    public void testDisableKnoxAutorestartIfCmVersionAtLeastWithLowerVersion() throws ApiException {
        setUpCMVersion(VERSION_7_0_1);

        underTest.disableKnoxAutorestartIfCmVersionAtLeast(CLOUDERAMANAGER_VERSION_7_1_0, new ApiClient(), TEST_CLUSTER_NAME);
        verify(clouderaManagerApiFactory, never()).getServicesResourceApi(any());
    }

    @Test
    public void testModifyServiceConfigValue() throws Exception {
        String hueType = "HUE";
        String hueName = "hue-1";
        String configName = "config_setting";
        String configValue = "new-config-value";
        ServicesResourceApi serviceResourceApi = mock(ServicesResourceApi.class);
        ApiServiceList apiServiceList = new ApiServiceList().addItemsItem(new ApiService().name(hueName).type(hueType));

        when(serviceResourceApi.readServices(TEST_CLUSTER_NAME, DataView.SUMMARY.name())).thenReturn(apiServiceList);
        when(clouderaManagerApiFactory.getServicesResourceApi(any())).thenReturn(serviceResourceApi);

        underTest.modifyServiceConfig(new ApiClient(), TEST_CLUSTER_NAME, hueType, Collections.singletonMap(configName, configValue));

        ArgumentCaptor<ApiServiceConfig> apiServiceConfigArgumentCaptor = ArgumentCaptor.forClass(ApiServiceConfig.class);
        verify(serviceResourceApi, times(1))
                .updateServiceConfig(eq(TEST_CLUSTER_NAME), eq(hueName), eq(""), apiServiceConfigArgumentCaptor.capture());

        ApiServiceConfig actualBody = apiServiceConfigArgumentCaptor.getValue();
        assertFalse(actualBody.getItems().isEmpty());
        ApiConfig actualApiConfig = actualBody.getItems().get(0);
        assertEquals(configName, actualApiConfig.getName());
        assertEquals(configValue, actualApiConfig.getValue());
    }

    @Test
    public void testModifyServiceConfigs() throws Exception {
        String hueType = "HUE";
        String hueName = "hue-1";
        String configName1 = "config_setting1";
        String configValue1 = "new-config-value1";
        String configName2 = "config_setting2";
        String configValue2 = "new-config-value3";
        ServicesResourceApi serviceResourceApi = mock(ServicesResourceApi.class);
        ApiServiceList apiServiceList = new ApiServiceList().addItemsItem(new ApiService().name(hueName).type(hueType));

        when(serviceResourceApi.readServices(TEST_CLUSTER_NAME, DataView.SUMMARY.name())).thenReturn(apiServiceList);
        when(clouderaManagerApiFactory.getServicesResourceApi(any())).thenReturn(serviceResourceApi);
        Map<String, String> configs = new HashMap<>();
        configs.put(configName1, configValue1);
        configs.put(configName2, configValue2);

        underTest.modifyServiceConfig(new ApiClient(), TEST_CLUSTER_NAME, hueType, configs);

        ArgumentCaptor<ApiServiceConfig> apiServiceConfigArgumentCaptor = ArgumentCaptor.forClass(ApiServiceConfig.class);
        verify(serviceResourceApi, times(1))
                .updateServiceConfig(eq(TEST_CLUSTER_NAME), eq(hueName), eq(""), apiServiceConfigArgumentCaptor.capture());

        ApiServiceConfig actualBody = apiServiceConfigArgumentCaptor.getValue();
        assertFalse(actualBody.getItems().isEmpty());
        assertEquals(2, actualBody.getItems().size());
        ApiConfig actualApiConfig = actualBody.getItems().get(0);
        assertEquals(configName1, actualApiConfig.getName());
        assertEquals(configValue1, actualApiConfig.getValue());
        actualApiConfig = actualBody.getItems().get(1);
        assertEquals(configName2, actualApiConfig.getName());
        assertEquals(configValue2, actualApiConfig.getValue());
    }

    @Test
    public void testModifyServiceConfigValueServiceMissing() throws Exception {
        String hueType = "HUE";
        String configName = "config_setting";
        String configValue = "new-config-value";
        ServicesResourceApi serviceResourceApi = mock(ServicesResourceApi.class);
        ApiServiceList apiServiceList = new ApiServiceList().addItemsItem(new ApiService().name("hbase-1").type("HBASE"));

        when(serviceResourceApi.readServices(TEST_CLUSTER_NAME, DataView.SUMMARY.name())).thenReturn(apiServiceList);
        when(clouderaManagerApiFactory.getServicesResourceApi(any())).thenReturn(serviceResourceApi);

        Exception exception = Assertions.assertThrows(
                ClouderaManagerOperationFailedException.class, ()
                        -> underTest.modifyServiceConfig(new ApiClient(), TEST_CLUSTER_NAME, hueType, Collections.singletonMap(configName, configValue)));
        Assert.assertEquals("Service of type: HUE is not found", exception.getMessage());
    }

    @Test
    public void testGetRoleConfigValueByServiceTypeShouldReturnTheConfigValue() throws ApiException {
        RoleConfigGroupsResourceApi roleConfigGroupsResourceApi = Mockito.mock(RoleConfigGroupsResourceApi.class);
        ServicesResourceApi servicesResourceApi = Mockito.mock(ServicesResourceApi.class);
        when(clouderaManagerApiFactory.getRoleConfigGroupsResourceApi(API_CLIENT)).thenReturn(roleConfigGroupsResourceApi);
        when(clouderaManagerApiFactory.getServicesResourceApi(API_CLIENT)).thenReturn(servicesResourceApi);
        List<ApiService> services = List.of(createApiService(NIFI_SERVICE, NIFI_SERVICE_TYPE), createApiService("SPARK", "SPARK-ROLE"));
        when(servicesResourceApi.readServices(eq(TEST_CLUSTER_NAME), any())).thenReturn(createApiServiceList(services));

        ApiRoleConfigGroupList configGroupList = createApiRoleConfigGroups(List.of(createConfigGroup(NIFI_CONFIG_GROUP, NIFI_ROLE),
                createConfigGroup("SPARK-GROUP", "SPARK-ROLE")));
        when(roleConfigGroupsResourceApi.readRoleConfigGroups(TEST_CLUSTER_NAME, NIFI_SERVICE)).thenReturn(configGroupList);

        ApiConfigList roleConfig = createApiConfigList(List.of(createConfig(CONFIG_VALUE, null)));
        when(roleConfigGroupsResourceApi.readConfig(TEST_CLUSTER_NAME, NIFI_CONFIG_GROUP, NIFI_SERVICE, CONFIG_VIEW)).thenReturn(roleConfig);

        Optional<String> actual = underTest.getRoleConfigValueByServiceType(API_CLIENT, TEST_CLUSTER_NAME, NIFI_ROLE, NIFI_SERVICE_TYPE, CONFIG_NAME);

        assertEquals(Optional.of(CONFIG_VALUE), actual);
        verify(clouderaManagerApiFactory).getRoleConfigGroupsResourceApi(API_CLIENT);
        verify(clouderaManagerApiFactory).getServicesResourceApi(API_CLIENT);
        verify(servicesResourceApi).readServices(eq(TEST_CLUSTER_NAME), any());
        verify(roleConfigGroupsResourceApi).readRoleConfigGroups(TEST_CLUSTER_NAME, NIFI_SERVICE);
        verify(roleConfigGroupsResourceApi).readConfig(TEST_CLUSTER_NAME, NIFI_CONFIG_GROUP, NIFI_SERVICE, CONFIG_VIEW);
    }

    @Test
    public void testGetRoleConfigValueByServiceTypeShouldReturnTheConfigValueWhenTheValueIsNullAndDefaultValuePresent() throws ApiException {
        RoleConfigGroupsResourceApi roleConfigGroupsResourceApi = Mockito.mock(RoleConfigGroupsResourceApi.class);
        ServicesResourceApi servicesResourceApi = Mockito.mock(ServicesResourceApi.class);
        when(clouderaManagerApiFactory.getRoleConfigGroupsResourceApi(API_CLIENT)).thenReturn(roleConfigGroupsResourceApi);
        when(clouderaManagerApiFactory.getServicesResourceApi(API_CLIENT)).thenReturn(servicesResourceApi);
        List<ApiService> services = List.of(createApiService(NIFI_SERVICE, NIFI_SERVICE_TYPE), createApiService("SPARK", "SPARK-ROLE"));
        when(servicesResourceApi.readServices(eq(TEST_CLUSTER_NAME), any())).thenReturn(createApiServiceList(services));
        ApiRoleConfigGroupList configGroupList = createApiRoleConfigGroups(List.of(createConfigGroup(NIFI_CONFIG_GROUP, NIFI_ROLE),
                createConfigGroup("SPARK-GROUP", "SPARK-ROLE")));
        when(roleConfigGroupsResourceApi.readRoleConfigGroups(TEST_CLUSTER_NAME, NIFI_SERVICE)).thenReturn(configGroupList);
        ApiConfigList roleConfig = createApiConfigList(List.of(createConfig(null, DEFAULT_VALUE)));
        when(roleConfigGroupsResourceApi.readConfig(TEST_CLUSTER_NAME, NIFI_CONFIG_GROUP, NIFI_SERVICE, CONFIG_VIEW)).thenReturn(roleConfig);

        Optional<String> actual = underTest.getRoleConfigValueByServiceType(API_CLIENT, TEST_CLUSTER_NAME, NIFI_ROLE, NIFI_SERVICE_TYPE, CONFIG_NAME);

        assertEquals(Optional.of(DEFAULT_VALUE), actual);
        verify(clouderaManagerApiFactory).getRoleConfigGroupsResourceApi(API_CLIENT);
        verify(clouderaManagerApiFactory).getServicesResourceApi(API_CLIENT);
        verify(servicesResourceApi).readServices(eq(TEST_CLUSTER_NAME), any());
        verify(roleConfigGroupsResourceApi).readRoleConfigGroups(TEST_CLUSTER_NAME, NIFI_SERVICE);
        verify(roleConfigGroupsResourceApi).readConfig(TEST_CLUSTER_NAME, NIFI_CONFIG_GROUP, NIFI_SERVICE, CONFIG_VIEW);
    }

    @Test
    public void testGetRoleConfigValueByServiceTypeShouldReturnOptionalEmptyWhenServiceTypeNotFound() throws ApiException {
        RoleConfigGroupsResourceApi roleConfigGroupsResourceApi = Mockito.mock(RoleConfigGroupsResourceApi.class);
        ServicesResourceApi servicesResourceApi = Mockito.mock(ServicesResourceApi.class);
        when(clouderaManagerApiFactory.getRoleConfigGroupsResourceApi(API_CLIENT)).thenReturn(roleConfigGroupsResourceApi);
        when(clouderaManagerApiFactory.getServicesResourceApi(API_CLIENT)).thenReturn(servicesResourceApi);
        List<ApiService> services = List.of(createApiService("SPARK", "SPARK-ROLE"));
        when(servicesResourceApi.readServices(eq(TEST_CLUSTER_NAME), any())).thenReturn(createApiServiceList(services));

        Optional<String> actual = underTest.getRoleConfigValueByServiceType(API_CLIENT, TEST_CLUSTER_NAME, NIFI_ROLE, NIFI_SERVICE_TYPE, CONFIG_NAME);

        assertEquals(Optional.empty(), actual);
        verify(clouderaManagerApiFactory).getRoleConfigGroupsResourceApi(API_CLIENT);
        verify(clouderaManagerApiFactory).getServicesResourceApi(API_CLIENT);
        verify(servicesResourceApi).readServices(eq(TEST_CLUSTER_NAME), any());
        verifyNoInteractions(roleConfigGroupsResourceApi);
        verifyNoInteractions(roleConfigGroupsResourceApi);
    }

    @Test
    public void testGetRoleConfigValueByServiceTypeShouldReturnOptionalEmptyWhenRoleTypeNotFound() throws ApiException {
        RoleConfigGroupsResourceApi roleConfigGroupsResourceApi = Mockito.mock(RoleConfigGroupsResourceApi.class);
        ServicesResourceApi servicesResourceApi = Mockito.mock(ServicesResourceApi.class);
        when(clouderaManagerApiFactory.getRoleConfigGroupsResourceApi(API_CLIENT)).thenReturn(roleConfigGroupsResourceApi);
        when(clouderaManagerApiFactory.getServicesResourceApi(API_CLIENT)).thenReturn(servicesResourceApi);
        List<ApiService> services = List.of(createApiService(NIFI_SERVICE, NIFI_SERVICE_TYPE), createApiService("SPARK", "SPARK-ROLE"));
        when(servicesResourceApi.readServices(eq(TEST_CLUSTER_NAME), any())).thenReturn(createApiServiceList(services));
        ApiRoleConfigGroupList configGroupList = createApiRoleConfigGroups(List.of(createConfigGroup("SPARK-GROUP", "SPARK-ROLE")));
        when(roleConfigGroupsResourceApi.readRoleConfigGroups(TEST_CLUSTER_NAME, NIFI_SERVICE)).thenReturn(configGroupList);

        Optional<String> actual = underTest.getRoleConfigValueByServiceType(API_CLIENT, TEST_CLUSTER_NAME, NIFI_ROLE, NIFI_SERVICE_TYPE, CONFIG_NAME);

        assertEquals(Optional.empty(), actual);
        verify(clouderaManagerApiFactory).getRoleConfigGroupsResourceApi(API_CLIENT);
        verify(clouderaManagerApiFactory).getServicesResourceApi(API_CLIENT);
        verify(servicesResourceApi).readServices(eq(TEST_CLUSTER_NAME), any());
        verify(roleConfigGroupsResourceApi, times(1)).readRoleConfigGroups(TEST_CLUSTER_NAME, NIFI_SERVICE);
    }

    @Test
    public void testGetRoleConfigValueByServiceTypeShouldReturnOptionalEmptyWhenTheConfigNotFound() throws ApiException {
        RoleConfigGroupsResourceApi roleConfigGroupsResourceApi = Mockito.mock(RoleConfigGroupsResourceApi.class);
        ServicesResourceApi servicesResourceApi = Mockito.mock(ServicesResourceApi.class);
        when(clouderaManagerApiFactory.getRoleConfigGroupsResourceApi(API_CLIENT)).thenReturn(roleConfigGroupsResourceApi);
        when(clouderaManagerApiFactory.getServicesResourceApi(API_CLIENT)).thenReturn(servicesResourceApi);
        List<ApiService> services = List.of(createApiService(NIFI_SERVICE, NIFI_SERVICE_TYPE), createApiService("SPARK", "SPARK-ROLE"));
        when(servicesResourceApi.readServices(eq(TEST_CLUSTER_NAME), any())).thenReturn(createApiServiceList(services));
        ApiRoleConfigGroupList configGroupList = createApiRoleConfigGroups(List.of(createConfigGroup(NIFI_CONFIG_GROUP, NIFI_ROLE),
                createConfigGroup("SPARK-GROUP", "SPARK-ROLE")));
        when(roleConfigGroupsResourceApi.readRoleConfigGroups(TEST_CLUSTER_NAME, NIFI_SERVICE)).thenReturn(configGroupList);
        ApiConfigList roleConfig = createApiConfigList(Collections.emptyList());
        when(roleConfigGroupsResourceApi.readConfig(TEST_CLUSTER_NAME, NIFI_CONFIG_GROUP, NIFI_SERVICE, CONFIG_VIEW)).thenReturn(roleConfig);

        Optional<String> actual = underTest.getRoleConfigValueByServiceType(API_CLIENT, TEST_CLUSTER_NAME, NIFI_ROLE, NIFI_SERVICE_TYPE, CONFIG_NAME);

        assertEquals(Optional.empty(), actual);
        verify(clouderaManagerApiFactory).getRoleConfigGroupsResourceApi(API_CLIENT);
        verify(clouderaManagerApiFactory).getServicesResourceApi(API_CLIENT);
        verify(servicesResourceApi).readServices(eq(TEST_CLUSTER_NAME), any());
        verify(roleConfigGroupsResourceApi).readRoleConfigGroups(TEST_CLUSTER_NAME, NIFI_SERVICE);
        verify(roleConfigGroupsResourceApi).readConfig(TEST_CLUSTER_NAME, NIFI_CONFIG_GROUP, NIFI_SERVICE, CONFIG_VIEW);
    }

    @Test
    public void testReadServices() throws ApiException {
        ServicesResourceApi servicesApi = mock(ServicesResourceApi.class);
        when(clouderaManagerApiFactory.getServicesResourceApi(any())).thenReturn(servicesApi);
        when(servicesApi.readServices(any(), any())).thenReturn(new ApiServiceList());

        underTest.readServices(new ApiClient(), "cluster");

        verify(servicesApi).readServices(any(), any());
    }

    @Test
    public void testReadServicesFailure() throws ApiException {
        ServicesResourceApi servicesApi = mock(ServicesResourceApi.class);
        when(clouderaManagerApiFactory.getServicesResourceApi(any())).thenReturn(servicesApi);
        when(servicesApi.readServices(any(), any())).thenThrow(new ApiException("something"));

        assertThrows(ClouderaManagerOperationFailedException.class, () -> underTest.readServices(new ApiClient(), "cluster"));

        verify(servicesApi).readServices(any(), any());
    }

    @Test
    public void testReadServiceConfig() throws ApiException {
        ServicesResourceApi servicesApi = mock(ServicesResourceApi.class);
        when(clouderaManagerApiFactory.getServicesResourceApi(any())).thenReturn(servicesApi);
        when(servicesApi.readServiceConfig(any(), any(), any())).thenReturn(new ApiServiceConfig());

        underTest.readServiceConfig(new ApiClient(), "cluster", "service");

        verify(servicesApi).readServiceConfig(any(), any(), any());
    }

    @Test
    public void testReadServiceConfigFailure() throws ApiException {
        ServicesResourceApi servicesApi = mock(ServicesResourceApi.class);
        when(clouderaManagerApiFactory.getServicesResourceApi(any())).thenReturn(servicesApi);
        when(servicesApi.readServiceConfig(any(), any(), any())).thenThrow(new ApiException("something"));

        assertThrows(ClouderaManagerOperationFailedException.class, () ->
                underTest.readServiceConfig(new ApiClient(), "cluster", "service"));

        verify(servicesApi).readServiceConfig(any(), any(), any());
    }

    @Test
    public void testReadRoleConfig() throws ApiException {
        RoleConfigGroupsResourceApi roleConfigGroupsResourceApi = mock(RoleConfigGroupsResourceApi.class);
        when(clouderaManagerApiFactory.getRoleConfigGroupsResourceApi(any())).thenReturn(roleConfigGroupsResourceApi);
        when(roleConfigGroupsResourceApi.readRoleConfigGroups(any(), any())).thenReturn(new ApiRoleConfigGroupList());

        underTest.readRoleConfigGroupConfigs(new ApiClient(), "cluster", "service");

        verify(roleConfigGroupsResourceApi).readRoleConfigGroups(any(), any());
    }

    @Test
    public void testReadRoleConfigFailure() throws ApiException {
        RoleConfigGroupsResourceApi roleConfigGroupsResourceApi = mock(RoleConfigGroupsResourceApi.class);
        when(clouderaManagerApiFactory.getRoleConfigGroupsResourceApi(any())).thenReturn(roleConfigGroupsResourceApi);
        when(roleConfigGroupsResourceApi.readRoleConfigGroups(any(), any())).thenThrow(new ApiException("something"));

        assertThrows(ClouderaManagerOperationFailedException.class, () ->
                underTest.readRoleConfigGroupConfigs(new ApiClient(), "cluster", "service"));

        verify(roleConfigGroupsResourceApi).readRoleConfigGroups(any(), any());
    }

    @Test
    public void testModifyServiceConfig() throws ApiException {
        ServicesResourceApi servicesApi = mock(ServicesResourceApi.class);
        when(clouderaManagerApiFactory.getServicesResourceApi(any())).thenReturn(servicesApi);
        when(servicesApi.updateServiceConfig(any(), any(), any(), any())).thenReturn(new ApiServiceConfig());

        underTest.modifyServiceConfigs(new ApiClient(), "cluster", Map.of("config", "newvalue"), "service");

        ArgumentCaptor<ApiServiceConfig> bodyCaptor = ArgumentCaptor.forClass(ApiServiceConfig.class);
        verify(servicesApi).updateServiceConfig(any(), any(), any(), bodyCaptor.capture());
        assertTrue(bodyCaptor.getValue().getItems().stream().anyMatch(apiConfig -> StringUtils.equals(apiConfig.getName(), "config")));
        assertTrue(bodyCaptor.getValue().getItems().stream().anyMatch(apiConfig -> StringUtils.equals(apiConfig.getValue(), "newvalue")));
    }

    @Test
    public void testModifyServiceConfigFailure() throws ApiException {
        ServicesResourceApi servicesApi = mock(ServicesResourceApi.class);
        when(clouderaManagerApiFactory.getServicesResourceApi(any())).thenReturn(servicesApi);
        when(servicesApi.updateServiceConfig(any(), any(), any(), any())).thenThrow(new ApiException("something"));

        assertThrows(ClouderaManagerOperationFailedException.class, () ->
                underTest.modifyServiceConfigs(new ApiClient(), "cluster", Map.of("config", "newvalue"), "service"));

        verify(servicesApi).updateServiceConfig(any(), any(), any(), any());
    }

    @Test
    public void testModifyRoleConfig() throws ApiException {
        RoleConfigGroupsResourceApi roleConfigGroupsResourceApi = mock(RoleConfigGroupsResourceApi.class);
        when(clouderaManagerApiFactory.getRoleConfigGroupsResourceApi(any())).thenReturn(roleConfigGroupsResourceApi);
        when(roleConfigGroupsResourceApi.updateRoleConfigGroup(any(), any(), any(), any(), any())).thenReturn(new ApiRoleConfigGroup());

        underTest.modifyRoleConfigGroups(new ApiClient(), "cluster", "service", "roleConfigGroup",
                Map.of("config", "newvalue"));

        ArgumentCaptor<ApiRoleConfigGroup> bodyCaptor = ArgumentCaptor.forClass(ApiRoleConfigGroup.class);
        verify(roleConfigGroupsResourceApi).updateRoleConfigGroup(any(), any(), any(), any(), bodyCaptor.capture());
        assertTrue(bodyCaptor.getValue().getConfig().getItems().stream().anyMatch(apiConfig -> StringUtils.equals(apiConfig.getName(), "config")));
        assertTrue(bodyCaptor.getValue().getConfig().getItems().stream().anyMatch(apiConfig -> StringUtils.equals(apiConfig.getValue(), "newvalue")));
    }

    @Test
    public void testModifyRoleConfigFailure() throws ApiException {
        RoleConfigGroupsResourceApi roleConfigGroupsResourceApi = mock(RoleConfigGroupsResourceApi.class);
        when(clouderaManagerApiFactory.getRoleConfigGroupsResourceApi(any())).thenReturn(roleConfigGroupsResourceApi);
        when(roleConfigGroupsResourceApi.updateRoleConfigGroup(any(), any(), any(), any(), any())).thenThrow(new ApiException("something"));

        assertThrows(ClouderaManagerOperationFailedException.class, () -> underTest.modifyRoleConfigGroups(new ApiClient(), "cluster",
                "service", "roleConfigGroup", Map.of("config", "newvalue")));

        verify(roleConfigGroupsResourceApi).updateRoleConfigGroup(any(), any(), any(), any(), any());
    }

    private ApiConfigList createApiConfigList(List<ApiConfig> apiConfigs) {
        ApiConfigList apiConfigList = new ApiConfigList();
        apiConfigList.setItems(apiConfigs);
        return apiConfigList;
    }

    private ApiConfig createConfig(String value, String defaultValue) {
        ApiConfig apiConfig = new ApiConfig();
        apiConfig.setName(CONFIG_NAME);
        apiConfig.setValue(value);
        apiConfig.setDefault(defaultValue);
        return apiConfig;
    }

    private ApiServiceList createApiServiceList(List<ApiService> services) {
        ApiServiceList apiServiceList = new ApiServiceList();
        apiServiceList.items(services);
        return apiServiceList;
    }

    private ApiService createApiService(String serviceName, String serviceType) {
        ApiService apiService = new ApiService();
        apiService.name(serviceName);
        apiService.setType(serviceType);
        return apiService;
    }

    private ApiRoleConfigGroupList createApiRoleConfigGroups(List<ApiRoleConfigGroup> apiRoleConfigGroups) {
        ApiRoleConfigGroupList apiRoleConfigGroupList = new ApiRoleConfigGroupList();
        apiRoleConfigGroupList.items(apiRoleConfigGroups);
        return apiRoleConfigGroupList;
    }

    private ApiRoleConfigGroup createConfigGroup(String configGroupName, String roleType) {
        ApiRoleConfigGroup configGroup = new ApiRoleConfigGroup();
        configGroup.setName(configGroupName);
        configGroup.setRoleType(roleType);
        return configGroup;
    }

    @Test
    public void testStopServiceSuccess() throws Exception {
        String serviceType = "YARN";
        String yarnName = "yarn-1";
        ServicesResourceApi serviceResourceApi = mock(ServicesResourceApi.class);
        ApiServiceList apiServiceList = new ApiServiceList().addItemsItem(new ApiService().name(yarnName).type(serviceType));
        StackDtoDelegate stack = mock(StackDtoDelegate.class);
        doReturn(TEST_CLUSTER_NAME).when(stack).getName();
        when(serviceResourceApi.readServices(TEST_CLUSTER_NAME, DataView.SUMMARY.name())).thenReturn(apiServiceList);
        when(clouderaManagerApiFactory.getServicesResourceApi(any())).thenReturn(serviceResourceApi);

        underTest.stopClouderaManagerService(new ApiClient(), stack, serviceType);

        verify(serviceResourceApi, times(1)).stopCommand(eq(TEST_CLUSTER_NAME), eq(yarnName));
    }

    @Test
    public void testStopServiceNoServiceFound() throws Exception {
        String serviceType = "YARN";
        String yarnName = "yarn-1";
        ServicesResourceApi serviceResourceApi = mock(ServicesResourceApi.class);
        ApiServiceList apiServiceList = new ApiServiceList().addItemsItem(new ApiService().name(yarnName).type("HUE"));
        StackDtoDelegate stack = mock(StackDtoDelegate.class);
        doReturn(TEST_CLUSTER_NAME).when(stack).getName();
        when(serviceResourceApi.readServices(TEST_CLUSTER_NAME, DataView.SUMMARY.name())).thenReturn(apiServiceList);
        when(clouderaManagerApiFactory.getServicesResourceApi(any())).thenReturn(serviceResourceApi);

        ClouderaManagerOperationFailedException exception = assertThrows(ClouderaManagerOperationFailedException.class,
                () -> underTest.stopClouderaManagerService(new ApiClient(), stack, serviceType));

        assertEquals("Service of type: YARN is not found", exception.getMessage());
    }

    @Test
    public void testStartServiceSuccess() throws Exception {
        String serviceType = "YARN";
        String yarnName = "yarn-1";
        ServicesResourceApi serviceResourceApi = mock(ServicesResourceApi.class);
        ApiServiceList apiServiceList = new ApiServiceList().addItemsItem(new ApiService().name(yarnName).type(serviceType));
        StackDtoDelegate stack = mock(StackDtoDelegate.class);
        doReturn(TEST_CLUSTER_NAME).when(stack).getName();
        when(serviceResourceApi.readServices(TEST_CLUSTER_NAME, DataView.SUMMARY.name())).thenReturn(apiServiceList);
        when(clouderaManagerApiFactory.getServicesResourceApi(any())).thenReturn(serviceResourceApi);

        underTest.startClouderaManagerService(new ApiClient(), stack, serviceType);

        verify(serviceResourceApi, times(1)).startCommand(eq(TEST_CLUSTER_NAME), eq(yarnName));
    }

    @Test
    public void testStartServiceNoServiceFound() throws Exception {
        String serviceType = "YARN";
        String yarnName = "yarn-1";
        ServicesResourceApi serviceResourceApi = mock(ServicesResourceApi.class);
        ApiServiceList apiServiceList = new ApiServiceList().addItemsItem(new ApiService().name(yarnName).type("HUE"));
        StackDtoDelegate stack = mock(StackDtoDelegate.class);
        doReturn(TEST_CLUSTER_NAME).when(stack).getName();
        when(serviceResourceApi.readServices(TEST_CLUSTER_NAME, DataView.SUMMARY.name())).thenReturn(apiServiceList);
        when(clouderaManagerApiFactory.getServicesResourceApi(any())).thenReturn(serviceResourceApi);

        ClouderaManagerOperationFailedException exception = assertThrows(ClouderaManagerOperationFailedException.class,
                () -> underTest.startClouderaManagerService(new ApiClient(), stack, serviceType));

        assertEquals("Service of type: YARN is not found", exception.getMessage());
    }

    @Test
    public void tesModifyRoleBasedConfigSuccess() throws Exception {
        String serviceType = "YARN";
        String yarnName = "yarn-1";
        String roleName = "yarn-NODEMANAGER-WORKER";
        ServicesResourceApi serviceResourceApi = mock(ServicesResourceApi.class);
        RoleConfigGroupsResourceApi roleConfigGroupsResourceApi = mock(RoleConfigGroupsResourceApi.class);
        ApiServiceList apiServiceList = new ApiServiceList().addItemsItem(new ApiService().name(yarnName).type(serviceType));
        StackDtoDelegate stack = mock(StackDtoDelegate.class);
        when(serviceResourceApi.readServices(TEST_CLUSTER_NAME, DataView.SUMMARY.name())).thenReturn(apiServiceList);
        when(clouderaManagerApiFactory.getServicesResourceApi(any())).thenReturn(serviceResourceApi);
        when(clouderaManagerApiFactory.getRoleConfigGroupsResourceApi(any())).thenReturn(roleConfigGroupsResourceApi);
        Map<String, String> config = new HashMap<>();
        config.put("test-config", "test-config-property");
        ApiConfigList apiConfigList = new ApiConfigList();
        apiConfigList.addItemsItem(new ApiConfig().name("test-config").value("test-config-property"));

        underTest.modifyRoleBasedConfig(new ApiClient(), TEST_CLUSTER_NAME, serviceType, config, List.of(roleName));

        verify(roleConfigGroupsResourceApi, times(1)).updateConfig(eq(TEST_CLUSTER_NAME), eq(roleName),
                eq(yarnName), eq("Modifying role based config for service yarn-1"), eq(apiConfigList));
    }

    @Test
    public void tesModifyRoleBasedConfigException() throws Exception {
        String serviceType = "YARN";
        String yarnName = "yarn-1";
        String roleName = "yarn-NODEMANAGER-WORKER";
        ServicesResourceApi serviceResourceApi = mock(ServicesResourceApi.class);
        RoleConfigGroupsResourceApi roleConfigGroupsResourceApi = mock(RoleConfigGroupsResourceApi.class);
        ApiServiceList apiServiceList = new ApiServiceList().addItemsItem(new ApiService().name(yarnName).type(serviceType));
        when(serviceResourceApi.readServices(TEST_CLUSTER_NAME, DataView.SUMMARY.name())).thenReturn(apiServiceList);
        when(clouderaManagerApiFactory.getServicesResourceApi(any())).thenReturn(serviceResourceApi);
        when(clouderaManagerApiFactory.getRoleConfigGroupsResourceApi(any())).thenReturn(roleConfigGroupsResourceApi);
        Map<String, String> config = new HashMap<>();
        config.put("test-config", "test-config-property");
        ApiConfigList apiConfigList = new ApiConfigList();
        apiConfigList.addItemsItem(new ApiConfig().name("test-config").value("test-config-property"));
        doThrow(new ApiException("Test")).when(roleConfigGroupsResourceApi).updateConfig(eq(TEST_CLUSTER_NAME), eq(roleName),
                eq(yarnName), eq("Modifying role based config for service yarn-1"), eq(apiConfigList));


        ClouderaManagerOperationFailedException exception = assertThrows(ClouderaManagerOperationFailedException.class,
                () -> underTest.modifyRoleBasedConfig(new ApiClient(), TEST_CLUSTER_NAME, serviceType, config, List.of(roleName)));

        verify(roleConfigGroupsResourceApi, times(1)).updateConfig(eq(TEST_CLUSTER_NAME), eq(roleName),
                eq(yarnName), eq("Modifying role based config for service yarn-1"), eq(apiConfigList));
    }
}
