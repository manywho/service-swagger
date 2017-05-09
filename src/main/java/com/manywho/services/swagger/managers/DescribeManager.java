package com.manywho.services.swagger.managers;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.api.describe.DescribeServiceActionResponse;
import com.manywho.sdk.api.describe.DescribeValue;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.services.swagger.ServiceConfiguration;
import com.manywho.services.swagger.exception.NotSupportedTypeException;
import com.manywho.services.swagger.services.SwaggerDefinitionService;
import io.swagger.models.Model;
import io.swagger.models.Path;
import io.swagger.models.RefModel;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.properties.RefProperty;
import io.swagger.parser.SwaggerParser;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DescribeManager {
    private SwaggerDefinitionService swaggerDefinitionService;

    @Inject
    public DescribeManager(SwaggerDefinitionService swaggerDefinitionService) {
        this.swaggerDefinitionService = swaggerDefinitionService;
    }

    public List<DescribeServiceActionResponse> getListActions(ServiceConfiguration serviceConfiguration) {
        List<DescribeServiceActionResponse> customActions = Lists.newArrayList();
        Swagger swagger = new SwaggerParser().read(serviceConfiguration.getSwaggerUrl());
        for (Map.Entry<String, Path> path : swagger.getPaths().entrySet()) {
            String pathAction = path.getKey().startsWith("/")? path.getKey().substring(1): path.getKey();
            String developerName = "";
            String summary = "";
            BodyParameter bodyParam = null;
            String verbPathAction = pathAction;

            if (path.getValue().getGet() != null) {
                developerName = String.format("GET %s", path.getValue().getGet().getSummary());
                summary = path.getValue().getGet().getSummary();
                bodyParam = (BodyParameter) swagger.getPaths().get(path.getKey()).getGet().getParameters().get(0);
                verbPathAction = "get/" + pathAction;
            } else if (path.getValue().getPost() != null) {
                developerName = String.format("POST %s", path.getValue().getPost().getSummary());
                bodyParam = (BodyParameter) swagger.getPaths().get(path.getKey()).getPost().getParameters().get(0);
                summary = path.getValue().getPost().getSummary();
                verbPathAction = "post/" + pathAction;
            }

            RefModel refModel = (RefModel) bodyParam.getSchema();
            RefProperty refProperty = (RefProperty) swagger.getPaths().get(path.getKey()).getPost()
                    .getResponses().get("200").getSchema();

            List<DescribeValue> serviceInputs = Lists.newArrayList();
            List<DescribeValue> serviceOutputs = Lists.newArrayList();
            serviceInputs.add(new DescribeValue(refModel.getSimpleRef(), ContentType.Object));
            serviceOutputs.add(new DescribeValue(refProperty.getSimpleRef(), ContentType.Object));

            customActions.add( new DescribeServiceActionResponse(developerName, summary, verbPathAction, serviceInputs,
                    serviceOutputs));
        }

        return customActions;
    }

    public List<TypeElement> getListTypeElement(ServiceConfiguration serviceConfiguration) throws Exception {
        List<TypeElement> listOfTypeElements = new ArrayList<>();

        if(Strings.isNullOrEmpty(serviceConfiguration.getSwaggerUrl())) {
            return listOfTypeElements;
        }

        Swagger swagger = new SwaggerParser().read(serviceConfiguration.getSwaggerUrl());
        Map<String,Model> definitions = swagger.getDefinitions();

        for(Map.Entry<String, Model> entry : definitions.entrySet()) {
            listOfTypeElements.add(this.swaggerDefinitionService.createManyWhoMetadataType(entry));
        }

        return listOfTypeElements;
    }

    Map.Entry<String, Model> getEntryDefinition(ServiceConfiguration serviceConfiguration, String type) {
        Swagger swagger = new SwaggerParser().read(serviceConfiguration.getSwaggerUrl());
        Map<String,Model> definitions = swagger.getDefinitions();

        for(Map.Entry<String, Model> entry : definitions.entrySet()) {
            try {
                if(entry.getKey().equalsIgnoreCase(type)) {
                    return entry;
                }
            } catch (NotSupportedTypeException e) {
                // if the type is not supported I don't add the type to the service
            }
        }

        throw new RuntimeException("entry "+ type + "not found");
    }



}
