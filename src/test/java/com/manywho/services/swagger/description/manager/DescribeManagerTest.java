package com.manywho.services.swagger.description.manager;

import com.google.common.io.Resources;
import com.manywho.sdk.api.describe.DescribeServiceActionResponse;
import com.manywho.sdk.api.describe.DescribeValue;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.services.swagger.ServiceConfiguration;
import com.manywho.services.swagger.description.SwaggerDefinitionService;
import com.manywho.services.swagger.factories.SwaggerParserFactory;
import io.swagger.parser.SwaggerParser;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Scanner;

public class DescribeManagerTest {

    @Test
    public void testDescribeActionsFromSwaggerDefinition() throws URISyntaxException {
        SwaggerDefinitionService swaggerDefinitionService = mock(SwaggerDefinitionService.class);
        SwaggerParserFactory swaggerFactory = mock(SwaggerParserFactory.class);
        String content = getFileContent("description/manager/swagger.json");
        when(swaggerFactory.createSwaggerParser(any())).thenReturn(new SwaggerParser().parse(content));

        DescribeManager describeManager = new DescribeManager(swaggerDefinitionService, swaggerFactory);
        List<DescribeServiceActionResponse> responseList = describeManager.getListActions(new ServiceConfiguration());
        assertEquals(1, responseList.size());

        assertEquals("POST Current Time", responseList.get(0).getDeveloperName());
        assertEquals("Current Time", responseList.get(0).getDeveloperSummary());
        assertEquals(0, responseList.get(0).getServiceActionOutcomes().size());
        assertEquals("post/current-time", responseList.get(0).getUriPart());

        assertEquals(1, responseList.get(0).getServiceInputs().size());
        DescribeValue firstInput = responseList.get(0).getServiceInputs().get(0);
        assertEquals("TimeOptions", responseList.get(0).getServiceInputs().get(0).getDeveloperName());
        assertEquals(null, responseList.get(0).getServiceInputs().get(0).getContentValue());
        assertEquals(false, responseList.get(0).getServiceInputs().get(0).isRequired());
        assertEquals("ContentObject", responseList.get(0).getServiceInputs().get(0).getContentType().toString());
        assertEquals("Object", responseList.get(0).getServiceInputs().get(0).getContentType().name());
        assertEquals(null, responseList.get(0).getServiceInputs().get(0).getTypeElementDeveloperName());
        assertEquals(0, responseList.get(0).getServiceInputs().get(0).getOrdinal());

        assertEquals(1, responseList.get(0).getServiceOutputs().size());
        DescribeValue firstOutput = responseList.get(0).getServiceOutputs().get(0);
        assertEquals("TimeObject", responseList.get(0).getServiceOutputs().get(0).getDeveloperName());
        assertEquals(null, responseList.get(0).getServiceOutputs().get(0).getContentValue());
        assertEquals("ContentObject", responseList.get(0).getServiceOutputs().get(0).getContentType().toString());
        assertEquals("Object", responseList.get(0).getServiceOutputs().get(0).getContentType().name());
        assertEquals(null, responseList.get(0).getServiceOutputs().get(0).getTypeElementDeveloperName());
        assertEquals(0, responseList.get(0).getServiceOutputs().get(0).getOrdinal());
    }

    @Test
    public void testDescribeTypesFromSwaggerDefinition() throws Exception {
        SwaggerDefinitionService swaggerDefinitionService = new SwaggerDefinitionService();
        SwaggerParserFactory swaggerFactory = mock(SwaggerParserFactory.class);
        ServiceConfiguration serviceConfiguration = mock(ServiceConfiguration.class);
        when(serviceConfiguration.getSwaggerUrl()).thenReturn("https://wwww.test.com");
        String content = getFileContent("description/manager/swagger.json");
        when(swaggerFactory.createSwaggerParser(any())).thenReturn(new SwaggerParser().parse(content));

        DescribeManager describeManager = new DescribeManager(swaggerDefinitionService, swaggerFactory);
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

    private String getFileContent(String fileResourcePath) {
        try {
            File file = new File(Resources.getResource(fileResourcePath).toURI());
            return (new Scanner(file)).useDelimiter("\\Z").next();
        } catch (Exception e) {
            throw new RuntimeException(String.format("File %s not found", fileResourcePath));
        }
    }
}
