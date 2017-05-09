package com.manywho.services.swagger.managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.services.swagger.ServiceConfiguration;
import com.manywho.services.swagger.services.SwaggerDefinitionService;
import io.swagger.models.Model;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class DataManager {
    private DescribeManager describeManager;

    private SwaggerDefinitionService swaggerDefinitionService;

    @Inject
    public DataManager(DescribeManager describeManager, SwaggerDefinitionService swaggerDefinitionService) {

        this.describeManager = describeManager;
        this.swaggerDefinitionService = swaggerDefinitionService;
    }

    public MObject find(ServiceConfiguration configuration, String entryName, String verb, String url,
                        String externalIdName) {

        Map.Entry<String, Model> entry = describeManager.getEntryDefinition(configuration, entryName);
        HttpRequestBase httpClient = null;

        if ("GET".equalsIgnoreCase(verb)) {
            httpClient = new HttpGet(url);
        } else if ("POST".equalsIgnoreCase(url)) {
            httpClient = new HttpPost(verb);
        }

        return swaggerDefinitionService.getManyWhoType(entry, executeOperation(httpClient), externalIdName);
    }

    public void save(ServiceConfiguration configuration, MObject object, String verb, String url,
                     String externalIdName) {

        Map.Entry<String, Model> entry = describeManager.getEntryDefinition(configuration, object.getDeveloperName());

        if ("GET".equalsIgnoreCase(verb)) {
            HttpGet httpClient = new HttpGet(url);
            swaggerDefinitionService.getManyWhoType(entry, executeOperation(httpClient),externalIdName);

        } else if ("POST".equalsIgnoreCase(verb)) {
            HttpPost httpClient = new HttpPost(url);
            StringEntity input = null;

            try {
                input = new StringEntity(swaggerDefinitionService.getEntryString(object, entry));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            input.setContentType("application/json");
            httpClient.setEntity(input);

            executeOperation(httpClient);

        } else if ("PUT".equalsIgnoreCase(verb)) {
            HttpPut httpClient = new HttpPut(url);
            StringEntity input = null;

            try {
                input = new StringEntity(swaggerDefinitionService.getEntryString(object, entry));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            input.setContentType("application/json");
            httpClient.setEntity(input);

            executeOperation(httpClient);

        }else {
            throw new RuntimeException("problem creating object");
        }
    }

    public Object executeOperation(HttpRequestBase httpClient) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try{
            // Create a custom response handler
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };
            String responseBody = httpclient.execute(httpClient, responseHandler);
            ObjectMapper mapper = new ObjectMapper();
            if (StringUtils.isEmpty(responseBody)) {
                return new Object();
            }
            return mapper.readValue(responseBody, Object.class);

        } catch (Exception e) {
            throw  new RuntimeException(e.getMessage());
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
