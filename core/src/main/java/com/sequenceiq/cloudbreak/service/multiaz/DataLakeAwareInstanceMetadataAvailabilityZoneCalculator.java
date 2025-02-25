package com.sequenceiq.cloudbreak.service.multiaz;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.cmtemplate.utils.BlueprintUtils;
import com.sequenceiq.cloudbreak.domain.stack.Stack;
import com.sequenceiq.cloudbreak.domain.stack.instance.InstanceGroup;
import com.sequenceiq.cloudbreak.domain.stack.instance.InstanceMetaData;
import com.sequenceiq.common.api.type.InstanceGroupName;

@Service
public class DataLakeAwareInstanceMetadataAvailabilityZoneCalculator extends InstanceMetadataAvailabilityZoneCalculator {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataLakeAwareInstanceMetadataAvailabilityZoneCalculator.class);

    @Inject
    private BlueprintUtils blueprintUtils;

    @Override
    public void populate(Long stackId) {
        Stack stack = getStackService().getByIdWithLists(stackId);
        if (populateSupportedOnStack(stack)) {
            if (blueprintUtils.isEnterpriseDatalake(stack)) {
                populateForEnterpriseDataLake(stack);
            } else {
                LOGGER.info("Populate availability zones with round-robin fashion for every instance group.");
                populate(stack);
            }
        } else {
            LOGGER.debug("Stack is NOT multi-AZ enabled or AvailabilityZone connector doesn't exist for platform , no need to populate zones for instances");
        }
    }

    private void populateForEnterpriseDataLake(Stack stack) {
        LOGGER.info("Populating availability zones for Enterprise Data Lake");
        Set<InstanceMetaData> updatedInstancesMetaData = new HashSet<>();
        for (InstanceGroup instanceGroup : stack.getInstanceGroups()) {
            if (notGatewayAndNotAuxiliaryGroup(instanceGroup)) {
                updatedInstancesMetaData.addAll(populateAvailabilityZonesOnGroup(instanceGroup));
            }
        }
        Optional<InstanceGroup> gatewayInstanceGroup = getInstanceGroupByInstanceGroupName(stack, InstanceGroupName.GATEWAY);
        Optional<InstanceGroup> auxiliaryInstanceGroup = getInstanceGroupByInstanceGroupName(stack, InstanceGroupName.AUXILIARY);
        if (gatewayInstanceGroup.isPresent() || auxiliaryInstanceGroup.isPresent()) {
            Set<InstanceMetaData> mergedInstanceMetaData = new HashSet<>();
            gatewayInstanceGroup.ifPresent(ig -> mergedInstanceMetaData.addAll(ig.getNotTerminatedInstanceMetaDataSet()));
            auxiliaryInstanceGroup.ifPresent(ig -> mergedInstanceMetaData.addAll(ig.getNotTerminatedInstanceMetaDataSet()));
            LOGGER.info("Auxiliary/Gateway instance group's meta data: {}", mergedInstanceMetaData);
            Set<String> availabilityZones = gatewayInstanceGroup.or(() -> auxiliaryInstanceGroup).get().getAvailabilityZones();
            updatedInstancesMetaData.addAll(populateAvailabilityZoneOfInstances(availabilityZones, mergedInstanceMetaData, "Gateway/Auxiliary"));
            updateInstancesMetaData(updatedInstancesMetaData);
        } else {
            LOGGER.info("{} and {} instance groups are not present, nothing to do", InstanceGroupName.GATEWAY.getName(),
                    InstanceGroupName.AUXILIARY.getName());
        }
    }

    private static boolean notGatewayAndNotAuxiliaryGroup(InstanceGroup instanceGroup) {
        String instanceGroupName = instanceGroup.getGroupName();
        return !InstanceGroupName.AUXILIARY.getName().equals(instanceGroupName)
                && !InstanceGroupName.GATEWAY.getName().equals(instanceGroupName);
    }

    private Optional<InstanceGroup> getInstanceGroupByInstanceGroupName(Stack stack, InstanceGroupName groupName) {
        return stack.getInstanceGroups()
                .stream()
                .filter(instanceGroup -> instanceGroup.getGroupName().equals(groupName.getName()))
                .findFirst();
    }
}
