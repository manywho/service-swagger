package com.manywho.services.swagger.factories;

import com.google.common.collect.Lists;
import com.manywho.services.swagger.ServiceConfiguration;
import io.swagger.models.Swagger;
import io.swagger.models.auth.AuthorizationValue;
import io.swagger.parser.SwaggerParser;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SwaggerFactory {
    public Swagger createSwaggerParser(ServiceConfiguration configuration) {

        if (!StringUtils.isEmpty(configuration.getBasicAuthPassword()) || !StringUtils.isEmpty(configuration.getBasicAuthUserName())) {
            String authString = configuration.getBasicAuthUserName() + ":" + configuration.getBasicAuthPassword();
            byte[] authToken = Base64.encodeBase64(authString.getBytes());
            String code = "Basic " + new String(authToken);
            AuthorizationValue apiKey = new AuthorizationValue("Authorization", code, "header");
            List<AuthorizationValue> values = Lists.newArrayList();
            values.add(apiKey);

            return new SwaggerParser().read(configuration.getSwaggerUrl(), values, false);
        } else {
            return new SwaggerParser().read(configuration.getSwaggerUrl());
        }
    }
}
