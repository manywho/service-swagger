package com.manywho.services.swagger.database.managers;

import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.services.swagger.ServiceConfiguration;
import com.manywho.services.swagger.client.HttpClientSwagger;
import com.manywho.services.swagger.description.SwaggerDefinitionService;
import com.manywho.services.swagger.description.manager.DescribeManager;
import io.swagger.models.Model;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;

import javax.inject.Inject;
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
        HttpClientSwagger httpClientSwagger = new HttpClientSwagger(configuration);
        return swaggerDefinitionService.getManyWhoType(entry, httpClientSwagger.executeOperationObject(httpClient), externalIdName);
    }

    public void save(ServiceConfiguration configuration, MObject object, String verb, String url,
                     String externalIdName) {
        HttpClientSwagger httpClientSwagger = new HttpClientSwagger(configuration);
        Map.Entry<String, Model> entry = describeManager.getEntryDefinition(configuration, object.getDeveloperName());

        if ("GET".equalsIgnoreCase(verb)) {
            HttpGet httpClient = new HttpGet(url);
            swaggerDefinitionService.getManyWhoType(entry, httpClientSwagger.executeOperationObject(httpClient), externalIdName);

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

            httpClientSwagger.executeOperationObject(httpClient);

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

            httpClientSwagger.executeOperationObject(httpClient);

        } else {
            throw new RuntimeException("problem creating object");
        }
    }
}
