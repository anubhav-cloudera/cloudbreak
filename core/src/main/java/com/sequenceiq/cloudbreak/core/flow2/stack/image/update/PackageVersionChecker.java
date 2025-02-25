package com.sequenceiq.cloudbreak.core.flow2.stack.image.update;

import static com.sequenceiq.cloudbreak.event.ResourceEvent.CLUSTER_PACKAGES_ON_INSTANCES_ARE_DIFFERENT;
import static com.sequenceiq.cloudbreak.event.ResourceEvent.CLUSTER_PACKAGE_VERSIONS_ON_INSTANCES_ARE_MISSING;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.sequenceiq.cloudbreak.cloud.model.Image;
import com.sequenceiq.cloudbreak.core.flow2.CheckResult;
import com.sequenceiq.cloudbreak.message.CloudbreakMessagesService;
import com.sequenceiq.cloudbreak.service.cluster.InstanceMetadataUpdater;
import com.sequenceiq.cloudbreak.service.cluster.Package;
import com.sequenceiq.cloudbreak.service.image.StatedImage;
import com.sequenceiq.cloudbreak.view.InstanceMetadataView;

@Component
public class PackageVersionChecker {
    public static final String SALT_BOOTSTRAP = "salt-bootstrap";

    private static final String SALT = "salt";

    private static final Logger LOGGER = LoggerFactory.getLogger(PackageVersionChecker.class);

    @Inject
    private InstanceMetadataUpdater instanceMetadataUpdater;

    @Inject
    private CloudbreakMessagesService messagesService;

    public CheckResult compareImageAndInstancesMandatoryPackageVersion(StatedImage newImage, Collection<InstanceMetadataView> instanceMetaDataSet) {
        Map<String, String> packageVersions = newImage.getImage().getPackageVersions();
        List<String> packagesToCompare;
        packagesToCompare = newImage.getImage().isPrewarmed()
                ? instanceMetadataUpdater.getPackages().stream().map(Package::getName).collect(Collectors.toList())
                : instanceMetadataUpdater.getPackages().stream().filter(pkg -> !pkg.isPrewarmed()).map(Package::getName).collect(Collectors.toList());

        List<String> missingPackageVersion = new ArrayList<>();
        List<String> differentPackageVersion = new ArrayList<>();
        Map<String, String> instancePackageVersions;
        try {
            instancePackageVersions = instanceMetaDataSet.stream()
                    .findFirst().orElseThrow(() -> new NoSuchElementException("Package version is missing"))
                    .getImage().get(Image.class).getPackageVersions();
        } catch (IOException e) {
            LOGGER.debug("Could not get image", e);
            return CheckResult.failed("Could not get image");
        }

        for (String packageToCompare : packagesToCompare) {
            if (!SALT_BOOTSTRAP.equals(packageToCompare) && !SALT.equals(packageToCompare)) {
                String packageVersionInImage = packageVersions.get(packageToCompare);
                if (StringUtils.isBlank(packageVersionInImage)) {
                    LOGGER.debug("Missing package in image: " + packageToCompare);
                    missingPackageVersion.add(packageToCompare);
                } else if (!instanceMetadataUpdater.isPackagesVersionEqual(packageVersionInImage, instancePackageVersions.get(packageToCompare))) {
                    LOGGER.debug(String.format("Different package [%s] version on image [%s] and on instance [%s]",
                            packageToCompare, packageVersionInImage, instancePackageVersions.get(packageToCompare)));
                    differentPackageVersion.add(packageToCompare);
                }
            }
        }

        if (!missingPackageVersion.isEmpty() || !differentPackageVersion.isEmpty()) {
            String message = messagesService.getMessage(Msg.PACKAGES_ARE_DIFFERENT_IN_IMAGE.code(),
                    Lists.newArrayList(StringUtils.join(missingPackageVersion, ","), StringUtils.join(differentPackageVersion, ",")));
            return CheckResult.failed(message);
        }
        return CheckResult.ok();
    }

    public CheckResult checkInstancesHaveAllMandatoryPackageVersion(Collection<InstanceMetadataView> instanceMetaDataSet) {
        Map<String, List<String>> instancesWithMissingPackageVersions = instanceMetadataUpdater.collectInstancesWithMissingPackageVersions(instanceMetaDataSet);
        if (!instancesWithMissingPackageVersions.isEmpty()) {
            String message = messagesService.getMessage(CLUSTER_PACKAGE_VERSIONS_ON_INSTANCES_ARE_MISSING.getMessage(),
                    Collections.singletonList(instancesWithMissingPackageVersions.entrySet().stream()
                            .map(entry -> String.format("Instance ID: [%s] Packages without version: [%s]",
                                    entry.getKey(), StringUtils.join(entry.getValue(), ",")))
                            .collect(Collectors.joining(" * "))));
            return CheckResult.failed(message);
        }
        return CheckResult.ok();
    }

    public CheckResult checkInstancesHaveMultiplePackageVersions(Collection<InstanceMetadataView> instanceMetaDataSet) {
        List<String> packagesWithMultipleVersions = instanceMetadataUpdater.collectPackagesWithMultipleVersions(instanceMetaDataSet);
        if (!packagesWithMultipleVersions.isEmpty()) {
            String message = messagesService.getMessage(CLUSTER_PACKAGES_ON_INSTANCES_ARE_DIFFERENT.getMessage(),
                    Collections.singletonList(String.join(",", packagesWithMultipleVersions)));
            return CheckResult.failed(message);
        }
        return CheckResult.ok();
    }

    private enum Msg {
        PACKAGES_ARE_DIFFERENT_IN_IMAGE("stack.image.update.packages.different");

        private final String code;

        Msg(String msgCode) {
            code = msgCode;
        }

        public String code() {
            return code;
        }
    }
}
