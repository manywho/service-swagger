package com.manywho.services.swagger;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.configuration.Configuration;

public class ServiceConfiguration implements Configuration {
    @Configuration.Setting(name="Swagger Description Url", contentType= ContentType.String)
    private String swaggerUrl;

    @Configuration.Setting(name="Swagger ManyWho Mapper", contentType= ContentType.String)
    private String serviceMapper;

    @Configuration.Setting(name="Basic Auth UserName", contentType = ContentType.String)
    private String basicAuthUserName;

    @Configuration.Setting(name="Basic Auth Password", contentType = ContentType.Password)
    private String basicAuthPassword;

    public String getSwaggerUrl() {
        return swaggerUrl;
    }

    public String getServiceMapper() {
        return serviceMapper;
    }

    public String getBasicAuthUserName() {
        return basicAuthUserName;
    }

    public String getBasicAuthPassword() {
        return basicAuthPassword;
    }
}
