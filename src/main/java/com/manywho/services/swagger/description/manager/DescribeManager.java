package com.manywho.services.swagger.description.manager;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.api.describe.DescribeServiceActionResponse;
import com.manywho.sdk.api.describe.DescribeValue;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.services.swagger.ServiceConfiguration;
import com.manywho.services.swagger.exception.NotSupportedTypeException;
import com.manywho.services.swagger.factories.SwaggerParserFactory;
import com.manywho.services.swagger.description.SwaggerDefinitionService;
import com.manywho.services.swagger.utilities.TypeConverterUtil;
import io.swagger.models.*;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;

import javax.inject.Inject;
import java.util.*;

public class DescribeManager {
    private SwaggerDefinitionService swaggerDefinitionService;
    private SwaggerParserFactory swaggerFactory;

    @Inject
    public DescribeManager(SwaggerDefinitionService swaggerDefinitionService, SwaggerParserFactory swaggerFactory) {
        this.swaggerDefinitionService = swaggerDefinitionService;
        this.swaggerFactory = swaggerFactory;
    }

    public List<DescribeServiceActionResponse> getListActions(ServiceConfiguration serviceConfiguration) {
        List<DescribeServiceActionResponse> customActions = Lists.newArrayList();
        Swagger swagger = swaggerFactory.createSwaggerParser(serviceConfiguration);

        for (Map.Entry<String, Path> path : swagger.getPaths().entrySet()) {
            String pathAction = path.getKey().startsWith("/") ? path.getKey().substring(1) : path.getKey();

            String developerName = null;
            String verbPathAction = null;
            Operation operation = null;

            if (path.getValue().getGet() != null) {
                operation = path.getValue().getGet();
                developerName = String.format("GET %s", operation.getSummary());
                verbPathAction = "get/" + pathAction;
            } else if (path.getValue().getPost() != null) {
                operation = path.getValue().getPost();
                developerName = String.format("POST %s", operation.getSummary());
                verbPathAction = "post/" + pathAction;
            }

            //ignored not supported verbs

            if (operation != null) {
                try {
                    DescribeServiceActionResponse describeServiceActionResponse = new DescribeServiceActionResponse(
                            developerName,
                            operation.getSummary(),
                            verbPathAction,
                            getInputs(swagger, operation),
                            getOutputs(swagger, operation));

                    customActions.add(describeServiceActionResponse);
                } catch (NotSupportedTypeException ex) {
                    // ignored not supported actions
                }
            }
        }

        return customActions;
    }

    private List<DescribeValue> getOutputs(Swagger swagger, Operation operation) {
        RefProperty refProperty = (RefProperty) operation.getResponses().get("200").getSchema();
        Map<String, Model> definitions = swagger.getDefinitions();

        List<DescribeValue> serviceOutputs = Lists.newArrayList();
        serviceOutputs.add(new DescribeValue(refProperty.getSimpleRef(), ContentType.Object));


        Map.Entry<String, Model> entry = new AbstractMap.SimpleEntry<>(refProperty.getSimpleRef(), definitions.get(refProperty.getSimpleRef()));

        //validate properties
        for (Map.Entry<String, Property> propertyEntry : entry.getValue().getProperties().entrySet()) {
            TypeConverterUtil.convertFromSwaggerToManyWho(propertyEntry.getValue().getType(), propertyEntry.getValue().getFormat());
        }

        return serviceOutputs;
    }


    private List<DescribeValue> getInputs(Swagger swagger, Operation operation) {
        Map<String, Model> definitions = swagger.getDefinitions();
        BodyParameter bodyParam = (BodyParameter) operation.getParameters().get(0);
        RefModel refModel = (RefModel) bodyParam.getSchema();
        List<DescribeValue> describeValues = Lists.newArrayList();
        describeValues.add(new DescribeValue(refModel.getSimpleRef(), ContentType.Object));
        Map.Entry<String, Model> entry = new AbstractMap.SimpleEntry<>(refModel.getSimpleRef(), definitions.get(refModel.getSimpleRef()));

        //validate properties
        for (Map.Entry<String, Property> propertyEntry : entry.getValue().getProperties().entrySet()) {
            TypeConverterUtil.convertFromSwaggerToManyWho(propertyEntry.getValue().getType(), propertyEntry.getValue().getFormat());
        }

        return describeValues;
    }

    public List<TypeElement> getListTypeElement(ServiceConfiguration serviceConfiguration) {
        List<TypeElement> listOfTypeElements = new ArrayList<>();

        if (Strings.isNullOrEmpty(serviceConfiguration.getSwaggerUrl())) {
            return listOfTypeElements;
        }

        Swagger swagger = swaggerFactory.createSwaggerParser(serviceConfiguration);
        Map<String, Model> definitions = swagger.getDefinitions();

        for (Map.Entry<String, Model> entry : definitions.entrySet()) {
            try {
                listOfTypeElements.add(this.swaggerDefinitionService.createManyWhoMetadataType(entry));
            } catch (NotSupportedTypeException e) {
                // ignore not supported types
            }
        }

        return listOfTypeElements;
    }

    public Map.Entry<String, Model> getEntryDefinition(ServiceConfiguration serviceConfiguration, String type) {
        Swagger swagger = swaggerFactory.createSwaggerParser(serviceConfiguration);
        Map<String, Model> definitions = swagger.getDefinitions();

        for (Map.Entry<String, Model> entry : definitions.entrySet()) {
            try {
                if (entry.getKey().equalsIgnoreCase(type)) {
                    return entry;
                }
            } catch (NotSupportedTypeException e) {
                // if the type is not supported I don't add the type to the service
            }
        }

        throw new RuntimeException("entry " + type + "not found");
    }
}
