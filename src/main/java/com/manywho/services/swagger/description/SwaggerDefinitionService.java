package com.manywho.services.swagger.description;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.api.draw.elements.type.TypeElementBinding;
import com.manywho.sdk.api.draw.elements.type.TypeElementProperty;
import com.manywho.sdk.api.draw.elements.type.TypeElementPropertyBinding;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.services.swagger.utilities.TypeConverterUtil;
import io.swagger.models.Model;
import io.swagger.models.properties.Property;

import java.util.*;

public class SwaggerDefinitionService {
    public TypeElement createManyWhoMetadataType(Map.Entry<String, Model> entry) {
        List<TypeElementProperty> properties = Lists.newArrayList();
        List<TypeElementPropertyBinding> propertyBindings = Lists.newArrayList();

        for (Map.Entry<String, Property> property : entry.getValue().getProperties().entrySet()) {

            ContentType contentType = TypeConverterUtil.convertFromSwaggerToManyWho(property.getValue().getType(), property.getValue().getFormat());
            properties.add(new TypeElementProperty(property.getKey(), contentType));
            propertyBindings.add(new TypeElementPropertyBinding(property.getKey(), property.getKey(), contentType.toString()));
        }

        List<TypeElementBinding> bindings = Lists.newArrayList();
        bindings.add(new TypeElementBinding(entry.getKey(), "The binding for " + entry.getKey(), entry.getKey(), propertyBindings));

        return new TypeElement(entry.getKey(), properties, bindings);
    }

    public MObject getManyWhoType(Map.Entry<String, Model> entry, Object object, String externalIdName) {

        MObject mObject = new MObject();
        mObject.setDeveloperName(entry.getKey());
        mObject.setExternalId("");
        List<com.manywho.sdk.api.run.elements.type.Property> properties = new ArrayList<>();

        for (Map.Entry<String, Property> propertyEntry : entry.getValue().getProperties().entrySet()) {
            com.manywho.sdk.api.run.elements.type.Property property = new com.manywho.sdk.api.run.elements.type.Property();
            property.setDeveloperName(propertyEntry.getKey());
            property.setContentType(TypeConverterUtil.convertFromSwaggerToManyWho(propertyEntry.getValue().getType(), propertyEntry.getValue().getFormat()));
            Object hashMap = ((LinkedHashMap) object).get(propertyEntry.getKey());

            if (hashMap != null) {
                property.setContentValue(hashMap.toString());
            }

            if (Objects.equals(propertyEntry.getKey(), externalIdName)) {
                mObject.setExternalId(hashMap.toString());
            }

            property.setObjectData(null);
            properties.add(property);
        }

        mObject.setProperties(properties);
        mObject.setOrder(0);
        mObject.setInternalId(null);
        mObject.setTypeElementId(null);
        mObject.setSelected(false);

        return mObject;
    }

    public String getEntryString(MObject mObject, Map.Entry<String, Model> entry) {
        HashMap<String, Object> properites = new HashMap<>();

        for (Map.Entry<String, Property> swaggerProperty : entry.getValue().getProperties().entrySet()) {
            for (com.manywho.sdk.api.run.elements.type.Property manywhoProperty : mObject.getProperties()) {
                if (Objects.equals(manywhoProperty.getDeveloperName(), swaggerProperty.getKey())) {
                    properites.put(swaggerProperty.getKey(), TypeConverterUtil.getPropertyValue(swaggerProperty.getValue(), manywhoProperty));
                }
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        String entity = null;
        try {
            entity = mapper.writeValueAsString(properites);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return entity;
    }
}