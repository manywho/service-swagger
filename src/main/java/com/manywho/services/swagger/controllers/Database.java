package com.manywho.services.swagger.controllers;

import com.manywho.sdk.api.run.elements.type.*;
import com.manywho.sdk.services.database.RawDatabase;
import com.manywho.services.swagger.ServiceConfiguration;
import com.manywho.services.swagger.factories.ManyWhoRelationMapperFactory;
import com.manywho.services.swagger.managers.DataManager;
import com.manywho.services.swagger.services.ManyWhoRelationMapperService;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Database implements RawDatabase<ServiceConfiguration> {
    private DataManager dataManager;
    private ManyWhoRelationMapperFactory manyWhoRelationServiceFactory;

    @Inject
    public Database(DataManager dataManager, ManyWhoRelationMapperFactory manyWhoRelationServiceFactory) {
        this.dataManager = dataManager;
        this.manyWhoRelationServiceFactory = manyWhoRelationServiceFactory;
    }

    @Override
    public MObject create(ServiceConfiguration configuration, MObject object) {
        try {
            ManyWhoRelationMapperService relationService = manyWhoRelationServiceFactory.createManyWhoRelationMapper(configuration);

            dataManager.save(configuration, object, relationService.getUrlCreateVerb(object.getDeveloperName()),
                    relationService.getUrlToCreate(object.getDeveloperName()), relationService.getExternalIdName(object.getDeveloperName()));

            Optional<Property> property = object.getProperties().stream()
                    .filter(p -> StringUtils.equals(p.getDeveloperName(), relationService.getExternalIdName(object.getDeveloperName())))
                    .findFirst();

            if (property.isPresent()) {
                return dataManager.find(configuration, object.getDeveloperName(),
                        relationService.getUrlLoadVerb(object.getDeveloperName()),
                        relationService.getUrlLoad(object.getDeveloperName(), property.get().getContentValue()),
                        relationService.getExternalIdName(object.getDeveloperName()));
            } else {
                throw new RuntimeException("External Id not found");
            }
        } catch (Exception e) {
            throw new RuntimeException("problem creating object" + e.getMessage());
        }
    }


    @Override
    public List<MObject> create(ServiceConfiguration configuration, List<MObject> objects) {
        return null;
    }

    @Override
    public void delete(ServiceConfiguration configuration, MObject object) {
        // todo delete object
        return;
    }

    @Override
    public void delete(ServiceConfiguration configuration, List<MObject> objects) {
        //todo delete list of object;

        return;
    }

    @Override
    public MObject find(ServiceConfiguration configuration, ObjectDataType objectDataType, String id) {
        ManyWhoRelationMapperService relationService = manyWhoRelationServiceFactory.createManyWhoRelationMapper(configuration);
        Optional<ObjectDataTypeProperty> property = objectDataType.getProperties().stream()
                .filter(p -> StringUtils.equalsIgnoreCase(p.getDeveloperName(), relationService.getExternalIdName(objectDataType.getDeveloperName())))
                .findFirst();

        if (property.isPresent()) {
            return dataManager.find(configuration, objectDataType.getDeveloperName(),
                    relationService.getUrlLoadVerb(objectDataType.getDeveloperName()),
                    relationService.getUrlLoad(objectDataType.getDeveloperName(), id), property.get().getDeveloperName());

        } else {
            throw new RuntimeException("External id not found");
        }
    }

    @Override
    public List<MObject> findAll(ServiceConfiguration configuration, ObjectDataType objectDataType, ListFilter filter) {
        try {
            List<MObject> mObjectList = new ArrayList<>();

            return mObjectList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public MObject update(ServiceConfiguration configuration, MObject object) {
        try {
            ManyWhoRelationMapperService relationService = manyWhoRelationServiceFactory.createManyWhoRelationMapper(configuration);

            dataManager.save(configuration, object, relationService.getUrlUpdateVerb(object.getDeveloperName()),
                    relationService.getUrlToUpdate(object.getDeveloperName(), object),
                    relationService.getExternalIdName(object.getDeveloperName()));

            Optional<Property> property = object.getProperties().stream()
                    .filter(p -> StringUtils.equals(p.getDeveloperName(), relationService.getExternalIdName(object.getDeveloperName())))
                    .findFirst();

            if (property.isPresent()) {
                return dataManager.find(configuration, object.getDeveloperName(),
                        relationService.getUrlLoadVerb(object.getDeveloperName()),
                        relationService.getUrlLoad(object.getDeveloperName(), property.get().getContentValue()),
                        relationService.getExternalIdName(object.getDeveloperName()));
            } else {
                throw new RuntimeException("External Id not found");
            }
        } catch (Exception e) {
            throw new RuntimeException("problem creating object" + e.getMessage());
        }
    }

    @Override
    public List<MObject> update(ServiceConfiguration configuration, List<MObject> objects) {
        return null;
    }

}