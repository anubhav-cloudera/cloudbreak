package com.sequenceiq.cloudbreak.cloud.model.catalog;

import java.util.Arrays;
import java.util.Optional;

public enum ImagePackageVersion {

    CDH_BUILD_NUMBER("cdh-build-number"),
    CDP_LOGGING_AGENT("cdp-logging-agent"),
    CFM("cfm", "Cloudera Flow Management"),
    CM("cm", "Cloudera Manager"),
    CM_BUILD_NUMBER("cm-build-number"),
    CSA("csa", "Cloudera Streaming Analytics with Apache Flink"),
    CSA_DH("csa-dh", "Cloudera Streaming Analytics with Apache Flink"),
    FREEIPA_LDAP_AGENT("freeipa-ldap-agent"),
    PROFILER("profiler", "Profiler Scheduler + Manager"),
    PYTHON38("python38", "Python 3.8"),
    SALT("salt"),
    SALT_BOOTSTRAP("salt-bootstrap"),
    SOURCE_IMAGE("source-image"),
    SPARK3("spark3", "Spark 3"),
    STACK("stack");

    private final String key;

    private final String displayName;

    ImagePackageVersion(String key) {
        this.key = key;
        this.displayName = key;
    }

    ImagePackageVersion(String key, String displayName) {
        this.key = key;
        this.displayName = displayName;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean hasProperDisplayName() {
        return !key.equals(displayName);
    }

    public static Optional<ImagePackageVersion> getByKey(String key) {
        return Arrays.stream(values())
                .filter(imagePackageVersion -> imagePackageVersion.getKey().equals(key))
                .findFirst();
    }
}
