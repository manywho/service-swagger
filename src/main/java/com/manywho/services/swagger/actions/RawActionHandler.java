package com.manywho.services.swagger.actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.manywho.sdk.api.InvokeType;
import com.manywho.sdk.api.run.EngineValue;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.api.run.elements.config.ServiceResponse;
import com.manywho.sdk.services.actions.ActionHandler;
import com.manywho.services.swagger.ServiceConfiguration;
import com.manywho.services.swagger.client.HttpClientSwagger;
import com.manywho.services.swagger.factories.SwaggerParserFactory;
import com.manywho.services.swagger.database.services.MapperService;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.properties.RefProperty;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RawActionHandler implements ActionHandler<ServiceConfiguration> {
    private MapperService mapperService;
    private SwaggerParserFactory swaggerFactory;

    @Inject
    public RawActionHandler(MapperService mapperService, SwaggerParserFactory swaggerFactory) {
        this.mapperService = mapperService;
        this.swaggerFactory = swaggerFactory;
    }

    @Override
    public boolean canHandleAction(String uriPath, ServiceConfiguration configuration, ServiceRequest serviceRequest) {
        Swagger swagger = swaggerFactory.createSwaggerParser(configuration);
        try {
            getPath(swagger, uriPath);

            return true;
        } catch (Exception ex) {

            return false;
        }
    }

    @Override
    public ServiceResponse handleRaw(String actionPath, ServiceConfiguration configuration, ServiceRequest serviceRequest) {
        Swagger swagger = swaggerFactory.createSwaggerParser(configuration);
        Path path = getPath(swagger, actionPath);
        String bodyRequest;
        StringEntity entity;
        HttpClientSwagger httpClientSwagger = new HttpClientSwagger(configuration);
        try {
            bodyRequest = mapperService.requestBody(serviceRequest.getInputs());
            entity = new StringEntity(bodyRequest);

        } catch (JsonProcessingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        HashMap<String, Object> object;

        String uri = getBaseUrlForActions(swagger, configuration) + getPathWithoutVerb(actionPath);

        String responseObjectName;

        if (getVerb(actionPath).equalsIgnoreCase("post")) {
            HttpPost httppost = new HttpPost(uri);

            entity.setContentType("application/json");
            httppost.setEntity(entity);
            object = httpClientSwagger.executeOperationHashMap(httppost);
            responseObjectName = ((RefProperty) path.getPost().getResponses().get("200").getSchema()).getSimpleRef();

        } else if (getVerb(actionPath).equalsIgnoreCase("get")) {
            HttpGet httpGet = new HttpGet(uri);
            entity.setContentType("application/json");
            object = httpClientSwagger.executeOperationHashMap(httpGet);
            responseObjectName = ((RefProperty) path.getGet().getResponses().get("200").getSchema()).getSimpleRef();
        } else {
            // todo it should be ignore
            throw new RuntimeException("method no supported");
        }

        List<EngineValue> outputs = mapperService.objectToEngineValues(object, responseObjectName);

        return new ServiceResponse(InvokeType.Forward, outputs, serviceRequest.getToken());
    }

    private String getBaseUrlForActions(Swagger swagger, ServiceConfiguration configuration) {
        //it force https if the option is selected in other case if there is only one choice select that choice.
        String scheme = "http";

        if (configuration.getForceHttps()) {
            scheme = "https";
        } else if (swagger.getSchemes().size() == 1) {
            scheme = swagger.getSchemes().get(0).toValue();
        }

        return scheme + "://" + swagger.getHost() + swagger.getBasePath();
    }

    private Path getPath(Swagger swagger, String uri) {
        String verb = getVerb(uri);
        String pathNoVerb = getPathWithoutVerb(uri);

        for (Map.Entry<String, Path> path : swagger.getPaths().entrySet()) {
            if (pathNoVerb.equals(path.getKey())) {
                if ((verb.equalsIgnoreCase("post") && path.getValue().getPost() != null)
                        || verb.equalsIgnoreCase("get") && path.getValue().getGet() != null) {

                    return path.getValue();
                }
            }
        }

        throw new RuntimeException(String.format("Action %s not found", uri));
    }

    private String getVerb(String uri) {
        if (uri.substring(0, 3).equalsIgnoreCase("get")) {

            return "get";
        } else if (uri.substring(0, 4).equalsIgnoreCase("post")) {

            return "post";
        } else {
            throw new RuntimeException(String.format("Verb in uri {%s} not supported", uri));
        }
    }

    private String getPathWithoutVerb(String uri) {
        String verb = getVerb(uri);
        if (verb.equalsIgnoreCase("post")) {
            return uri.substring(4);
        } else if (verb.equals("get")) {
            return uri.substring(3);
        }

        throw new RuntimeException(String.format("Verb in uri {%s} not supported", uri));
    }
}
