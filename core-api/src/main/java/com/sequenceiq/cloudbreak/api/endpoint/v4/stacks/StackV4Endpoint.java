package com.sequenceiq.cloudbreak.api.endpoint.v4.stacks;

import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.ClusterOpDescription.PUT_BY_STACK_ID;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.ClusterOpDescription.SET_MAINTENANCE_MODE_BY_NAME;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.ClusterOpDescription.UPDATE_PILLAR_CONFIG;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.ClusterOpDescription.UPDATE_SALT;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.ATTACH_RECIPE_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.ATTACH_RECIPE_IN_WORKSPACE_INTERNAL;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.CHANGE_IMAGE_CATALOG;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.CHECK_FOR_UPGRADE_CLUSTER_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.CHECK_IMAGE_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.CHECK_IMAGE_IN_WORKSPACE_INTERNAL;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.CHECK_RDS_UPGRADE_INTERNAL;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.CHECK_STACK_UPGRADE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.CREATE_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.CREATE_IN_WORKSPACE_INTERNAL;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.DATABASE_BACKUP;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.DATABASE_BACKUP_INTERNAL;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.DATABASE_RESTORE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.DATABASE_RESTORE_INTERNAL;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.DELETE_BY_NAME_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.DELETE_BY_NAME_IN_WORKSPACE_INTERNAL;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.DELETE_INSTANCE_BY_ID_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.DELETE_MULTIPLE_INSTANCES_BY_ID_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.DELETE_WITH_KERBEROS_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.DETACH_RECIPE_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.DETACH_RECIPE_IN_WORKSPACE_INTERNAL;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.GENERATE_HOSTS_INVENTORY;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.GENERATE_IMAGE_CATALOG;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.GET_BY_CRN_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.GET_BY_NAME_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.GET_BY_NAME_WITH_RESOURCES_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.GET_STACK_REQUEST_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.GET_STATUS_BY_NAME;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.GET_USED_SUBNETS_BY_ENVIRONMENT_CRN;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.LIST_BY_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.POST_STACK_FOR_BLUEPRINT_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.PUT_BY_NAME;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.RANGER_RAZ_ENABLED;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.RDS_UPGRADE_INTERNAL;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.RECOVER_CLUSTER_IN_WORKSPACE_INTERNAL;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.REFRESH_RECIPES_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.REFRESH_RECIPES_IN_WORKSPACE_INTERNAL;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.REPAIR_CLUSTER_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.REPAIR_CLUSTER_IN_WORKSPACE_INTERNAL;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.RETRY_BY_NAME_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.RE_REGISTER_CLUSTER_PROXY_CONFIG;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.ROTATE_CERTIFICATES;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.ROTATE_SALT_PASSWORD_BY_CRN_IN_WORKSPACE_INTERNAL;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.SALT_PASSWORD_STATUS;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.SCALE_BY_NAME_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.STACK_UPGRADE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.STACK_UPGRADE_INTERNAL;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.START_BY_NAME_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.START_BY_NAME_IN_WORKSPACE_INTERNAL;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.STOP_BY_NAME_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.STOP_BY_NAME_IN_WORKSPACE_INTERNAL;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.SYNC_BY_NAME_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.SYNC_CM_BY_NAME_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.UPDATE_BY_NAME_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.UPDATE_LOAD_BALANCERS;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.UPDATE_LOAD_BALANCER_DNS_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.UPGRADE_CLUSTER_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.UPGRADE_OS_IN_WORKSPACE;
import static com.sequenceiq.distrox.api.v1.distrox.doc.DistroXOpDescription.DETERMINE_DATALAKE_DATA_SIZES;
import static com.sequenceiq.distrox.api.v1.distrox.doc.DistroXOpDescription.ROTATE_STACK_SECRETS;
import static com.sequenceiq.distrox.api.v1.distrox.doc.DistroXOpDescription.VERTICAL_SCALE_BY_NAME;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.sequenceiq.authorization.annotation.ResourceName;
import com.sequenceiq.cloudbreak.api.endpoint.v4.rotation.requests.StackV4SecretRotationRequest;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.CertificatesRotationV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.ChangeImageCatalogV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.ClusterRepairV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.MaintenanceModeV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.RotateSaltPasswordRequest;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.StackImageChangeV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.StackScaleV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.StackV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.StackVerticalScaleV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.UpdateClusterV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.UserNamePasswordV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.cluster.cm.ClouderaManagerSyncV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.osupgrade.OrderedOSUpgradeSetRequest;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.recipe.AttachRecipeV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.recipe.DetachRecipeV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.recipe.UpdateRecipesV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.tags.upgrade.UpgradeV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.CertificatesRotationV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.GeneratedBlueprintV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.RangerRazEnabledV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.SaltPasswordStatusResponse;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.StackStatusV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.StackV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.StackViewV4Responses;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.dr.BackupV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.dr.RestoreV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.imagecatalog.GenerateImageCatalogV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.recipe.AttachRecipeV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.recipe.DetachRecipeV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.recipe.UpdateRecipesV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.recovery.RecoveryV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.recovery.RecoveryValidationV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.upgrade.RdsUpgradeV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.upgrade.StackCcmUpgradeV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.upgrade.UpgradeOptionV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.upgrade.UpgradeV4Response;
import com.sequenceiq.cloudbreak.auth.crn.CrnResourceDescriptor;
import com.sequenceiq.cloudbreak.auth.security.internal.AccountId;
import com.sequenceiq.cloudbreak.common.database.TargetMajorVersion;
import com.sequenceiq.cloudbreak.doc.Notes;
import com.sequenceiq.cloudbreak.doc.OperationDescriptions;
import com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription;
import com.sequenceiq.cloudbreak.jerseyclient.RetryAndMetrics;
import com.sequenceiq.cloudbreak.validation.ValidCrn;
import com.sequenceiq.common.api.UsedSubnetsByEnvironmentResponse;
import com.sequenceiq.flow.api.model.FlowIdentifier;
import com.sequenceiq.flow.api.model.RetryableFlowResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RetryAndMetrics
@Path("/v4/{workspaceId}/stacks")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "/v4/{workspaceId}/stacks", protocols = "http,https",
        consumes = MediaType.APPLICATION_JSON)
public interface StackV4Endpoint {

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = LIST_BY_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = Notes.STACK_NOTES, nickname = "listStackInWorkspaceV4")
    StackViewV4Responses list(@PathParam("workspaceId") Long workspaceId, @QueryParam("environment") String environment,
            @QueryParam("onlyDatalakes") @DefaultValue("false") boolean onlyDatalakes);

    @POST
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = CREATE_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = Notes.STACK_NOTES, nickname = "postStackInWorkspaceV4")
    StackV4Response post(@PathParam("workspaceId") Long workspaceId, @Valid StackV4Request request, @AccountId @QueryParam("accountId") String accountId);

    @POST
    @Path("internal")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = CREATE_IN_WORKSPACE_INTERNAL, produces = MediaType.APPLICATION_JSON, notes = Notes.STACK_NOTES,
            nickname = "postStackInWorkspaceV4Internal")
    StackV4Response postInternal(@PathParam("workspaceId") Long workspaceId, @Valid StackV4Request request,
            @QueryParam("initiatorUserCrn") String initiatorUserCrn);

    @GET
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = GET_BY_NAME_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = Notes.STACK_NOTES, nickname = "getStackInWorkspaceV4")
    StackV4Response get(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name, @QueryParam("entries") Set<String> entries,
            @AccountId @QueryParam("accountId") String accountId);

    @GET
    @Path("{name}/withResources")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = GET_BY_NAME_WITH_RESOURCES_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = Notes.STACK_NOTES,
            nickname = "getStackInWorkspaceV4WithResources")
    StackV4Response getWithResources(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name, @QueryParam("entries") Set<String> entries,
            @AccountId @QueryParam("accountId") String accountId);

    @GET
    @Path("crn/{crn}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = GET_BY_CRN_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = Notes.STACK_NOTES, nickname = "getStackByCrnInWorkspaceV4")
    StackV4Response getByCrn(@PathParam("workspaceId") Long workspaceId, @PathParam("crn") String crn, @QueryParam("entries") Set<String> entries);

    @DELETE
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = DELETE_BY_NAME_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = Notes.STACK_NOTES, nickname = "deleteStackInWorkspaceV4")
    void delete(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @QueryParam("forced") @DefaultValue("false") boolean forced, @AccountId @QueryParam("accountId") String accountId);

    @DELETE
    @Path("internal/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = DELETE_BY_NAME_IN_WORKSPACE_INTERNAL, produces = MediaType.APPLICATION_JSON, notes = Notes.STACK_NOTES,
            nickname = "deleteStackInWorkspaceV4Internal")
    void deleteInternal(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name, @QueryParam("forced") @DefaultValue("false") boolean forced,
            @QueryParam("initiatorUserCrn") String initiatorUserCrn);

    @PUT
    @Path("{name}/update_name_crn_type")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = UPDATE_BY_NAME_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = Notes.STACK_NOTES,
            nickname = "updateNameCrnAndType")
    void updateNameAndCrn(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @QueryParam("initiatorUserCrn") String initiatorUserCrn, @QueryParam("newName") String newName, @QueryParam("newCrn") String newCrn,
            @QueryParam("retainOriginalName") @DefaultValue("false") boolean retainOriginalName);

    @PUT
    @Path("{name}/update_load_balancer_dns")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = UPDATE_LOAD_BALANCER_DNS_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = Notes.STACK_NOTES,
            nickname = "updateLoadBalancerDNS")
    void updateLoadBalancerDNS(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @QueryParam("initiatorUserCrn") String initiatorUserCrn);

    @POST
    @Path("{name}/sync")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = SYNC_BY_NAME_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = Notes.STACK_NOTES, nickname = "syncStackInWorkspaceV4")
    FlowIdentifier sync(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @AccountId @QueryParam("accountId") String accountId);

    @POST
    @Path("internal/{name}/sync_cm")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = SYNC_CM_BY_NAME_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = Notes.STACK_NOTES, nickname = "syncCmInWorkspaceV4")
    FlowIdentifier syncCm(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @QueryParam("initiatorUserCrn") String initiatorUserCrn, @Valid ClouderaManagerSyncV4Request syncRequest);

    @POST
    @Path("{name}/retry")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = RETRY_BY_NAME_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = Notes.RETRY_STACK_NOTES,
            nickname = "retryStackInWorkspaceV4")
    FlowIdentifier retry(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @AccountId @QueryParam("accountId") String accountId);

    @GET
    @Path("{name}/retry")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = StackOpDescription.LIST_RETRYABLE_FLOWS, produces = MediaType.APPLICATION_JSON, notes = Notes.LIST_RETRYABLE_NOTES,
            nickname = "listRetryableFlowsV4")
    List<RetryableFlowResponse> listRetryableFlows(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @AccountId @QueryParam("accountId") String accountId);

    @PUT
    @Path("{name}/stop")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = STOP_BY_NAME_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = Notes.STACK_NOTES, nickname = "stopStackInWorkspaceV4")
    FlowIdentifier putStop(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @AccountId @QueryParam("accountId") String accountId);

    @PUT
    @Path("internal/{name}/stop")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = STOP_BY_NAME_IN_WORKSPACE_INTERNAL, produces = MediaType.APPLICATION_JSON, notes = Notes.STACK_NOTES,
            nickname = "stopStackInWorkspaceV4Internal")
    FlowIdentifier putStopInternal(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @QueryParam("initiatorUserCrn") String initiatorUserCrn);

    @PUT
    @Path("{name}/start")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = START_BY_NAME_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = Notes.STACK_NOTES, nickname = "startStackInWorkspaceV4")
    FlowIdentifier putStart(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @AccountId @QueryParam("accountId") String accountId);

    @PUT
    @Path("internal/{name}/start")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = START_BY_NAME_IN_WORKSPACE_INTERNAL, produces = MediaType.APPLICATION_JSON, notes = Notes.STACK_NOTES,
            nickname = "startStackInWorkspaceV4Internal")
    FlowIdentifier putStartInternal(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @QueryParam("initiatorUserCrn") String initiatorUserCrn);

    @POST
    @Path("internal/{crn}/rotate_salt_password")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = ROTATE_SALT_PASSWORD_BY_CRN_IN_WORKSPACE_INTERNAL, produces = MediaType.APPLICATION_JSON, notes = Notes.STACK_NOTES,
            nickname = "rotateSaltPasswordForStackInWorkspaceV4Internal")
    FlowIdentifier rotateSaltPasswordInternal(@PathParam("workspaceId") Long workspaceId, @PathParam("crn") String crn,
            @Valid RotateSaltPasswordRequest request, @QueryParam("initiatorUserCrn") String initiatorUserCrn);

    @GET
    @Path("internal/{crn}/rotate_salt_password/status")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = SALT_PASSWORD_STATUS, produces = MediaType.APPLICATION_JSON, notes = Notes.STACK_NOTES,
            nickname = "getSaltPasswordStatusForStackInWorkspaceV4Internal")
    SaltPasswordStatusResponse getSaltPasswordStatus(@PathParam("workspaceId") Long workspaceId, @PathParam("crn") String crn);

    @PUT
    @Path("internal/{crn}/modify_proxy")
    @Produces(MediaType.APPLICATION_JSON)
    FlowIdentifier modifyProxyConfigInternal(@PathParam("workspaceId") Long workspaceId, @PathParam("crn") String crn,
            @QueryParam("previousProxyConfigCrn") String previousProxyConfigCrn, @QueryParam("initiatorUserCrn") String initiatorUserCrn);

    @PUT
    @Path("{name}/scaling")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = SCALE_BY_NAME_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = Notes.STACK_NOTES,
            nickname = "putScalingStackInWorkspaceV4")
    FlowIdentifier putScaling(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name, @Valid StackScaleV4Request updateRequest,
            @AccountId @QueryParam("accountId") String accountId);

    @POST
    @Path("{name}/manual_repair")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = REPAIR_CLUSTER_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = Notes.CLUSTER_REPAIR_NOTES,
            nickname = "repairStackInWorkspaceV4")
    FlowIdentifier repairCluster(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @Valid ClusterRepairV4Request clusterRepairRequest, @AccountId @QueryParam("accountId") String accountId);

    @POST
    @Path("internal/{name}/manual_repair")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = REPAIR_CLUSTER_IN_WORKSPACE_INTERNAL, produces = MediaType.APPLICATION_JSON, notes = Notes.CLUSTER_REPAIR_NOTES,
            nickname = "repairStackInWorkspaceV4Internal")
    FlowIdentifier repairClusterInternal(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @Valid ClusterRepairV4Request clusterRepairRequest, @QueryParam("initiatorUserCrn") String initiatorUserCrn);

    @POST
    @Path("{name}/upgrade")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = UPGRADE_CLUSTER_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = Notes.CLUSTER_UPGRADE_NOTES,
            nickname = "upgradeOsInWorkspaceV4")
    FlowIdentifier upgradeOs(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @AccountId @QueryParam("accountId") String accountId, @QueryParam("keepVariant") Boolean keepVariant);

    @POST
    @Path("internal/{crn}/os_upgrade_by_upgrade_sets")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = UPGRADE_OS_IN_WORKSPACE, nickname = "osUpgradeByUpgradeSetsInternalStack")
    FlowIdentifier upgradeOsByUpgradeSetsInternal(@PathParam("workspaceId") Long workspaceId,
            @PathParam("crn") String crn, OrderedOSUpgradeSetRequest orderedOsUpgradeSetRequest);

    @POST
    @Path("internal/{name}/upgrade")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = UPGRADE_CLUSTER_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = Notes.CLUSTER_UPGRADE_NOTES,
            nickname = "upgradeOsInWorkspaceV4Internal")
    FlowIdentifier upgradeOsInternal(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @QueryParam("initiatorUserCrn") String initiatorUserCrn, @QueryParam("keepVariant") Boolean keepVariant);

    @GET
    @Path("{name}/check_for_upgrade")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = CHECK_FOR_UPGRADE_CLUSTER_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = Notes.CHECK_FOR_CLUSTER_UPGRADE_NOTES,
            nickname = "checkForOsUpgradeInWorkspaceV4")
    UpgradeOptionV4Response checkForOsUpgrade(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @AccountId @QueryParam("accountId") String accountId);

    @POST
    @Path("{name}/blueprint")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = POST_STACK_FOR_BLUEPRINT_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON,
            notes = Notes.STACK_NOTES, nickname = "postStackForBlueprintV4")
    GeneratedBlueprintV4Response postStackForBlueprint(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @Valid StackV4Request stackRequest, @AccountId @QueryParam("accountId") String accountId);

    @PUT
    @Path("{name}/change_image")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = CHECK_IMAGE_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON,
            notes = Notes.STACK_NOTES, nickname = "changeImageStackInWorkspaceV4")
    FlowIdentifier changeImage(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @Valid StackImageChangeV4Request stackImageChangeRequest, @AccountId @QueryParam("accountId") String accountId);

    @PUT
    @Path("internal/{name}/change_image")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = CHECK_IMAGE_IN_WORKSPACE_INTERNAL, produces = MediaType.APPLICATION_JSON,
            notes = Notes.STACK_NOTES, nickname = "changeImageStackInWorkspaceV4Internal")
    FlowIdentifier changeImageInternal(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @Valid StackImageChangeV4Request stackImageChangeRequest, @QueryParam("initiatorUserCrn") String initiatorUserCrn);

    @DELETE
    @Path("{name}/cluster")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = DELETE_WITH_KERBEROS_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = Notes.CLUSTER_NOTES)
    void deleteWithKerberos(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @QueryParam("forced") @DefaultValue("false") boolean forced, @AccountId @QueryParam("accountId") String accountId);

    @GET
    @Path("{name}/request")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = GET_STACK_REQUEST_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = Notes.STACK_NOTES,
            nickname = "getStackRequestFromNameV4")
    StackV4Request getRequestfromName(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @AccountId @QueryParam("accountId") String accountId);

    @GET
    @Path("{name}/status")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = GET_STATUS_BY_NAME, produces = MediaType.APPLICATION_JSON, notes = Notes.STACK_NOTES,
            nickname = "statusStackV4")
    StackStatusV4Response getStatusByName(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @AccountId @QueryParam("accountId") String accountId);

    @DELETE
    @Path("{name}/instance")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = DELETE_INSTANCE_BY_ID_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = Notes.STACK_NOTES,
            nickname = "deleteInstanceStackV4")
    FlowIdentifier deleteInstance(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @QueryParam("forced") @DefaultValue("false") boolean forced, @QueryParam("instanceId") String instanceId,
            @AccountId @QueryParam("accountId") String accountId);

    @DELETE
    @Path("{name}/instances")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = DELETE_MULTIPLE_INSTANCES_BY_ID_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = Notes.STACK_NOTES,
            nickname = "deleteMultipleInstancesStackV4")
    FlowIdentifier deleteMultipleInstances(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @QueryParam("id") @NotEmpty List<String> instances, @QueryParam("forced") @DefaultValue("false") boolean forced,
            @AccountId @QueryParam("accountId") String accountId);

    @PUT
    @Path("{name}/ambari_password")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = PUT_BY_NAME, produces = MediaType.APPLICATION_JSON, notes = Notes.STACK_NOTES,
            nickname = "putpasswordStackV4")
    FlowIdentifier putPassword(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @Valid UserNamePasswordV4Request userNamePasswordJson, @AccountId @QueryParam("accountId") String accountId);

    @PUT
    @Path("{name}/maintenance")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = SET_MAINTENANCE_MODE_BY_NAME, produces = MediaType.APPLICATION_JSON, notes = Notes.MAINTENANCE_NOTES,
            nickname = "setClusterMaintenanceMode")
    FlowIdentifier setClusterMaintenanceMode(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @NotNull MaintenanceModeV4Request maintenanceMode, @AccountId @QueryParam("accountId") String accountId);

    @PUT
    @Path("{name}/cluster")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = PUT_BY_STACK_ID, produces = MediaType.APPLICATION_JSON, notes = Notes.CLUSTER_NOTES, nickname = "putClusterV4")
    FlowIdentifier putCluster(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name, @Valid UpdateClusterV4Request updateJson,
            @AccountId @QueryParam("accountId") String accountId);

    @GET
    @Path("{name}/inventory")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(value = GENERATE_HOSTS_INVENTORY, produces = MediaType.TEXT_PLAIN, nickname = "getClusterHostsInventory")
    String getClusterHostsInventory(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @AccountId @QueryParam("accountId") String accountId);

    @POST
    @Path("{name}/check_cluster_upgrade")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = CHECK_STACK_UPGRADE, nickname = "checkForClusterUpgradeByName")
    UpgradeV4Response checkForClusterUpgradeByName(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @NotNull UpgradeV4Request request, @AccountId @QueryParam("accountId") String accountId);

    @POST
    @Path("{name}/cluster_upgrade")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = STACK_UPGRADE, nickname = "upgradeClusterByName")
    FlowIdentifier upgradeClusterByName(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name, String imageId,
            @AccountId @QueryParam("accountId") String accountId);

    @POST
    @Path("internal/{name}/cluster_upgrade")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = STACK_UPGRADE_INTERNAL, nickname = "upgradeClusterByNameInternal")
    FlowIdentifier upgradeClusterByNameInternal(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @QueryParam("imageId") String imageId, @QueryParam("initiatorUserCrn") String initiatorUserCrn,
            @QueryParam("rollingUpgradeEnabled") Boolean rollingUpgradeEnabled);

    @POST
    @Path("internal/{crn}/prepare_cluster_upgrade")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = STACK_UPGRADE_INTERNAL, nickname = "prepareClusterUpgradeByCrnInternal")
    FlowIdentifier prepareClusterUpgradeByCrnInternal(@PathParam("workspaceId") Long workspaceId, @PathParam("crn") String crn,
            @QueryParam("imageId") String imageId, @QueryParam("initiatorUserCrn") String initiatorUserCrn);

    @PUT
    @Path("internal/{name}/check_rds_upgrade")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = CHECK_RDS_UPGRADE_INTERNAL, nickname = "checkUpgradeRdsByNameInternal")
    void checkUpgradeRdsByClusterNameInternal(@PathParam("workspaceId") Long workspaceId,
            @NotEmpty @ResourceName @PathParam("name") String clusterName,
            @QueryParam("targetVersion") TargetMajorVersion targetMajorVersion,
            @NotEmpty @ValidCrn(resource = {CrnResourceDescriptor.USER, CrnResourceDescriptor.MACHINE_USER}) @QueryParam("initiatorUserCrn")
            String initiatorUserCrn);

    @PUT
    @Path("internal/{name}/rds_upgrade")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = RDS_UPGRADE_INTERNAL, nickname = "upgradeRdsByNameInternal")
    RdsUpgradeV4Response upgradeRdsByClusterNameInternal(@PathParam("workspaceId") Long workspaceId,
            @NotEmpty @ResourceName @PathParam("name") String clusterName,
            @QueryParam("targetVersion") TargetMajorVersion targetMajorVersion,
            @NotEmpty @ValidCrn(resource = {CrnResourceDescriptor.USER, CrnResourceDescriptor.MACHINE_USER}) @QueryParam("initiatorUserCrn")
            String initiatorUserCrn);

    @Deprecated
    @PUT
    @Path("internal/{name}/upgrade_ccm")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Initiates the CCM tunnel type upgrade to the latest available version", nickname = "upgradeCcmByNameInternal")
    StackCcmUpgradeV4Response upgradeCcmByNameInternal(@PathParam("workspaceId") Long workspaceId, @NotEmpty @ResourceName @PathParam("name") String name,
            @NotEmpty @ValidCrn(resource = {CrnResourceDescriptor.USER, CrnResourceDescriptor.MACHINE_USER})
            @QueryParam("initiatorUserCrn") String initiatorUserCrn);

    @PUT
    @Path("internal/crn/{crn}/upgrade_ccm")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Initiates the CCM tunnel type upgrade to the latest available version", nickname = "upgradeCcmByCrnInternal")
    StackCcmUpgradeV4Response upgradeCcmByCrnInternal(@PathParam("workspaceId") Long workspaceId,
            @NotEmpty @ValidCrn(resource = {CrnResourceDescriptor.DATAHUB, CrnResourceDescriptor.DATALAKE}) @PathParam("crn") String crn,
            @NotEmpty @ValidCrn(resource = {CrnResourceDescriptor.USER, CrnResourceDescriptor.MACHINE_USER})
            @QueryParam("initiatorUserCrn") String initiatorUserCrn);

    @GET
    @Path("internal/{envCrn}/upgrade_ccm_stacks_remaining")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Returns the count of not upgraded stacks for an environment CRN", nickname = "getNotCcmUpgradedStackCountInternal")
    int getNotCcmUpgradedStackCount(@PathParam("workspaceId") Long workspaceId,
            @NotEmpty @ValidCrn(resource = CrnResourceDescriptor.ENVIRONMENT) @PathParam("envCrn") String envCrn,
            @NotEmpty @ValidCrn(resource = {CrnResourceDescriptor.USER, CrnResourceDescriptor.MACHINE_USER})
            @QueryParam("initiatorUserCrn") String initiatorUserCrn);

    @PUT
    @Path("{name}/salt_update")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = UPDATE_SALT, nickname = "updateSaltByName")
    FlowIdentifier updateSaltByName(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @AccountId @QueryParam("accountId") String accountId);

    /**
     * @deprecated Use updatePillarConfigurationByCrn instead
     */
    @PUT
    @Path("{name}/pillar_config_update")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = UPDATE_PILLAR_CONFIG, nickname = "updatePillarConfigurationByName")
    @Deprecated
    FlowIdentifier updatePillarConfigurationByName(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name);

    @PUT
    @Path("crn/{crn}/pillar_config_update")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = UPDATE_PILLAR_CONFIG, nickname = "updatePillarConfigurationByCrn")
    FlowIdentifier updatePillarConfigurationByCrn(@PathParam("workspaceId") Long workspaceId, @PathParam("crn") String crn);

    @POST
    @Path("{name}/database_backup")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = DATABASE_BACKUP, nickname = "databaseBackup")
    BackupV4Response backupDatabaseByName(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @QueryParam("backupLocation") String backupLocation, @QueryParam("backupId") String backupId,
            @QueryParam("skipDatabaseNames") List<String> skipDatabaseNames,
            @AccountId @QueryParam("accountId") String accountId, @QueryParam("databaseMaxDurationInMin") int databaseMaxDurationInMin);

    @SuppressWarnings("ParameterNumber")
    @POST
    @Path("internal/{name}/database_backup")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = DATABASE_BACKUP_INTERNAL, nickname = "databaseBackupInternal")
    BackupV4Response backupDatabaseByNameInternal(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @QueryParam("backupId") String backupId, @QueryParam("backupLocation") String backupLocation,
            @QueryParam("closeConnections") boolean closeConnections,
            @QueryParam("skipDatabaseNames") List<String> skipDatabaseNames, @QueryParam("initiatorUserCrn") String initiatorUserCrn,
            @QueryParam("databaseMaxDurationInMin") int databaseMaxDurationInMin);

    @POST
    @Path("{name}/database_restore")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = DATABASE_RESTORE, nickname = "databaseRestore")
    RestoreV4Response restoreDatabaseByName(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @QueryParam("backupLocation") String backupLocation, @QueryParam("backupId") String backupId,
            @AccountId @QueryParam("accountId") String accountId, @QueryParam("databaseMaxDurationInMin") int databaseMaxDurationInMin);

    @POST
    @Path("internal/{name}/database_restore")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = DATABASE_RESTORE_INTERNAL, nickname = "databaseRestoreInternal")
    RestoreV4Response restoreDatabaseByNameInternal(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @QueryParam("backupLocation") String backupLocation, @QueryParam("backupId") String backupId,
            @QueryParam("initiatorUserCrn") String initiatorUserCrn, @QueryParam("databaseMaxDurationInMin") int databaseMaxDurationInMin);

    @POST
    @Path("internal/{name}/cluster_recover")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = RECOVER_CLUSTER_IN_WORKSPACE_INTERNAL, nickname = "recoverClusterInternal")
    RecoveryV4Response recoverClusterByNameInternal(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @QueryParam("initiatorUserCrn") String initiatorUserCrn);

    @GET
    @Path("internal/{name}/cluster_recoverable")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "validates if the cluster is recoverable or not", nickname = "getClusterRecoverableByNameInternal")
    RecoveryValidationV4Response getClusterRecoverableByNameInternal(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @QueryParam("initiatorUserCrn") String initiatorUserCrn);

    @PUT
    @Path("{name}/refresh_recipes")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = REFRESH_RECIPES_IN_WORKSPACE, nickname = "refreshStackRecipes")
    UpdateRecipesV4Response refreshRecipes(@PathParam("workspaceId") Long workspaceId, @Valid UpdateRecipesV4Request request,
            @PathParam("name") String name, @AccountId @QueryParam("accountId") String accountId);

    @PUT
    @Path("internal/{name}/refresh_recipes")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = REFRESH_RECIPES_IN_WORKSPACE_INTERNAL, nickname = "refreshRecipesInternal")
    UpdateRecipesV4Response refreshRecipesInternal(@PathParam("workspaceId") Long workspaceId, @Valid UpdateRecipesV4Request request,
            @PathParam("name") String name, @QueryParam("initiatorUserCrn") String initiatorUserCrn);

    @POST
    @Path("{name}/attach_recipe")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = ATTACH_RECIPE_IN_WORKSPACE, nickname = "attachStackRecipe")
    AttachRecipeV4Response attachRecipe(@PathParam("workspaceId") Long workspaceId, @Valid AttachRecipeV4Request request,
            @PathParam("name") String name, @AccountId @QueryParam("accountId") String accountId);

    @POST
    @Path("internal/{name}/attach_recipe")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = ATTACH_RECIPE_IN_WORKSPACE_INTERNAL, nickname = "attachRecipeInternal")
    AttachRecipeV4Response attachRecipeInternal(@PathParam("workspaceId") Long workspaceId, @Valid AttachRecipeV4Request request,
            @PathParam("name") String name, @QueryParam("initiatorUserCrn") String initiatorUserCrn);

    @POST
    @Path("{name}/detach_recipe")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = DETACH_RECIPE_IN_WORKSPACE, nickname = "detachStackRecipe")
    DetachRecipeV4Response detachRecipe(@PathParam("workspaceId") Long workspaceId, @Valid DetachRecipeV4Request request,
            @PathParam("name") String name, @AccountId @QueryParam("accountId") String accountId);

    @POST
    @Path("internal/{name}/detach_recipe")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = DETACH_RECIPE_IN_WORKSPACE_INTERNAL, nickname = "detachRecipeInternal")
    DetachRecipeV4Response detachRecipeInternal(@PathParam("workspaceId") Long workspaceId, @Valid DetachRecipeV4Request request,
            @PathParam("name") String name, @QueryParam("initiatorUserCrn") String initiatorUserCrn);

    @PUT
    @Path("internal/{name}/rotate_autotls_certificates")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = ROTATE_CERTIFICATES, nickname = "rotateAutoTlsCertificates")
    CertificatesRotationV4Response rotateAutoTlsCertificates(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @QueryParam("initiatorUserCrn") String initiatorUserCrn, @Valid CertificatesRotationV4Request rotateCertificateRequest);

    @POST
    @Path("internal/{name}/renew_certificate")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = OperationDescriptions.StackOpDescription.RENEW_CERTIFICATE, produces = MediaType.APPLICATION_JSON,
            notes = Notes.RENEW_CERTIFICATE_NOTES, nickname = "renewStackCertificate")
    FlowIdentifier renewCertificate(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @QueryParam("initiatorUserCrn") String initiatorUserCrn);

    @POST
    @Path("internal/crn/{crn}/renew_certificate")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = OperationDescriptions.StackOpDescription.RENEW_CERTIFICATE, produces = MediaType.APPLICATION_JSON,
            notes = Notes.RENEW_CERTIFICATE_NOTES, nickname = "renewInternalStackCertificate")
    FlowIdentifier renewInternalCertificate(@PathParam("workspaceId") Long workspaceId, @PathParam("crn") String crn);

    @PUT
    @Path("internal/{name}/update_load_balancers")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = UPDATE_LOAD_BALANCERS, nickname = "updateLoadBalancersInternal")
    FlowIdentifier updateLoadBalancersInternal(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @QueryParam("initiatorUserCrn") String initiatorUserCrn);

    @PUT
    @Path("internal/{name}/change_image_catalog")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = CHANGE_IMAGE_CATALOG, nickname = "changeImageCatalogInternal")
    void changeImageCatalogInternal(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @QueryParam("initiatorUserCrn") String initiatorUserCrn, @Valid @NotNull ChangeImageCatalogV4Request changeImageCatalogRequest);

    @GET
    @Path("internal/{name}/generate_image_catalog")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = GENERATE_IMAGE_CATALOG, nickname = "generateImageCatalogInternal")
    GenerateImageCatalogV4Response generateImageCatalogInternal(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @QueryParam("initiatorUserCrn") String initiatorUserCrn);

    @GET
    @Path("internal/{crn}/ranger_raz_enabled")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = RANGER_RAZ_ENABLED, produces = MediaType.APPLICATION_JSON, nickname = "rangerRazEnabled")
    RangerRazEnabledV4Response rangerRazEnabledInternal(@PathParam("workspaceId") Long workspaceId, @PathParam("crn") String crn,
            @QueryParam("initiatorUserCrn") String initiatorUserCrn);

    @PUT
    @Path("internal/{crn}/re_register_cluster_proxy_config")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = RE_REGISTER_CLUSTER_PROXY_CONFIG, nickname = "reRegisterClusterProxyConfig")
    FlowIdentifier reRegisterClusterProxyConfig(@PathParam("workspaceId") Long workspaceId, @PathParam("crn") String crn,
            @QueryParam("skipFullReRegistration") @DefaultValue("false") boolean skipFullReRegistration, @QueryParam("originalCrn") String originalCrn,
            @QueryParam("initiatorUserCrn") String initiatorUserCrn);

    @PUT
    @Path("internal/{name}/vertical_scaling")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = VERTICAL_SCALE_BY_NAME, produces = MediaType.APPLICATION_JSON, notes = Notes.STACK_NOTES,
            nickname = "verticalScalingInternalByName")
    FlowIdentifier verticalScalingByName(
            @PathParam("workspaceId") Long workspaceId,
            @PathParam("name") String name,
            @QueryParam("initiatorUserCrn") String initiatorUserCrn,
            @Valid StackVerticalScaleV4Request updateRequest);

    @GET
    @Path("internal/used_subnets")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = GET_USED_SUBNETS_BY_ENVIRONMENT_CRN, produces = MediaType.APPLICATION_JSON, notes = Notes.STACK_NOTES,
            nickname = "getUsedSubnetsByEnvironment")
    UsedSubnetsByEnvironmentResponse getUsedSubnetsByEnvironment(
            @PathParam("workspaceId") Long workspaceId,
            @ValidCrn(resource = CrnResourceDescriptor.ENVIRONMENT) @QueryParam("environmentCrn") String environmentCrn);

    @POST
    @Path("{name}/determine_datalake_data_sizes")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = DETERMINE_DATALAKE_DATA_SIZES, nickname = "determineDatalakeDataSizes")
    void determineDatalakeDataSizes(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @QueryParam("operationId") String operationId);

    @PUT
    @Path("internal/rotate_secret")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = ROTATE_STACK_SECRETS, produces = MediaType.APPLICATION_JSON, nickname = "rotateStackSecrets", hidden = true)
    FlowIdentifier rotateSecrets(@PathParam("workspaceId") Long workspaceId, @Valid @NotNull StackV4SecretRotationRequest request,
            @QueryParam("initiatorUserCrn") String initiatorUserCrn);

    @POST
    @Path("{crn}/cm_rolling_restart")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Services rolling restart", produces = MediaType.APPLICATION_JSON, nickname = "rollingRestartServices")
    FlowIdentifier rollingRestartServices(@PathParam("workspaceId") Long workspaceId, @PathParam("crn") String crn);
}
