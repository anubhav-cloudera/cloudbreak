package com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.instancegroup.network.azure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sequenceiq.cloudbreak.common.mappable.CloudPlatform;
import com.sequenceiq.cloudbreak.common.mappable.MappableBase;
import com.sequenceiq.cloudbreak.common.network.NetworkConstants;
import com.sequenceiq.common.model.JsonEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class InstanceGroupAzureNetworkV4Parameters extends MappableBase implements JsonEntity {

    @ApiModelProperty
    private List<String> subnetIds = new ArrayList<>();

    @ApiModelProperty
    private Set<String> availabilityZones = new HashSet<>();

    public List<String> getSubnetIds() {
        return subnetIds;
    }

    public void setSubnetIds(List<String> subnetIds) {
        this.subnetIds = subnetIds;
    }

    public Set<String> getAvailabilityZones() {
        return availabilityZones;
    }

    public void setAvailabilityZones(Set<String> availabilityZones) {
        this.availabilityZones = availabilityZones;
    }

    @Override
    public Map<String, Object> asMap() {
        Map<String, Object> map = super.asMap();
        putIfValueNotNull(map, NetworkConstants.SUBNET_IDS, subnetIds);
        putIfValueNotNull(map, NetworkConstants.AVAILABILITY_ZONES, availabilityZones);
        return map;
    }

    @Override
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public CloudPlatform getCloudPlatform() {
        return CloudPlatform.AZURE;
    }

    @Override
    public void parse(Map<String, Object> parameters) {
        subnetIds = getStringList(parameters, NetworkConstants.SUBNET_IDS);
        availabilityZones = getStringSet(parameters, NetworkConstants.AVAILABILITY_ZONES);
    }
}
