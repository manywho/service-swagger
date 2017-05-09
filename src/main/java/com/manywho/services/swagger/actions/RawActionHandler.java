package com.manywho.services.swagger.actions;

import com.google.common.collect.Lists;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.api.InvokeType;
import com.manywho.sdk.api.run.EngineValue;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.api.run.elements.config.ServiceResponse;
import com.manywho.sdk.services.actions.ActionHandler;
import com.manywho.services.swagger.ServiceConfiguration;

import java.util.List;

public class RawActionHandler implements ActionHandler<ServiceConfiguration> {
    @Override
    public boolean canHandleAction(String uriPath, ServiceConfiguration configuration, ServiceRequest serviceRequest) {
        return uriPath.equals("custom-action");
    }

    @Override
    public ServiceResponse handleRaw(String actionPath, ServiceConfiguration configuration, ServiceRequest serviceRequest) {
        List<EngineValue> serviceInputs = serviceRequest.getInputs();

        List<EngineValue> outputs = Lists.newArrayList();
        EngineValue engineValue1 = new EngineValue("Output 1", ContentType.String, "Value1");
        outputs.add(engineValue1);

        return new ServiceResponse(InvokeType.Forward, outputs, serviceRequest.getToken());
    }
}
