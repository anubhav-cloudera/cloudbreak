package com.sequenceiq.freeipa.controller.mapper;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import javax.ws.rs.core.Response.Status;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.cloud.service.GetCloudParameterException;
import com.sequenceiq.cloudbreak.exception.mapper.BaseExceptionMapper;

@Component
public class GetCloudParameterExceptionMapper extends BaseExceptionMapper<GetCloudParameterException> {

    @Override
    public Status getResponseStatus(GetCloudParameterException exception) {
        return INTERNAL_SERVER_ERROR;
    }

    @Override
    public Class<GetCloudParameterException> getExceptionType() {
        return GetCloudParameterException.class;
    }

    @Override
    protected boolean logException() {
        return false;
    }
}
