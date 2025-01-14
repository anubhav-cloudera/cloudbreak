package com.sequenceiq.environment.exception.mapper;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Component;

import com.sequenceiq.environment.exception.MarketplaceTermsAlreadySetException;

@Provider
@Component
public class MarketplaceTermsAlreadySetExceptionMapper extends EnvironmentBaseExceptionMapper<MarketplaceTermsAlreadySetException> {

    @Override
    public Status getResponseStatus(MarketplaceTermsAlreadySetException exception) {
        return Status.CONFLICT;
    }

    @Override
    public Class<MarketplaceTermsAlreadySetException> getExceptionType() {
        return MarketplaceTermsAlreadySetException.class;
    }

}