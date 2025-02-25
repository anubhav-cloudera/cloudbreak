package com.sequenceiq.freeipa.configuration;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.ext.ExceptionMapper;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.sequenceiq.authorization.controller.AuthorizationInfoController;
import com.sequenceiq.cloudbreak.exception.mapper.DefaultExceptionMapper;
import com.sequenceiq.cloudbreak.structuredevent.rest.controller.CDPStructuredEventV1Controller;
import com.sequenceiq.cloudbreak.structuredevent.rest.filter.CDPRestAuditFilter;
import com.sequenceiq.cloudbreak.structuredevent.rest.filter.CDPStructuredEventFilter;
import com.sequenceiq.flow.controller.FlowPublicController;
import com.sequenceiq.freeipa.api.FreeIpaApi;
import com.sequenceiq.freeipa.controller.ClientTestV1Controller;
import com.sequenceiq.freeipa.controller.DiagnosticsV1Controller;
import com.sequenceiq.freeipa.controller.DnsV1Controller;
import com.sequenceiq.freeipa.controller.FreeIpaCO2V1Controller;
import com.sequenceiq.freeipa.controller.FreeIpaCostV1Controller;
import com.sequenceiq.freeipa.controller.FreeIpaRotationV1Controller;
import com.sequenceiq.freeipa.controller.FreeIpaUpgradeV1Controller;
import com.sequenceiq.freeipa.controller.FreeIpaV1Controller;
import com.sequenceiq.freeipa.controller.FreeIpaV1FlowController;
import com.sequenceiq.freeipa.controller.OperationV1Controller;
import com.sequenceiq.freeipa.controller.ProgressV1Controller;
import com.sequenceiq.freeipa.controller.RecipeV1Controller;
import com.sequenceiq.freeipa.controller.UserV1Controller;
import com.sequenceiq.freeipa.controller.UtilV1Controller;
import com.sequenceiq.freeipa.controller.mapper.WebApplicaitonExceptionMapper;
import com.sequenceiq.freeipa.kerberos.v1.KerberosConfigV1Controller;
import com.sequenceiq.freeipa.kerberosmgmt.v1.KerberosMgmtV1Controller;
import com.sequenceiq.freeipa.ldap.v1.LdapConfigV1Controller;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.config.SwaggerConfigLocator;
import io.swagger.jaxrs.config.SwaggerContextService;

@ApplicationPath(FreeIpaApi.API_ROOT_CONTEXT)
@Configuration
public class EndpointConfig extends ResourceConfig {

    private static final List<Class<?>> CONTROLLERS = List.of(
            UserV1Controller.class,
            ClientTestV1Controller.class,
            FreeIpaV1Controller.class,
            LdapConfigV1Controller.class,
            KerberosConfigV1Controller.class,
            KerberosMgmtV1Controller.class,
            DnsV1Controller.class,
            OperationV1Controller.class,
            FreeIpaV1FlowController.class,
            FlowPublicController.class,
            AuthorizationInfoController.class,
            DiagnosticsV1Controller.class,
            ProgressV1Controller.class,
            CDPStructuredEventV1Controller.class,
            UtilV1Controller.class,
            FreeIpaUpgradeV1Controller.class,
            RecipeV1Controller.class,
            FreeIpaCostV1Controller.class,
            FreeIpaCO2V1Controller.class,
            FreeIpaRotationV1Controller.class);

    @Value("${info.app.version:unspecified}")
    private String applicationVersion;

    @Value("${freeipa.structuredevent.rest.enabled:false}")
    private Boolean auditEnabled;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Inject
    private List<ExceptionMapper<?>> exceptionMappers;

    @PostConstruct
    private void init() {
        registerFilters();
        registerEndpoints();
        registerExceptionMappers();
    }

    @PostConstruct
    private void registerSwagger() {
        BeanConfig swaggerConfig = new BeanConfig();
        swaggerConfig.setTitle("FreeIPA API");
        swaggerConfig.setDescription("API for working with FreeIPA clusters");
        swaggerConfig.setVersion(applicationVersion);
        swaggerConfig.setSchemes(new String[]{"http", "https"});
        swaggerConfig.setBasePath(contextPath + FreeIpaApi.API_ROOT_CONTEXT);
        swaggerConfig.setLicenseUrl("https://github.com/sequenceiq/cloudbreak/blob/master/LICENSE");
        swaggerConfig.setResourcePackage("com.sequenceiq.freeipa.api,com.sequenceiq.flow.api,com.sequenceiq.authorization," +
                "com.sequenceiq.cloudbreak.structuredevent.rest.endpoint");
        swaggerConfig.setScan(true);
        swaggerConfig.setContact("https://hortonworks.com/contact-sales/");
        swaggerConfig.setPrettyPrint(true);
        SwaggerConfigLocator.getInstance().putConfig(SwaggerContextService.CONFIG_ID_DEFAULT, swaggerConfig);
    }

    private void registerExceptionMappers() {
        for (ExceptionMapper<?> mapper : exceptionMappers) {
            register(mapper);
        }
        register(WebApplicaitonExceptionMapper.class);
        register(DefaultExceptionMapper.class);
    }

    private void registerEndpoints() {
        CONTROLLERS.forEach(this::register);

        register(io.swagger.jaxrs.listing.ApiListingResource.class);
        register(io.swagger.jaxrs.listing.SwaggerSerializers.class);
        register(io.swagger.jaxrs.listing.AcceptHeaderApiListingResource.class);
    }

    private void registerFilters() {
        register(CDPRestAuditFilter.class);
        if (auditEnabled) {
            register(CDPStructuredEventFilter.class);
        }
    }
}

