package com.sequenceiq.cloudbreak.reactor.handler.cluster.upgrade.rds;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sequenceiq.cloudbreak.cluster.api.ClusterApi;
import com.sequenceiq.cloudbreak.common.database.TargetMajorVersion;
import com.sequenceiq.cloudbreak.common.event.Selectable;
import com.sequenceiq.cloudbreak.common.orchestration.Node;
import com.sequenceiq.cloudbreak.dto.StackDto;
import com.sequenceiq.cloudbreak.orchestrator.exception.CloudbreakOrchestratorFailedException;
import com.sequenceiq.cloudbreak.orchestrator.host.HostOrchestrator;
import com.sequenceiq.cloudbreak.orchestrator.model.GatewayConfig;
import com.sequenceiq.cloudbreak.orchestrator.state.ExitCriteriaModel;
import com.sequenceiq.cloudbreak.reactor.api.event.cluster.upgrade.rds.UpgradeRdsStopServicesRequest;
import com.sequenceiq.cloudbreak.service.CloudbreakException;
import com.sequenceiq.cloudbreak.service.GatewayConfigService;
import com.sequenceiq.cloudbreak.service.cluster.ClusterApiConnectors;
import com.sequenceiq.cloudbreak.service.stack.StackDtoService;
import com.sequenceiq.cloudbreak.util.StackUtil;
import com.sequenceiq.cloudbreak.view.ClusterView;
import com.sequenceiq.cloudbreak.view.InstanceMetadataView;
import com.sequenceiq.cloudbreak.view.StackView;
import com.sequenceiq.flow.reactor.api.handler.HandlerEvent;

@ExtendWith(MockitoExtension.class)
class StopServicesHandlerTest {

    private static final Long STACK_ID = 12L;

    private static final TargetMajorVersion TARGET_MAJOR_VERSION = TargetMajorVersion.VERSION_11;

    @Mock
    private StackDtoService stackDtoService;

    @Mock
    private GatewayConfigService gatewayConfigService;

    @Mock
    private ClusterApiConnectors clusterApiConnectors;

    @Mock
    private StackUtil stackUtil;

    @Mock
    private HostOrchestrator hostOrchestrator;

    @Mock
    private HandlerEvent<UpgradeRdsStopServicesRequest> event;

    @InjectMocks
    private StopServicesHandler underTest;

    @Test
    void selector() {
        assertThat(underTest.selector()).isEqualTo("UPGRADERDSSTOPSERVICESREQUEST");
    }

    @Test
    void testDoAccept() throws CloudbreakException {
        // GIVEN
        UpgradeRdsStopServicesRequest request = new UpgradeRdsStopServicesRequest(STACK_ID, TARGET_MAJOR_VERSION);
        when(event.getData()).thenReturn(request);
        StackDto stackDto = mock(StackDto.class);
        StackView stackView = mock(StackView.class);
        ClusterView clusterView = mock(ClusterView.class);
        when(stackDto.getStack()).thenReturn(stackView);
        when(stackDto.getCluster()).thenReturn(clusterView);
        when(stackDto.getPrimaryGatewayInstance()).thenReturn(mock(InstanceMetadataView.class));
        GatewayConfig gatewayConfig = mock(GatewayConfig.class);
        when(stackDtoService.getById(STACK_ID)).thenReturn(stackDto);
        when(gatewayConfigService.getGatewayConfig(stackView, stackDto.getSecurityConfig(), stackDto.getPrimaryGatewayInstance(), stackDto.hasGateway()))
                .thenReturn(gatewayConfig);
        when(clusterApiConnectors.getConnector(stackDto)).thenReturn(mock(ClusterApi.class));
        when(stackUtil.collectReachableNodes(stackDto)).thenReturn(Set.of(mock(Node.class)));
        // WHEN
        Selectable actualSelectable = underTest.doAccept(event);
        // THEN
        verify(clusterApiConnectors.getConnector(stackDto), times(1)).stopCluster(anyBoolean());
        assertThat(actualSelectable.selector()).isEqualTo("UPGRADERDSSTOPSERVICESRESULT");
    }

    @Test
    void testDoAcceptThrowsException() throws Exception {
        // GIVEN
        UpgradeRdsStopServicesRequest request = new UpgradeRdsStopServicesRequest(STACK_ID, TARGET_MAJOR_VERSION);
        when(event.getData()).thenReturn(request);
        StackDto stackDto = mock(StackDto.class);
        StackView stackView = mock(StackView.class);
        ClusterView clusterView = mock(ClusterView.class);
        when(stackDto.getStack()).thenReturn(stackView);
        when(stackDto.getCluster()).thenReturn(clusterView);
        when(stackDto.getPrimaryGatewayInstance()).thenReturn(mock(InstanceMetadataView.class));
        GatewayConfig gatewayConfig = mock(GatewayConfig.class);
        when(stackDtoService.getById(STACK_ID)).thenReturn(stackDto);
        when(gatewayConfigService.getGatewayConfig(stackView, stackDto.getSecurityConfig(), stackDto.getPrimaryGatewayInstance(), stackDto.hasGateway()))
                .thenReturn(gatewayConfig);
        when(clusterApiConnectors.getConnector(stackDto)).thenReturn(mock(ClusterApi.class));
        when(stackUtil.collectReachableNodes(stackDto)).thenReturn(Set.of(mock(Node.class)));
        doThrow(new CloudbreakOrchestratorFailedException("exception")).when(hostOrchestrator).stopClusterManagerWithItsAgents(
                any(GatewayConfig.class), anySet(), any(ExitCriteriaModel.class));
        // WHEN
        Selectable actualSelectable = underTest.doAccept(event);
        // THEN
        assertThat(actualSelectable.selector()).isEqualTo("UPGRADERDSFAILEDEVENT");
    }
}