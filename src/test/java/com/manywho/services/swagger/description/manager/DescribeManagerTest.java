package com.manywho.services.swagger.description.manager;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.manywho.sdk.api.describe.DescribeServiceActionResponse;
import com.manywho.sdk.api.describe.DescribeValue;
import com.manywho.services.swagger.ServiceConfiguration;
import com.manywho.services.swagger.description.SwaggerDefinitionService;
import com.manywho.services.swagger.factories.SwaggerParserFactory;
import io.swagger.models.Swagger;
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
    public void testAction() throws URISyntaxException {
        SwaggerDefinitionService swaggerDefinitionService = mock(SwaggerDefinitionService.class);
        SwaggerParserFactory swaggerFactory = mock(SwaggerParserFactory.class);
        String content = getFileContent("description/manager/swagger.json");
        when(swaggerFactory.createSwaggerParser(any())).thenReturn(new SwaggerParser().parse(content));

        DescribeManager describeManager = new DescribeManager(swaggerDefinitionService, swaggerFactory);
        List<DescribeServiceActionResponse> responseList =  describeManager.getListActions(new ServiceConfiguration());
        assertEquals(1, responseList.size());
        DescribeServiceActionResponse firstAction = responseList.get(0);

        assertEquals("POST Current Time", firstAction.getDeveloperName());
        assertEquals("Current Time", firstAction.getDeveloperSummary());
        assertEquals(0, firstAction.getServiceActionOutcomes().size());
        assertEquals("post/current-time", firstAction.getUriPart());

        List<DescribeValue> inputs = firstAction.getServiceInputs();
        assertEquals(1, inputs.size());
        DescribeValue firstInput = inputs.get(0);
        assertEquals("TimeOptions", firstInput.getDeveloperName() );
        assertEquals(null, firstInput.getContentValue());
        assertEquals(false, firstInput.isRequired());
        assertEquals("ContentObject", firstInput.getContentType().toString());
        assertEquals("Object", firstInput.getContentType().name());
        assertEquals(null, firstInput.getTypeElementDeveloperName());
        assertEquals(0, firstInput.getOrdinal());

        List<DescribeValue> outputs = firstAction.getServiceOutputs();
        assertEquals(1, outputs.size());
        DescribeValue firstOutput = outputs.get(0);
        assertEquals("TimeObject", firstOutput.getDeveloperName());
        assertEquals(null, firstOutput.getContentValue());
        assertEquals("ContentObject", firstOutput.getContentType().toString());
        assertEquals("Object", firstOutput.getContentType().name());
        assertEquals(null, firstOutput.getTypeElementDeveloperName());
        assertEquals(0, firstOutput.getOrdinal());
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
