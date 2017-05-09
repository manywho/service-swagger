package com.manywho.services.swagger.managers;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.api.describe.DescribeServiceActionResponse;
import com.manywho.sdk.api.describe.DescribeValue;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.services.swagger.ServiceConfiguration;
import com.manywho.services.swagger.exception.NotSupportedTypeException;
import com.manywho.services.swagger.factories.ManyWhoRelationMapperFactory;
import com.manywho.services.swagger.services.ManyWhoRelationMapperService;
import com.manywho.services.swagger.services.SwaggerDefinitionService;
import io.swagger.models.Model;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DescribeManager {
    private SwaggerDefinitionService swaggerDefinitionService;
    private ManyWhoRelationMapperFactory manyWhoRelationServiceFactory;

    @Inject
    public DescribeManager(SwaggerDefinitionService swaggerDefinitonService, ManyWhoRelationMapperFactory manyWhoRelationMapperFactory) {
        this.swaggerDefinitionService = swaggerDefinitonService;
        this.manyWhoRelationServiceFactory = manyWhoRelationMapperFactory;
    }

    public List<DescribeServiceActionResponse> getListActionsFromSwaggerDeffinition(ServiceConfiguration serviceConfiguration) {
        List<DescribeServiceActionResponse> customActions = Lists.newArrayList();
        Swagger swagger = new SwaggerParser().read(serviceConfiguration.getSwaggerUrl());
        for (Map.Entry<String, Path> path : swagger.getPaths().entrySet()) {
            String pathAction = path.getKey().startsWith("/")? path.getKey().substring(1): path.getKey();

            if (path.getValue().getGet() != null) {
                String developerName = String.format("GET %s", path.getValue().getGet().getSummary());
                customActions.add(createAction(pathAction, developerName, path.getValue().getGet().getSummary()));
            } else if (path.getValue().getPost() != null) {
                String developerName = String.format("POST %s", path.getValue().getGet().getSummary());
                customActions.add(createAction(pathAction, developerName, path.getValue().getPost().getSummary()));
            }
        }

        return customActions;
    }

    private DescribeServiceActionResponse createAction(String path, String developerName, String developerSummary) {
        List<DescribeValue> serviceInputs = Lists.newArrayList();
        List<DescribeValue> serviceOutputs = Lists.newArrayList();
        serviceInputs.add(new DescribeValue("Input 1", ContentType.String));
        serviceOutputs.add(new DescribeValue("Output 1", ContentType.String));

        return new DescribeServiceActionResponse(developerName, developerSummary, path, serviceInputs, serviceOutputs);
    }

    public List<TypeElement> getListTypeElementFromSwaggerDeffinition(ServiceConfiguration serviceConfiguration) throws Exception {
        ManyWhoRelationMapperService relationService = manyWhoRelationServiceFactory.createManyWhoRelationMapper(serviceConfiguration);
        List<TypeElement> listOfTypeElements = new ArrayList<>();

        if(Strings.isNullOrEmpty(serviceConfiguration.getSwaggerUrl())) {
            return listOfTypeElements;
        }

        Swagger swagger = new SwaggerParser().read(serviceConfiguration.getSwaggerUrl());
        Map<String,Model> definitions = swagger.getDefinitions();

        for(Map.Entry<String, Model> entry : definitions.entrySet()) {
            try {
                if (relationService.isThereManyWhoRelation(entry.getKey())) {
                    listOfTypeElements.add(this.swaggerDefinitionService.createManyWhoMetadataType(entry));
                }
            } catch (NotSupportedTypeException e) {
                // if the type is not supported I don't add the type to the service
            }
        }

        return listOfTypeElements;
    }

    public Map.Entry<String, Model> getEntryDefinition(ServiceConfiguration serviceConfiguration, String type) {
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
