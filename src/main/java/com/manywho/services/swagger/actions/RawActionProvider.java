package com.manywho.services.swagger.actions;

import com.google.common.collect.Lists;
import com.manywho.sdk.api.describe.DescribeServiceActionResponse;
import com.manywho.sdk.api.describe.DescribeServiceRequest;
import com.manywho.sdk.services.actions.ActionProvider;
import com.manywho.services.swagger.ServiceConfiguration;
import com.manywho.services.swagger.managers.DescribeManager;

import javax.inject.Inject;
import java.util.List;

public class RawActionProvider implements ActionProvider<ServiceConfiguration> {

    private DescribeManager describeManager;

    @Inject
    public RawActionProvider(DescribeManager describeManager) {
        this.describeManager = describeManager;
    }

    @Override
    public List<DescribeServiceActionResponse> describeActions(ServiceConfiguration configuration, DescribeServiceRequest request) {
        if (configuration.getSwaggerUrl() != null) {
            return describeManager.getListActions(configuration);
        } else {
            return Lists.newArrayList();
        }
    }
}
