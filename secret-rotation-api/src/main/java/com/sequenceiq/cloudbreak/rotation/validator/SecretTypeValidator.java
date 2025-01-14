package com.sequenceiq.cloudbreak.rotation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.common.collect.Sets;
import com.sequenceiq.cloudbreak.rotation.SecretType;
import com.sequenceiq.cloudbreak.rotation.SecretTypeConverter;
import com.sequenceiq.cloudbreak.rotation.annotation.ValidSecretType;
import com.sequenceiq.common.api.util.ValidatorUtil;

public class SecretTypeValidator implements ConstraintValidator<ValidSecretType, String> {

    private Class<? extends SecretType>[] allowedTypes;

    private boolean internalAllowed;

    @Override
    public void initialize(ValidSecretType constraintAnnotation) {
        allowedTypes = constraintAnnotation.allowedTypes();
        internalAllowed = constraintAnnotation.internalAllowed();
    }

    @Override
    public boolean isValid(String secret, ConstraintValidatorContext context) {
        try {
            SecretType secretType = SecretTypeConverter.mapSecretType(secret, Sets.newHashSet(allowedTypes));
            if (!internalAllowed && secretType.internal()) {
                return false;
            }
        } catch (Exception e) {
            ValidatorUtil.addConstraintViolation(context, e.getMessage());
            return false;
        }
        return true;
    }
}
