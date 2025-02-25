package com.sequenceiq.distrox.api.v1.distrox.model.network.azure;

import java.io.Serializable;
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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class InstanceGroupAzureNetworkV1Parameters extends MappableBase implements Serializable {

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

    @Override
    public Map<String, Object> asMap() {
        Map<String, Object> map = super.asMap();
        map.put(NetworkConstants.SUBNET_IDS, subnetIds);
        map.put(NetworkConstants.AVAILABILITY_ZONES, availabilityZones);
        return map;
    }
}
