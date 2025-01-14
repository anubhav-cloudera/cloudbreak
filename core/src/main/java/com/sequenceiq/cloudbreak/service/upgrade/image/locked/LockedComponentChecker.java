package com.sequenceiq.cloudbreak.service.upgrade.image.locked;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.cloud.model.catalog.Image;

@Component
public class LockedComponentChecker {

    @Inject
    private ParcelMatcher parcelMatcher;

    @Inject
    private StackVersionMatcher stackVersionMatcher;

    @Inject
    private CmVersionMatcher cmVersionMatcher;

    public boolean isUpgradePermitted(Image candidateImage, Map<String, String> activatedParcels, String cmBuildNumber) {
        boolean parcelsMatch = parcelMatcher.isMatchingNonCdhParcels(candidateImage, activatedParcels);
        boolean stackVersionMatches = stackVersionMatcher.isMatchingStackVersion(candidateImage, activatedParcels);
        boolean cmVersionMatches = cmVersionMatcher.isCmVersionMatching(cmBuildNumber, candidateImage);

        return parcelsMatch && stackVersionMatches && cmVersionMatches;
    }
}
