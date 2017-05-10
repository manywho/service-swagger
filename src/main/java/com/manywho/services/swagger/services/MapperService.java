package com.manywho.services.swagger.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.api.run.EngineValue;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.Property;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MapperService {
    public String requestBody(List<EngineValue> engineValues) throws JsonProcessingException {
        EngineValue engineValue = engineValues.get(0);
        HashMap<String, Object> objectHashMap = new HashMap<>();

        for (MObject engineValue1 : engineValue.getObjectData()) {
            for (Property p : engineValue1.getProperties()) {
                objectHashMap.put(p.getDeveloperName(), p.getContentValue());
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(objectHashMap);
    }

    public List<EngineValue> objectToEngineValues(HashMap<String, Object> object, String name) {
        List<EngineValue> response = Lists.newArrayList();
        List<MObject> objectData = Lists.newArrayList();
        List<Property> properties = Lists.newArrayList();
        for (Map.Entry<String, Object> entryProp : object.entrySet()) {
            properties.add(new Property(entryProp.getKey(), entryProp.getValue()));
        }

        MObject mObject = new MObject(name, UUID.randomUUID().toString(), properties);
        objectData.add(mObject);

        EngineValue root = new EngineValue(name, ContentType.Object, objectData);
        response.add(root);
        return response;
    }
}

