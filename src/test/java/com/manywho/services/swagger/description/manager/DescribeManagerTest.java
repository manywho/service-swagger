package com.manywho.services.swagger.description.manager;

import com.google.common.io.Resources;
import com.manywho.sdk.api.describe.DescribeServiceActionResponse;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.services.swagger.ServiceConfiguration;
import com.manywho.services.swagger.description.SwaggerDefinitionService;
import com.manywho.services.swagger.factories.SwaggerParserFactory;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;

import static junit.framework.TestCase.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class DescribeManagerTest {
    @Test
    public void testDescribeActionsFromSwaggerDefinition() throws URISyntaxException {
        DescribeManager describeManager = getDescribeManager("description/manager/swagger.json");

        List<DescribeServiceActionResponse> responseList = describeManager.getListActions(new ServiceConfiguration());
        assertEquals(1, responseList.size());

        assertEquals("POST Current Time", responseList.get(0).getDeveloperName());
        assertEquals("Current Time", responseList.get(0).getDeveloperSummary());
        assertEquals(0, responseList.get(0).getServiceActionOutcomes().size());
        assertEquals("post/current-time", responseList.get(0).getUriPart());

        assertEquals(1, responseList.get(0).getServiceInputs().size());
        assertEquals("TimeOptions", responseList.get(0).getServiceInputs().get(0).getDeveloperName());
        assertEquals(null, responseList.get(0).getServiceInputs().get(0).getContentValue());
        assertEquals(false, responseList.get(0).getServiceInputs().get(0).isRequired());
        assertEquals("ContentObject", responseList.get(0).getServiceInputs().get(0).getContentType().toString());
        assertEquals("Object", responseList.get(0).getServiceInputs().get(0).getContentType().name());
        assertEquals(null, responseList.get(0).getServiceInputs().get(0).getTypeElementDeveloperName());
        assertEquals(0, responseList.get(0).getServiceInputs().get(0).getOrdinal());

        assertEquals(1, responseList.get(0).getServiceOutputs().size());
        assertEquals("TimeObject", responseList.get(0).getServiceOutputs().get(0).getDeveloperName());
        assertEquals(null, responseList.get(0).getServiceOutputs().get(0).getContentValue());
        assertEquals("ContentObject", responseList.get(0).getServiceOutputs().get(0).getContentType().toString());
        assertEquals("Object", responseList.get(0).getServiceOutputs().get(0).getContentType().name());
        assertEquals(null, responseList.get(0).getServiceOutputs().get(0).getTypeElementDeveloperName());
        assertEquals(0, responseList.get(0).getServiceOutputs().get(0).getOrdinal());
    }

    @Test
    public void testDescribeTypesFromSwaggerDefinition() throws Exception {
        ServiceConfiguration serviceConfiguration = mock(ServiceConfiguration.class);
        when(serviceConfiguration.getSwaggerUrl()).thenReturn("http://not-empty.com");
        DescribeManager describeManager = getDescribeManager("description/manager/swagger.json");
        List<TypeElement> responseList = describeManager.getListTypeElement(serviceConfiguration);

        assertEquals(2, responseList.size());
        assertEquals("TimeObject", responseList.get(0).getDeveloperName());
        assertEquals("TYPE", responseList.get(0).getElementType());
        assertEquals(null, responseList.get(0).getId());
        assertEquals(null, responseList.get(0).getServiceElementId());
        assertEquals(null, responseList.get(0).getDeveloperSummary());

        assertEquals(1, responseList.get(0).getProperties().size());
        assertEquals("Current Time", responseList.get(0).getProperties().get(0).getDeveloperName());
        assertEquals("ContentString", responseList.get(0).getProperties().get(0).getContentType().toString());

        assertEquals(1, responseList.get(0).getProperties().size());
        assertEquals(null, responseList.get(0).getProperties().get(0).getId());
        assertEquals("Current Time", responseList.get(0).getProperties().get(0).getDeveloperName());
        assertEquals(null, responseList.get(0).getProperties().get(0).getContentFormat());
        assertEquals("ContentString", responseList.get(0).getProperties().get(0).getContentType().toString());
        assertEquals(null, responseList.get(0).getProperties().get(0).getTypeElementId());
        assertEquals(null, responseList.get(0).getProperties().get(0).getTypeElementDeveloperName());

        assertEquals(1, responseList.get(0).getBindings().size());
        assertEquals(null, responseList.get(0).getBindings().get(0).getId());
        assertEquals("The binding for TimeObject", responseList.get(0).getBindings().get(0).getDeveloperSummary());
        assertEquals("TimeObject", responseList.get(0).getBindings().get(0).getDatabaseTableName());
        assertEquals(null, responseList.get(0).getBindings().get(0).getServiceElementId());

        assertEquals(1, responseList.get(0).getBindings().get(0).getPropertyBindings().size());
        assertEquals("Current Time", responseList.get(0).getBindings().get(0).getPropertyBindings().get(0).getDatabaseFieldName());
        assertEquals("Current Time", responseList.get(0).getBindings().get(0).getPropertyBindings().get(0).getTypeElementPropertyDeveloperName());
        assertEquals("ContentString", responseList.get(0).getBindings().get(0).getPropertyBindings().get(0).getDatabaseContentType());

        assertEquals("TYPE", responseList.get(0).getElementType());
        assertEquals("TimeObject", responseList.get(0).getDeveloperName());
        assertEquals(null, responseList.get(0).getDeveloperSummary());
    }

    @Test
    public void testDescribeIgnoreActionWithNestedType() throws Exception {
        // the type is ignored if there are nested fields in it
        DescribeManager describeManager = getDescribeManager("description/manager/swagger-with-nested-input.json");
        ServiceConfiguration serviceConfiguration = mock(ServiceConfiguration.class);
        when(serviceConfiguration.getSwaggerUrl()).thenReturn("http://not-empty.com");
        List<DescribeServiceActionResponse> responseActionList = describeManager.getListActions(serviceConfiguration);
        assertEquals(1, responseActionList.size());

        Optional<DescribeServiceActionResponse> actionWithNestedType = responseActionList.stream()
                .filter(action -> action.getUriPart().equals("/current-time-nested")).findFirst();

        assertFalse(actionWithNestedType.isPresent());
    }

    @Test
    public void testDescribeIgnoreTypesWithNestedType() throws Exception {
        // the type is ignored if there are nested fields in it
        DescribeManager describeManager = getDescribeManager("description/manager/swagger-with-nested-input.json");
        ServiceConfiguration serviceConfiguration = mock(ServiceConfiguration.class);
        when(serviceConfiguration.getSwaggerUrl()).thenReturn("http://not-empty.com");

        List<TypeElement> responseTypeList = describeManager.getListTypeElement(serviceConfiguration);
        //it should ignore the type Current Time Nested
        assertEquals(2, responseTypeList.size());

        Optional<TypeElement> typeElementNested = responseTypeList.stream().filter(type -> type.getDeveloperName()
                .equals("TimeOptions nested")).findFirst();

        assertFalse(typeElementNested.isPresent());
    }

    private DescribeManager getDescribeManager(String swaggerJson) {
        SwaggerDefinitionService swaggerDefinitionService = new SwaggerDefinitionService();
        SwaggerParserFactory swaggerFactory = mock(SwaggerParserFactory.class);
        Swagger swagger = new SwaggerParser().parse(getFileContent(swaggerJson));
        when(swaggerFactory.createSwaggerParser(any())).thenReturn(swagger);

        return new DescribeManager(swaggerDefinitionService, swaggerFactory);
    }

    private String getFileContent(String fileResourcePath) {
        try {
            File file = new File(Resources.getResource(fileResourcePath).toURI());
            return (new Scanner(file)).useDelimiter("\\Z").next();
        } catch (Exception e) {
            throw new RuntimeException(String.format("File %s not found", fileResourcePath));
        }
    }
}
