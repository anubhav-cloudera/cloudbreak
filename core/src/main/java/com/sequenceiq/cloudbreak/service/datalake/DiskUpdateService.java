package com.sequenceiq.cloudbreak.service.datalake;

import static com.sequenceiq.cloudbreak.cloud.model.AvailabilityZone.availabilityZone;
import static com.sequenceiq.cloudbreak.cloud.model.Location.location;
import static com.sequenceiq.cloudbreak.cloud.model.Region.region;
import static com.sequenceiq.cloudbreak.core.flow2.cluster.disk.resize.DiskResizeEvent.DISK_RESIZE_TRIGGER_EVENT;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.DiskUpdateRequest;
import com.sequenceiq.cloudbreak.auth.crn.Crn;
import com.sequenceiq.cloudbreak.cloud.CloudConnector;
import com.sequenceiq.cloudbreak.cloud.context.AuthenticatedContext;
import com.sequenceiq.cloudbreak.cloud.context.CloudContext;
import com.sequenceiq.cloudbreak.cloud.init.CloudPlatformConnectors;
import com.sequenceiq.cloudbreak.cloud.model.CloudCredential;
import com.sequenceiq.cloudbreak.cloud.model.CloudPlatformVariant;
import com.sequenceiq.cloudbreak.cloud.model.Location;
import com.sequenceiq.cloudbreak.cloud.model.Platform;
import com.sequenceiq.cloudbreak.cloud.model.Variant;
import com.sequenceiq.cloudbreak.cloud.model.Volume;
import com.sequenceiq.cloudbreak.cloud.model.VolumeSetAttributes;
import com.sequenceiq.cloudbreak.cloud.service.CloudParameterCache;
import com.sequenceiq.cloudbreak.cluster.api.ClusterApi;
import com.sequenceiq.cloudbreak.cluster.util.ResourceAttributeUtil;
import com.sequenceiq.cloudbreak.core.flow2.cluster.disk.resize.request.DiskResizeRequest;
import com.sequenceiq.cloudbreak.core.flow2.service.ReactorNotifier;
import com.sequenceiq.cloudbreak.domain.Template;
import com.sequenceiq.cloudbreak.domain.VolumeTemplate;
import com.sequenceiq.cloudbreak.domain.stack.Stack;
import com.sequenceiq.cloudbreak.dto.StackDto;
import com.sequenceiq.cloudbreak.logger.MDCBuilder;
import com.sequenceiq.cloudbreak.service.cluster.ClusterApiConnectors;
import com.sequenceiq.cloudbreak.service.resource.ResourceService;
import com.sequenceiq.cloudbreak.service.stack.InstanceGroupService;
import com.sequenceiq.cloudbreak.service.stack.StackDtoService;
import com.sequenceiq.cloudbreak.service.stack.StackService;
import com.sequenceiq.cloudbreak.service.template.TemplateService;
import com.sequenceiq.cloudbreak.util.StackUtil;
import com.sequenceiq.cloudbreak.view.InstanceGroupView;
import com.sequenceiq.flow.api.model.FlowIdentifier;

@Service
public class DiskUpdateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiskUpdateService.class);

    @Inject
    private CloudPlatformConnectors cloudPlatformConnectors;

    @Inject
    private ClusterApiConnectors clusterApiConnectors;

    @Inject
    private StackUtil stackUtil;

    @Inject
    private StackDtoService stackDtoService;

    @Inject
    private CloudParameterCache cloudParameterCache;

    @Inject
    private StackService stackService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private ResourceAttributeUtil resourceAttributeUtil;

    @Inject
    private ReactorNotifier reactorNotifier;

    @Inject
    private TemplateService templateService;

    @Inject
    private InstanceGroupService instanceGroupService;

    public boolean isDiskTypeChangeSupported(String platform) {
        return cloudParameterCache.isDiskTypeChangeSupported(platform);
    }

    public void updateDiskTypeAndSize(DiskUpdateRequest diskUpdateRequest, List<Volume> volumesToUpdate, Long stackId) throws Exception {
        StackDto stackDto = stackDtoService.getById(stackId);
        CloudPlatformVariant cloudPlatformVariant = new CloudPlatformVariant(Platform.platform(stackDto.getCloudPlatform()),
                Variant.variant(stackDto.getPlatformVariant()));
        CloudConnector cloudConnector = cloudPlatformConnectors.get(cloudPlatformVariant);
        AuthenticatedContext ac = getAuthenticatedContext(cloudConnector, stackDto);
        List<String> volumeIds = volumesToUpdate.stream().map(Volume::getId).toList();
        cloudConnector.volumeConnector().updateDiskVolumes(ac, volumeIds, diskUpdateRequest.getVolumeType(), diskUpdateRequest.getSize());
        resourceService.saveAll(stackDto.getDiskResources().stream()
            .filter(volumeSet -> diskUpdateRequest.getGroup().equals(volumeSet.getInstanceGroup()))
            .peek(volumeSet -> resourceAttributeUtil.getTypedAttributes(volumeSet, VolumeSetAttributes.class).ifPresent(volumeSetAttributes -> {
                volumeSetAttributes.setVolumes(volumeSetAttributes.getVolumes().stream()
                        .peek(attr -> {
                            attr.setSize(diskUpdateRequest.getSize());
                            attr.setType(diskUpdateRequest.getVolumeType());
                        }).toList());
                resourceAttributeUtil.setTypedAttributes(volumeSet, volumeSetAttributes);
            }))
            .collect(Collectors.toList()));
        updateTemplate(stackId, diskUpdateRequest);
    }

    public void stopCMServices(long stackId) throws Exception {
        StackDto stackDto = stackDtoService.getById(stackId);
        ClusterApi clusterApi = clusterApiConnectors.getConnector(stackDto);
        clusterApi.clusterModificationService().stopCluster(true);
    }

    public void startCMServices(long stackId) throws Exception {
        StackDto stackDto = stackDtoService.getById(stackId);
        ClusterApi clusterApi = clusterApiConnectors.getConnector(stackDto);
        clusterApi.clusterModificationService().startCluster();
    }

    public FlowIdentifier resizeDisks(long stackId, String instanceGroup) {
        Stack stack = stackService.getByIdWithListsInTransaction(stackId);
        MDCBuilder.buildMdcContext(stack);
        LOGGER.info("Stack Resize Flow triggered for stack {}", stack.getName());
        DiskResizeRequest diskResizeRequest = DiskResizeRequest.Builder.builder()
                .withSelector(DISK_RESIZE_TRIGGER_EVENT.selector())
                .withStackId(stackId)
                .withInstanceGroup(instanceGroup)
                .build();
        FlowIdentifier flowIdentifier = reactorNotifier.notify(stackId, diskResizeRequest.selector(), diskResizeRequest);
        LOGGER.info("DiskResizeRequest event is triggered for stack {}", stack.getName());
        return flowIdentifier;
    }

    private AuthenticatedContext getAuthenticatedContext(CloudConnector cloudConnector, StackDto stack) {
        CloudCredential cloudCredential = stackUtil.getCloudCredential(stack.getEnvironmentCrn());
        Location location = location(region(stack.getRegion()), availabilityZone(stack.getAvailabilityZone()));
        CloudContext cloudContext = CloudContext.Builder.builder()
                .withId(stack.getId())
                .withName(stack.getName())
                .withCrn(stack.getResourceCrn())
                .withPlatform(stack.getCloudPlatform())
                .withVariant(stack.getPlatformVariant())
                .withLocation(location)
                .withWorkspaceId(stack.getWorkspace().getId())
                .withAccountId(Crn.safeFromString(stack.getResourceCrn()).getAccountId())
                .build();
        return cloudConnector.authentication().authenticate(cloudContext, cloudCredential);
    }

    private void updateTemplate(Long stackId, DiskUpdateRequest diskUpdateRequest) {
        LOGGER.debug("Updating stack template and saving it to CBDB.");
        Optional<InstanceGroupView> optionalGroup = instanceGroupService
                .findInstanceGroupViewByStackIdAndGroupName(stackId, diskUpdateRequest.getGroup());
        if (optionalGroup.isPresent()) {
            InstanceGroupView instanceGroup = optionalGroup.get();
            Template template = instanceGroup.getTemplate();
            for (VolumeTemplate volumeTemplateInTheDatabase : template.getVolumeTemplates()) {
                volumeTemplateInTheDatabase.setVolumeType(diskUpdateRequest.getVolumeType());
                volumeTemplateInTheDatabase.setVolumeSize(diskUpdateRequest.getSize());
            }
            templateService.savePure(template);
        }
    }
}
