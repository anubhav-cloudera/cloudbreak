package com.sequenceiq.distrox.api.v1.distrox.model;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sequenceiq.cloudbreak.auth.crn.CrnResourceDescriptor;
import com.sequenceiq.cloudbreak.rotation.annotation.ValidMultiSecretType;
import com.sequenceiq.cloudbreak.validation.ValidCrn;

import io.swagger.annotations.ApiModel;

@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class DistroXChildResourceMarkingRequest {

    @ValidCrn(resource = { CrnResourceDescriptor.ENVIRONMENT, CrnResourceDescriptor.DATALAKE })
    private String parentCrn;

    @ValidMultiSecretType
    @NotEmpty
    private String secret;

    public String getParentCrn() {
        return parentCrn;
    }

    public void setParentCrn(String parentCrn) {
        this.parentCrn = parentCrn;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
