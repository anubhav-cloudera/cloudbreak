package com.sequenceiq.environment.api.v1.environment.model.response;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sequenceiq.environment.api.doc.environment.EnvironmentModelDescription;
import com.sequenceiq.environment.api.v1.environment.model.request.aws.AwsFreeIpaParameters;

import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FreeIpaResponse implements Serializable {

    @ApiModelProperty(EnvironmentModelDescription.FREEIPA_INSTANCE_COUNT_BY_GROUP)
    private Integer instanceCountByGroup = 1;

    @ApiModelProperty(EnvironmentModelDescription.FREEIPA_INSTANCE_TYPE)
    private String instanceType;

    @ApiModelProperty(EnvironmentModelDescription.FREEIPA_AWS_PARAMETERS)
    private AwsFreeIpaParameters aws;

    @ApiModelProperty(EnvironmentModelDescription.FREEIPA_IMAGE)
    private FreeIpaImageResponse image;

    @ApiModelProperty(value = EnvironmentModelDescription.MULTIAZ_FREEIPA)
    private boolean enableMultiAz;

    @ApiModelProperty(value = EnvironmentModelDescription.FREEIPA_RECIPES)
    private Set<String> recipes = new HashSet<>();

    public Integer getInstanceCountByGroup() {
        return instanceCountByGroup;
    }

    public void setInstanceCountByGroup(Integer instanceCountByGroup) {
        this.instanceCountByGroup = instanceCountByGroup;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public AwsFreeIpaParameters getAws() {
        return aws;
    }

    public void setAws(AwsFreeIpaParameters aws) {
        this.aws = aws;
    }

    public FreeIpaImageResponse getImage() {
        return image;
    }

    public void setImage(FreeIpaImageResponse image) {
        this.image = image;
    }

    public boolean isEnableMultiAz() {
        return enableMultiAz;
    }

    public void setEnableMultiAz(boolean enableMultiAz) {
        this.enableMultiAz = enableMultiAz;
    }

    public Set<String> getRecipes() {
        return recipes;
    }

    public void setRecipes(Set<String> recipes) {
        this.recipes = recipes;
    }

    @Override
    public String toString() {
        return "FreeIpaResponse{" +
                "instanceCountByGroup=" + instanceCountByGroup +
                "instanceType=" + instanceType +
                ", aws=" + aws +
                ", image=" + image +
                ", enableMultiAz=" + enableMultiAz +
                ", recipes=" + recipes +
                '}';
    }
}
