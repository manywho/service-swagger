package com.manywho.services.swagger.types;

import com.manywho.sdk.api.describe.DescribeServiceRequest;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.services.types.TypeProvider;
import com.manywho.services.swagger.ServiceConfiguration;
import com.manywho.services.swagger.managers.DescribeManager;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class RawTypeProvider implements TypeProvider<ServiceConfiguration> {

    private DescribeManager describeManager;

    @Inject
    public RawTypeProvider(DescribeManager describeManager) {
        this.describeManager = describeManager;
    }

    @Override
    public boolean doesTypeExist(ServiceConfiguration configuration, String s) {
        return true;
    }

    @Override
    public List<TypeElement> describeTypes(ServiceConfiguration configuration, DescribeServiceRequest describeServiceRequest) {
        try {
            if (describeServiceRequest.getConfigurationValues() != null) {
                return describeManager.getListTypeElement(configuration);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new ArrayList<>();
    }
}
