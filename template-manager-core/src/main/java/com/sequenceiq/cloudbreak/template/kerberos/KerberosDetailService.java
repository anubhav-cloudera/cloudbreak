package com.sequenceiq.cloudbreak.template.kerberos;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.sequenceiq.cloudbreak.common.json.JsonUtil;
import com.sequenceiq.cloudbreak.common.mappable.CloudPlatform;
import com.sequenceiq.cloudbreak.dto.KerberosConfig;
import com.sequenceiq.cloudbreak.type.KerberosType;

@Service
public class KerberosDetailService {

    @Value("${cb.freeipa.dns.ttl}")
    private int dnsTtl;

    private final Gson gson = new Gson();

    public String resolveHostForKdcAdmin(@Nonnull KerberosConfig kerberosConfig, String defaultHost) {
        String adminHost = Optional.ofNullable(kerberosConfig.getAdminUrl()).orElse("").trim();
        return adminHost.isEmpty() ? defaultHost : adminHost;
    }

    public boolean areClusterManagerManagedKerberosPackages(@Nonnull KerberosConfig kerberosConfig) throws IOException {
        return getBooleanConfigValue(kerberosConfig.getDescriptor(), new String[] { "kerberos-env", "properties", "install_packages" }, true);
    }

    public boolean isClusterManagerManagedKrb5Config(@Nonnull KerberosConfig kerberosConfig) throws IOException {
        return getBooleanConfigValue(kerberosConfig.getKrb5Conf(), new String[] { "krb5-conf", "properties", "manage_krb5_conf" }, false);
    }

    private boolean getBooleanConfigValue(String configJson, String[] path, boolean defaultValue) throws IOException {
        if (isEmpty(configJson)) {
            return defaultValue;
        }
        JsonNode node = JsonUtil.readTree(configJson);
        for (String p : path) {
            node = node.get(p);
            if (node == null) {
                return defaultValue;
            }
        }
        return node.asBoolean();
    }

    public Map<String, Object> getKerberosEnvProperties(@Nonnull KerberosConfig kerberosConfig) {
        Map<String, Object> kerberosEnv = (Map<String, Object>) gson
                .fromJson(kerberosConfig.getDescriptor(), Map.class).get("kerberos-env");
        return (Map<String, Object>) kerberosEnv.get("properties");
    }

    public boolean isAdJoinable(KerberosConfig kerberosConfig) {
        return kerberosConfig != null && kerberosConfig.getType() == KerberosType.ACTIVE_DIRECTORY;
    }

    public boolean isIpaJoinable(KerberosConfig kerberosConfig) {
        return kerberosConfig != null && kerberosConfig.getType() == KerberosType.FREEIPA;
    }

    // TODO remove Cloudplatform check when FreeIPA registration is ready
    public boolean keytabsShouldBeUpdated(String cloudPlatform, boolean childEnvironment, Optional<KerberosConfig> kerberosConfigOptional) {
        boolean yarnChildEnvironment = CloudPlatform.YARN.name().equals(cloudPlatform)
                && childEnvironment;

        boolean supportedOnCloudPlatform = CloudPlatform.AWS.name().equals(cloudPlatform)
                || CloudPlatform.AZURE.name().equals(cloudPlatform)
                || CloudPlatform.GCP.name().equals(cloudPlatform)
                || yarnChildEnvironment;

        return supportedOnCloudPlatform
                && kerberosConfigOptional.isPresent() && isIpaJoinable(kerberosConfigOptional.get());
    }

    public int getDnsTtl() {
        return dnsTtl;
    }
}
