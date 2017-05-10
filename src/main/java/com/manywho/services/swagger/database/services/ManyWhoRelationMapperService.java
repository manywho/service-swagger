package com.manywho.services.swagger.database.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.Property;
import com.manywho.services.swagger.ServiceConfiguration;
import com.manywho.services.swagger.database.mapper.SwaggerMapper;
import com.manywho.services.swagger.database.mapper.Parameter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ManyWhoRelationMapperService {
    private List<SwaggerMapper> swaggerMappers;

    public ManyWhoRelationMapperService(ServiceConfiguration serviceConfiguration) {
        swaggerMappers = getSwaggerMapperList(serviceConfiguration.getServiceMapper());
    }

    public String getUrlLoad(String entryName, String id) {
        String url = getSwaggerMapper(entryName).getLoad().getUrl();

        for (Parameter parameter : getSwaggerMapper(entryName).getLoad().getParameters()) {
            if (Objects.equals(parameter.getType(), "query")) {
                url = url.replace(String.format("{%s}", parameter.getParamName()), id);
            }
        }

        return url;
    }

    public String getUrlToCreate(String entryName) {
        String url = getSwaggerMapper(entryName).getCreate().getUrl();
        return url;
    }

    public String getUrlCreateVerb(String entryName) {
        return getSwaggerMapper(entryName).getCreate().getVerb();
    }

    public String getUrlToUpdate(String entryName, MObject object) {
        String url = getSwaggerMapper(entryName).getUpdate().getUrl();

        for (Parameter parameter : getSwaggerMapper(entryName).getUpdate().getParameters()) {
            if (Objects.equals(parameter.getType(), "query") && Objects.equals(parameter.getPassedBy(), "object")) {
                for (Property p : object.getProperties()) {
                    if (Objects.equals(p.getDeveloperName(), parameter.getName())) {
                        url = url.replace(String.format("{%s}", parameter.getParamName()), p.getContentValue());
                    }
                }
            }
        }

        return url;
    }

    public Boolean isThereManyWhoRelation(String entryName) {

        for (SwaggerMapper swaggerMapper : swaggerMappers) {
            if (entryName.equalsIgnoreCase(swaggerMapper.getManyWhoType())) {
                return true;
            }
        }

        return false;
    }

    public String getUrlUpdateVerb(String entryName) {
        return getSwaggerMapper(entryName).getUpdate().getVerb();
    }

    public String getUrlLoadVerb(String entryName) {
        return getSwaggerMapper(entryName).getLoad().getVerb();
    }

    public String getExternalIdName(String entryName) {
        return getSwaggerMapper(entryName).getExternalId();
    }

    private SwaggerMapper getSwaggerMapper(String entryName) {

        for (SwaggerMapper swaggerMapper : swaggerMappers) {
            if (StringUtils.equalsIgnoreCase(swaggerMapper.getManyWhoType(), entryName)) {
                return swaggerMapper;
            }
        }

        throw new RuntimeException("ManyWho mapper for " + entryName + " not found");
    }

    /**
     * change for a real api call, ore a configuration field
     *
     * @return
     * @throws IOException
     */
    private List<SwaggerMapper> getSwaggerMapperList(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();

        if (Strings.isNullOrEmpty(jsonString)) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(jsonString, objectMapper.getTypeFactory().
                    constructCollectionType(List.class, SwaggerMapper.class));
        } catch (IOException e) {
            throw new RuntimeException("error reading the ManyWho mapper");
        }
    }
}
