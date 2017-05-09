package com.manywho.services.swagger.actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manywho.sdk.api.InvokeType;
import com.manywho.sdk.api.run.EngineValue;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.api.run.elements.config.ServiceResponse;
import com.manywho.sdk.services.actions.ActionHandler;
import com.manywho.services.swagger.ServiceConfiguration;
import com.manywho.services.swagger.services.MapperService;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.properties.RefProperty;
import io.swagger.parser.SwaggerParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RawActionHandler implements ActionHandler<ServiceConfiguration> {
    private MapperService mapperService;

    @Inject
    public RawActionHandler(MapperService mapperService) {
        this.mapperService = mapperService;
    }

    @Override
    public boolean canHandleAction(String uriPath, ServiceConfiguration configuration, ServiceRequest serviceRequest) {
        Swagger swagger = new SwaggerParser().read(configuration.getSwaggerUrl());

        try {
            getPath(swagger, uriPath);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public ServiceResponse handleRaw(String actionPath, ServiceConfiguration configuration, ServiceRequest serviceRequest) {
        Swagger swagger = new SwaggerParser().read(configuration.getSwaggerUrl());
        Path path = getPath(swagger, actionPath);
        String bodyRequest;
        StringEntity entity;

        try {
            bodyRequest = mapperService.requestBody(serviceRequest.getInputs());
            entity = new StringEntity(bodyRequest);

        } catch (JsonProcessingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        HashMap<String, Object> object = null;

        String uri = swagger.getSchemes().get(0).toValue() + "://" + swagger.getHost()
                + swagger.getBasePath() + getPathWithoutVerb(actionPath);

        String responseObjectName;

        if (getVerb(actionPath).equals("post")) {
            HttpPost httppost = new HttpPost(uri);
            entity.setContentType("application/json");
            httppost.setEntity(entity);
            object = executeOperation(httppost);
            responseObjectName = ((RefProperty) path.getPost().getResponses().get("200").getSchema()).getSimpleRef();

        } else if (getVerb(actionPath).equals("get")) {
            HttpGet httpGet = new HttpGet(uri);
            entity.setContentType("application/json");
            object = executeOperation(httpGet);
            responseObjectName = ((RefProperty) path.getGet().getResponses().get("200").getSchema()).getSimpleRef();
        } else {
            // todo it should be ignore
            throw new RuntimeException("method no supported");
        }

        List<EngineValue> outputs = mapperService.objectToEngineValues(object, responseObjectName);

        return new ServiceResponse(InvokeType.Forward, outputs, serviceRequest.getToken());
    }

    private Path getPath(Swagger swagger, String uri) {
        String verb = getVerb(uri);
        String pathNoVerb = getPathWithoutVerb(uri);

        for (Map.Entry<String, Path> path : swagger.getPaths().entrySet()) {
            if (pathNoVerb.equals(path.getKey())) {
                if ((verb.equals("post") && path.getValue().getPost() != null)
                        || verb.equals("get") && path.getValue().getGet() != null) {

                    return path.getValue();
                }
            }
        }

        throw new RuntimeException(String.format("Action %s not found", uri));
    }

    private String getVerb(String uri) {
        if (uri.substring(0, 3).equals("get")) {
            return "get";
        } else if (uri.substring(0, 4).equals("post")) {
            return "post";
        } else {
            throw new RuntimeException(String.format("Verb in uri {%s} not supported", uri));
        }
    }

    private String getPathWithoutVerb(String uri) {
        String verb = getVerb(uri);
        if (verb.equals("post")) {
            return uri.substring(4);
        } else if (verb.equals("get")) {
            return uri.substring(3);
        }

        throw new RuntimeException(String.format("Verb in uri {%s} not supported", uri));
    }

    private HashMap<String, Object> executeOperation(HttpRequestBase httpClient) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
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
                return new HashMap<>();
            }
            return mapper.readValue(responseBody, HashMap.class);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
